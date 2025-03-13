package org.tnmk.java_twilio_dump.twilo.api.config;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

@Getter
@Setter
public class TwilioConfig {
    @NonNull
    private String accountSid;
//    @NonNull
//    private String conversationServiceSid;
    @NonNull
    private String apiKey;
    @NonNull
    private String apiSecret;
}
