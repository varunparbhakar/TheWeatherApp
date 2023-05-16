package edu.uw.tcss450.varpar.weatherapp.chat;

import java.io.Serializable;

/**
 * Class to encapsulate a chat message from a contact.
 *
 * @author Nathan Brown
 * @version 5/10/2023
 */
public class ChatRoomRecyclerItem implements Serializable {

    private final int mMessageId;

    private final String mMessageBody;

    private final String mUser;

    private final String mTimestamp;

    public ChatRoomRecyclerItem(int messageId, String messageBody, String user, String timestamp) {
        this.mMessageId = messageId;
        this.mMessageBody = messageBody;
        this.mUser = user;
        this.mTimestamp = timestamp;
    }

    public int getMessageId() {
        return mMessageId;
    }

    public String getMessage() {
        return mMessageBody;
    }

    public String getUser() {
        return mUser;
    }

    public String getTimestamp() {
        return mTimestamp;
    }


}
