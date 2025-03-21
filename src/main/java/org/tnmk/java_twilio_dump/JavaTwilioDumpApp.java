package org.tnmk.java_twilio_dump;

import org.tnmk.java_twilio_dump.twilo.api.config.TwilioConfig;
import org.tnmk.java_twilio_dump.twilo.api.config.TwilioConfigLoader;
import org.tnmk.java_twilio_dump.twilo.api.datadump.EncryptionUtil;
import org.tnmk.java_twilio_dump.twilo.api.datadump.TwilioDataExporter;
import com.twilio.Twilio;
import lombok.extern.slf4j.Slf4j;

import javax.crypto.SecretKey;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

@Slf4j
public class JavaTwilioDumpApp {
    
    public static void main(String[] args) throws Exception {
        String configurationFilePath = args[0];
        TwilioConfig config = TwilioConfigLoader.loadConfig(configurationFilePath);
        Twilio.init(config.getApiKey(), config.getApiSecret(), config.getAccountSid());

        String inputFilePath = args[1];
        log.info("Reading conversationSids from file: {} ...", inputFilePath);
        List<String> conversationSids = Files.readAllLines(Paths.get(inputFilePath));
        log.info("Found {} conversationSids.", conversationSids.size());

        String outputFilePath = args[2];
        log.info("Exporting conversations to JSON file: {} ...", outputFilePath);

        SecretKey secretKey = EncryptionUtil.generateKey();
        TwilioDataExporter.exportConversationsToJson(conversationSids, outputFilePath, secretKey);
    }
}  
