package edu.uw.tcss450.varpar.weatherapp.chat;

import java.util.Arrays;
import java.util.List;

import edu.uw.tcss450.varpar.weatherapp.R;

public class ChatGenerator {

    private static final ChatMessage[] CHATS;
    public static final int COUNT = 20;


    static {
        CHATS = new ChatMessage[COUNT];
        for (int i = 0; i < CHATS.length; i++) {
            CHATS[i] = new ChatMessage
                    .Builder("User_" + i, "Lorem ipsum dolor sit amet, consectetuer adipiscing elit. Aenean commodo ligula eget dolor. Aenean massa. Cum sociis natoque penatibus et.")
                    .build();
        }
    }

    public static List<ChatMessage> getChatList() {
        return Arrays.asList(CHATS);
    }

    public static ChatMessage[] getChats() {
        return Arrays.copyOf(CHATS, CHATS.length);
    }

    private ChatGenerator() { }



}
