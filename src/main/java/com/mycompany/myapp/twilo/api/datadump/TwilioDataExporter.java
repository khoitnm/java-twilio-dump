package com.mycompany.myapp.twilo.api.datadump;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.mycompany.myapp.twilo.api.config.TwilioConfig;
import com.mycompany.myapp.twilo.api.config.TwilioConfigLoader;
import com.twilio.Twilio;
import com.twilio.base.Resource;
import com.twilio.base.ResourceSet;
import com.twilio.rest.conversations.v1.service.Conversation;
import com.twilio.rest.conversations.v1.service.User;
import com.twilio.rest.conversations.v1.service.conversation.Message;
import com.twilio.rest.conversations.v1.service.conversation.Participant;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ForkJoinPool;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Slf4j
public class TwilioDataExporter {
    private static final TwilioConfig config = TwilioConfigLoader.loadConfig("application-localqa.properties");

    static {
        Twilio.init(config.getApiKey(), config.getApiSecret(), config.getAccountSid());
    }

    public static void exportConversationsToJson(List<String> conversationSids, String outputFilePath) throws IOException {
        List<ExportedConversation> exportedConversations = new ArrayList<>(conversationSids.size());
        Map<String, User> userCache = new HashMap<>();

        for (String conversationSid : conversationSids) {
            if (conversationSid == null || conversationSid.trim().isBlank()) {
                continue;
            }

            Conversation conversation;
            try {
                conversation = Conversation.fetcher(config.getConversationServiceSid(), conversationSid).fetch();
            } catch (Exception e) {
                log.info("Error fetching conversation: " + conversationSid);
                continue;
            }

            ResourceSet<Message> messages = Message.reader(config.getConversationServiceSid(), conversationSid).read();
            List<Message> allMessages = getAllItemsInAutoPagingResourceSet(messages);

            ResourceSet<Participant> participants = Participant.reader(config.getConversationServiceSid(), conversationSid).read();
            List<Participant> allParticipants = getAllItemsInAutoPagingResourceSet(participants);

            List<User> allUsers = new ArrayList<>();
            for (Participant participant : allParticipants) {
                String identity = participant.getIdentity();
                User user = userCache.computeIfAbsent(identity,
                        userIdentity -> User.fetcher(config.getConversationServiceSid(), userIdentity).fetch()
                );
                allUsers.add(user);
            }

            ExportedConversation exportedConversation = ExportedConversation.builder()
                    .conversation(conversation)
                    .messages(allMessages)
                    .participants(allParticipants)
                    .users(allUsers)
                    .build();
            exportedConversations.add(exportedConversation);
        }

        ObjectMapper objectMapper = newObjectMapper();
        objectMapper.writeValue(new File(outputFilePath), exportedConversations);
    }

    private static <E extends Resource> List<E> getAllItemsInAutoPagingResourceSet(ResourceSet<E> resourceSet) {
        // We don't want too many parallelism to avoid exceeding Twilio rate limits.
        ForkJoinPool customThreadPool = new ForkJoinPool(3);
        try {
            // Note that Twilio has autoPaging resourceSet, so after it iterate through all items in the current page,
            // it will automatically fetch the next page and continue iterating.
            return customThreadPool.submit(() ->
                    StreamSupport.stream(resourceSet.spliterator(), true)
                            .collect(Collectors.toList())
            ).get();
        } catch (Exception e) {
            throw new RuntimeException("Error processing resource set in parallel", e);
        } finally {
            customThreadPool.shutdown();
        }
    }

    public static ObjectMapper newObjectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        // This will help convert modern date types in Java 8 (ZonedDateTime, Instant, etc.)
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        return objectMapper;
    }
}