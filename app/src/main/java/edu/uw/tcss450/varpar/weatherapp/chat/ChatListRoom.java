package edu.uw.tcss450.varpar.weatherapp.chat;

import java.io.Serializable;

/**
 * Class to encapsulate a chat.
 *
 * @author Nathan Brown
 * @version 4/30/2023
 */
public class ChatListRoom implements Serializable {

    /** ID of chat. */
    private final String mChatId;

    /** Name of chat. */
    private final String mName;

    /**
     * Contructor to set values to display.
     * @param chatId id of chat.
     * @param name name of chat.
     */
    public ChatListRoom(final String chatId, final String name) {
        this.mChatId = chatId;
        this.mName = name;
    }

    /**
     * Get ID of chat.
     * @return String ID.
     */
    public String getChatId() {
        return mChatId;
    }

    /**
     * Get name of chat.
     * @return String name.
     */
    public String getName() {
        return mName;
    }


}
