package com.mycompany.myapp.twilo.api.datadump;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mycompany.myapp.twilo.api.config.TwilioConfig;
import com.mycompany.myapp.twilo.api.config.TwilioConfigLoader;
import com.twilio.Twilio;
import com.twilio.base.Resource;
import com.twilio.base.ResourceSet;
import com.twilio.rest.conversations.v1.service.Conversation;
import com.twilio.rest.conversations.v1.service.conversation.Message;
import com.twilio.rest.conversations.v1.service.conversation.Participant;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ForkJoinPool;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class TwilioDataExporter {
    private static final TwilioConfig config = TwilioConfigLoader.loadConfig("application-localkevin.properties");
    static {
        Twilio.init(config.getApiKey(), config.getApiSecret(), config.getAccountSid());
    }

    public static void exportConversationsToJson(List<String> conversationSids, String outputFilePath) throws IOException, IOException {
        List<ExportedConversation> exportedConversations = new ArrayList<>(conversationSids.size());

        for (String conversationSid : conversationSids) {
            if (conversationSid == null || conversationSid.trim().isBlank()) {
                continue;
            }

            Conversation conversation = Conversation.fetcher(config.getConversationServiceSid(), conversationSid).fetch();

            ResourceSet<Message> messages = Message.reader(config.getConversationServiceSid(), conversationSid).read();
            List<Message> allMessages = getAllItemsInAutoPagingResourceSet(messages);

            ResourceSet<Participant> participants = Participant.reader(config.getConversationServiceSid(), conversationSid).read();
            List<Participant> allParticipants = getAllItemsInAutoPagingResourceSet(participants);

            // TODO for each participants, find corresponding User data.

            ExportedConversation exportedConversation = ExportedConversation.builder()
                    .conversation(conversation)
                    .messages(allMessages)
                    .participants(allParticipants)
                    .build();
            exportedConversations.add(exportedConversation);
        }

        ObjectMapper objectMapper = new ObjectMapper();
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


}