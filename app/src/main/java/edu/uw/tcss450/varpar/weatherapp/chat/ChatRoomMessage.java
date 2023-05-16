package edu.uw.tcss450.varpar.weatherapp.chat;

import android.icu.text.SimpleDateFormat;

import java.io.Serializable;
import java.util.Date;

/**
 * Class to encapsulate a chat message from a contact.
 *
 * @author Nathan Brown
 * @version 5/10/2023
 */
public class ChatRoomMessage implements Serializable {

    private final int mMessageId;

    private final String mMessageBody;

    private final String mUser;

    private final String mTimestamp;

    public ChatRoomMessage(int messageId, String messageBody, String user) {
        this.mMessageId = messageId;
        this.mMessageBody = messageBody;
        this.mUser = user;
        this.mTimestamp = new SimpleDateFormat("HH.mm.ss").format(new Date());
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
