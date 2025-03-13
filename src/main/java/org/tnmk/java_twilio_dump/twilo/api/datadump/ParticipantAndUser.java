package org.tnmk.java_twilio_dump.twilo.api.datadump;

import com.twilio.rest.conversations.v1.User;
import com.twilio.rest.conversations.v1.conversation.Participant;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class ParticipantAndUser {
    private final Participant participant;
    private final User user;
}
