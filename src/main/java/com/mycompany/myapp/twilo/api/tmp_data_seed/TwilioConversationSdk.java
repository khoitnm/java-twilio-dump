package com.mycompany.myapp.twilo.api.tmp_data_seed;

import com.twilio.http.TwilioRestClient;
import com.twilio.rest.conversations.v1.Conversation;
import com.twilio.rest.conversations.v1.conversation.Message;

import static com.mycompany.myapp.twilo.api.config.TwilioConfigLoader.getClient;

public class TwilioConversationSdk {
    public static void createConversations() {
        //This example shows that we can handle different Twilio accounts in the same application.
        TwilioRestClient client1 = getClient("application-localqa.properties");
        TwilioRestClient client2 = getClient("application-localkevin.properties");

        Conversation conversation = Conversation.creator()
                .setFriendlyName("My Conversation in org 1")
                .create(client1);
        System.out.println("Conversation1 SID: " + conversation.getSid());

        Message message = Message.creator(conversation.getSid())
                .setBody("Hello, World!")
                .create(client1);
        System.out.println("message SID: " + message.getSid());

        Conversation conversation2 = Conversation.creator()
                .setFriendlyName("My Conversation in org 2")
                .create(client2);
        System.out.println("Conversation2 SID: " + conversation2.getSid());
    }


}
