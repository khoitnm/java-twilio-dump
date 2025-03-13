package org.tnmk.java_twilio_dump;

import org.junit.jupiter.api.Test;

import java.io.IOException;

class JavaTwilioDumpAppTest {
    @Test
    public void testMain() throws IOException {
        // Arrange
        String[] args = new String[3];

        args[0] = "C:\\dev\\workspace\\personal\\java-twilio-dump\\src\\main\\resources\\application-localqa.properties";
        args[1] = "C:\\dev\\workspace\\personal\\java-twilio-dump\\src\\main\\resources\\input-conversations.txt";
        args[2] = "C:\\dev\\workspace\\personal\\java-twilio-dump\\target\\output.json";
        // Act
        JavaTwilioDumpApp.main(args);
        // Assert
    }
}