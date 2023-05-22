package edu.uw.tcss450.varpar.weatherapp.chat;

import java.util.Arrays;
import java.util.List;

public class ChatListRoomPreviewGenerator {

    private static final ChatListRoom[] CHATS;
    public static final int COUNT = 20;


    static {
        CHATS = new ChatListRoom[COUNT];
        for (int i = 0; i < CHATS.length; i++) {
            CHATS[i] = new ChatListRoom(
                    i,
                    "User_" + i,
                    "Lorem ipsum dolor sit amet, consectetuer adipiscing elit. Aenean commodo ligula eget dolor. Aenean massa. Cum sociis natoque penatibus et."
            );
        }
    }

    public static List<ChatListRoom> getChatList() {
        return Arrays.asList(CHATS);
    }

    public static ChatListRoom[] getChats() {
        return Arrays.copyOf(CHATS, CHATS.length);
    }

    private ChatListRoomPreviewGenerator() { }



}
