package edu.uw.tcss450.varpar.weatherapp.chat;

import java.io.Serializable;

/**
 * Class to encapsulate a chat message from a contact.
 *
 * @author Nathan Brown
 * @version 4/30/2023
 */
public class ChatListRoom implements Serializable {


    private final String mChatId;
    //    private final String mTimestamp;
    private final String mName;

    public ChatListRoom(String chatId, String name) {
        this.mChatId = chatId;
        this.mName = name;
    }

    public String getChatId() {
        return mChatId;
    }

    public String getName() {
        return mName;
    }


}
