package com.mycompany.myapp.twilo.api.datadump;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mycompany.myapp.twilo.api.config.TwilioConfig;
import com.mycompany.myapp.twilo.api.config.TwilioConfigLoader;
import com.twilio.Twilio;
import com.twilio.base.ResourceSet;
import com.twilio.rest.conversations.v1.service.Conversation;
import com.twilio.rest.conversations.v1.service.conversation.Message;
import com.twilio.rest.conversations.v1.service.conversation.Participant;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class TwilioDataExporter {

    public static void exportConversationsToJson(List<String> conversationSids, String outputFilePath) throws IOException, IOException {
        TwilioConfig config = TwilioConfigLoader.loadConfig("application-localkevin.properties");
        Twilio.init(config.getApiKey(), config.getApiSecret(), config.getAccountSid());

        List<ExportedConversation> exportedConversations = new ArrayList<>(conversationSids.size());

        for (String conversationSid : conversationSids) {
            Conversation conversation = Conversation.fetcher(config.getConversationServiceSid(), conversationSid).fetch();

            ResourceSet<Message> messages = Message.reader(config.getConversationServiceSid(), conversationSid).read();

            ResourceSet<Participant> participants = Participant.reader(
                    config.getConversationServiceSid(), conversationSid).read();

            ExportedConversation exportedConversation = ExportedConversation.builder()
                    .conversation(conversation)
                    .messages(messages)
                    .participants(participants)
                    .build();
            exportedConversations.add(exportedConversation);
        }

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.writeValue(new File(outputFilePath), exportedConversations);
    }
}