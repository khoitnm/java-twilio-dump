package com.mycompany.myapp;

import com.mycompany.myapp.twilo.api.datadump.TwilioDataExporter;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

@Slf4j
public class ConversationCreatorApp {
    /**
     * And example command line:
     * java -cp target/myapp-1.0-SNAPSHOT.jar com.mycompany.myapp.ConversationCreatorApp inputFilePath.txt outputFilePath.json
     */
    public static void main(String[] args) throws IOException {
        String inputFilePath = args[0];
        log.info("Reading conversationSids from file: {} ...", inputFilePath);
        List<String> conversationSids = Files.readAllLines(Paths.get(inputFilePath));
        log.info("Found {} conversationSids.", conversationSids.size());

        String outputFilePath = args[1];
        log.info("Exporting conversations to JSON file: {} ...", outputFilePath);
        TwilioDataExporter.exportConversationsToJson(conversationSids, outputFilePath);
    }
}  
