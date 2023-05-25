package edu.uw.tcss450.varpar.weatherapp.chat;

import android.icu.text.SimpleDateFormat;

import androidx.annotation.Nullable;

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

    private final String mSender;

    private final String mTimestamp;

    public ChatRoomMessage(int messageId, String messageBody, String user) {
        this.mMessageId = messageId;
        this.mMessageBody = messageBody;
        this.mSender = user;
        this.mTimestamp = new SimpleDateFormat("HH.mm.ss").format(new Date());
    }

    public int getMessageId() {
        return mMessageId;
    }

    public String getMessage() {
        return mMessageBody;
    }

    public String getSender() {
        return mSender;
    }

    public String getTimestamp() {
        return mTimestamp;
    }

    /**
     * Provides equality solely based on MessageId.
     * @param other the other object to check for equality
     * @return true if other message ID matches this message ID, false otherwise
     */
    @Override
    public boolean equals(@Nullable Object other) {
        boolean result = false;
        if (other instanceof ChatRoomMessage) {
            result = mMessageId == ((ChatRoomMessage) other).mMessageId;
        }
        return result;
    }


}
