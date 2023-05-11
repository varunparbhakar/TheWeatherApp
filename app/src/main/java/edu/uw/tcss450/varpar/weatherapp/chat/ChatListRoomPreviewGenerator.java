package edu.uw.tcss450.varpar.weatherapp.chat;

import java.util.Arrays;
import java.util.List;

public class ChatListRoomPreviewGenerator {

    private static final ChatListRoomPreview[] CHATS;
    public static final int COUNT = 20;


    static {
        CHATS = new ChatListRoomPreview[COUNT];
        for (int i = 0; i < CHATS.length; i++) {
            CHATS[i] = new ChatListRoomPreview
                    .Builder("User_" + i, "Lorem ipsum dolor sit amet, consectetuer adipiscing elit. Aenean commodo ligula eget dolor. Aenean massa. Cum sociis natoque penatibus et.")
                    .build();
        }
    }

    public static List<ChatListRoomPreview> getChatList() {
        return Arrays.asList(CHATS);
    }

    public static ChatListRoomPreview[] getChats() {
        return Arrays.copyOf(CHATS, CHATS.length);
    }

    private ChatListRoomPreviewGenerator() { }



}
