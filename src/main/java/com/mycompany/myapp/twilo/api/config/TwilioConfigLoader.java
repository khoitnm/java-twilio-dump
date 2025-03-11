package com.mycompany.myapp.twilo.api.config;

import com.mycompany.myapp.ConversationCreatorApp;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class TwilioConfigLoader {

    public static TwilioConfig loadConfig(String filePath) {
        Properties properties = new Properties();
        try (InputStream inputStream = ConversationCreatorApp.class.getClassLoader().getResourceAsStream(filePath)) {
            properties.load(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        TwilioConfig config = new TwilioConfig();
        config.setAccountSid(properties.getProperty("twilio.account.sid"));
        config.setConversationServiceSid(properties.getProperty("twilio.conversation-service-sid"));
        config.setApiKey(properties.getProperty("twilio.api.key"));
        config.setApiSecret(properties.getProperty("twilio.api.secret"));
        return config;
    }
}
