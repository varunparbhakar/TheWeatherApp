package edu.uw.tcss450.varpar.weatherapp.chat;

import java.io.Serializable;

/**
 * Class to encapsulate a chat message from a contact.
 *
 * @author Nathan Brown
 * @version 4/30/2023
 */
public class ChatListRoom implements Serializable {


    private final int mChatId;
    //    private final String mTimestamp;
    private final String mUser;
    private final String mMessage;

    public ChatListRoom(int chatId, String user, String message) {
        this.mChatId = chatId;
        this.mUser = user;
        this.mMessage = message;
    }

    public String getUser() {
        return mUser;
    }

    public String getMessage() {
        return mMessage;
    }


}
