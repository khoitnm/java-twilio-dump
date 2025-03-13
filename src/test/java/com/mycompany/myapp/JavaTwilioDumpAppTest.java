package com.mycompany.myapp;

import org.junit.jupiter.api.Test;

import java.io.IOException;

class JavaTwilioDumpAppTest {
    @Test
    public void testMain() throws IOException {
        // Arrange
        String[] args = new String[2];
        args[0] = "C:\\dev\\workspace\\personal\\java-twilio-dump\\src\\main\\resources\\input-conversations.txt";
        args[1] = "C:\\dev\\workspace\\personal\\java-twilio-dump\\target\\output.json";
        // Act
        JavaTwilioDumpApp.main(args);
        // Assert
    }
}