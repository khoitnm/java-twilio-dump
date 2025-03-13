package org.tnmk.java_twilio_dump.twilo.api.datadump;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.twilio.base.Resource;
import com.twilio.base.ResourceSet;
import com.twilio.rest.conversations.v1.Conversation;
import com.twilio.rest.conversations.v1.User;
import com.twilio.rest.conversations.v1.conversation.Message;
import com.twilio.rest.conversations.v1.conversation.Participant;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Slf4j
public class TwilioDataExporter {
    public static final int BATCH_SIZE = 500;
    public static final int USER_CACHE_SIZE = 10000;
    public static final int PARALLEL_CONVERSATIONS = 5;
    public static final ObjectMapper objectMapper = newObjectMapper();

    public static void exportConversationsToJson(List<String> conversationSids, String outputFilePath) throws IOException {
        int totalBatches = (int) Math.ceil((double) conversationSids.size() / BATCH_SIZE);
        ExecutorService executorService = Executors.newFixedThreadPool(PARALLEL_CONVERSATIONS);

        for (int batchIndex = 0; batchIndex < totalBatches; batchIndex++) {
            int start = batchIndex * BATCH_SIZE;
            int end = Math.min(start + BATCH_SIZE, conversationSids.size());
            int logBatchIndex = batchIndex + 1;
            log.info("Batch {} of {} [{} - {}] Exporting conversations ...", logBatchIndex, totalBatches, start, end);
            List<String> batchConversationSids = conversationSids.subList(start, end);

            // We have to use concurrentHashMap because we are working on multiple threads.
            /**
             * key: user identity == participant identity
             * value: user
             */
            final Map<String, User> usersCache = new ConcurrentHashMap<>(USER_CACHE_SIZE);

            List<Future<ExportedConversation>> futures = new ArrayList<>();
            for (String conversationSid : batchConversationSids) {
                if (conversationSid == null || conversationSid.trim().isBlank()) {
                    continue;
                }
                futures.add(executorService.submit(() -> exportConversation(conversationSid, usersCache)));
            }

            List<ExportedConversation> exportedConversations = new ArrayList<>(batchConversationSids.size());
            for (Future<ExportedConversation> future : futures) {
                try {
                    exportedConversations.add(future.get());
                } catch (InterruptedException | ExecutionException e) {
                    log.error("Batch {} of {}: Error fetching conversation", logBatchIndex, totalBatches, e);
                }
            }

            String batchOutputFilePath = outputFilePath.replace(".json", "_" + batchIndex + ".json");
            File batchFile = new File(batchOutputFilePath);
            objectMapper.writeValue(batchFile, exportedConversations);
            log.info("Batch {} of {}: Generated JSON file: {}", logBatchIndex, totalBatches, batchOutputFilePath);
        }

        executorService.shutdown();
    }

    private static ExportedConversation exportConversation(String conversationSid, Map<String, User> userCache) {
        Conversation conversation = Conversation.fetcher(conversationSid).fetch();
        ResourceSet<Message> messages = Message.reader(conversationSid).read();
        List<Message> allMessages = getAllItemsInAutoPagingResourceSet(messages);

        ResourceSet<Participant> participants = Participant.reader(conversationSid).read();
        List<Participant> allParticipants = getAllItemsInAutoPagingResourceSet(participants);

        List<ParticipantAndUser> participantAndUsers = new ArrayList<>();
        for (Participant participant : allParticipants) {
            String identity = participant.getIdentity();
            User user = userCache.computeIfAbsent(identity, userIdentity -> User.fetcher(userIdentity).fetch());
            ParticipantAndUser participantAndUser = ParticipantAndUser.builder().participant(participant).user(user).build();
            participantAndUsers.add(participantAndUser);
        }

        ExportedConversation exportedConversation = ExportedConversation.builder().conversation(conversation).messages(allMessages).participantAndUsers(participantAndUsers).build();
        return exportedConversation;
    }

    private static <E extends Resource> List<E> getAllItemsInAutoPagingResourceSet(ResourceSet<E> resourceSet) {
        // Note that Twilio has autoPaging resourceSet, so after it iterate through all items in the current page,
        // it will automatically fetch the next page and continue iterating.
        return StreamSupport.stream(resourceSet.spliterator(), false).collect(Collectors.toList());
    }

    public static ObjectMapper newObjectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        // This will help convert modern date types in Java 8 (ZonedDateTime, Instant, etc.)
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        return objectMapper;
    }
}