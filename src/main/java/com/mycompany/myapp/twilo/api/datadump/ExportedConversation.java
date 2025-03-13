package com.mycompany.myapp.twilo.api.datadump;

import com.twilio.rest.conversations.v1.Conversation;
import com.twilio.rest.conversations.v1.User;
import com.twilio.rest.conversations.v1.conversation.Message;
import com.twilio.rest.conversations.v1.conversation.Participant;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;

import java.util.List;

@Builder
@Getter
public class ExportedConversation {
    @NonNull
    private final Conversation conversation;
    @NonNull
    private final List<Message> messages;
    @NonNull
    private final List<Participant> participants;
    @NonNull
    private final List<User> users;
}