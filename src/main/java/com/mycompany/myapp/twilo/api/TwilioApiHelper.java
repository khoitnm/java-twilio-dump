package com.mycompany.myapp.twilo.api;

import com.mycompany.myapp.twilo.api.config.TwilioConfig;
import com.mycompany.myapp.twilo.api.config.TwilioConfigLoader;
import org.apache.http.HttpMessage;

public class TwilioApiHelper {
    public static final String configFileName = "application-localkevin.properties";
    public static final TwilioConfig twilioConfig = TwilioConfigLoader.loadConfig(configFileName);

    public static void setHttpAuthorizationHeaders(HttpMessage httpMessage) {
        httpMessage.setHeader("Authorization", "Basic " + java.util.Base64.getEncoder().encodeToString(
                (twilioConfig.getApiKey() + ":" + twilioConfig.getApiSecret()).getBytes()));
    }

}
