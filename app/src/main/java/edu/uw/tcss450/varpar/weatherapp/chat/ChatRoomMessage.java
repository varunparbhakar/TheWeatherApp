package edu.uw.tcss450.varpar.weatherapp.chat;

import android.icu.text.SimpleDateFormat;

import androidx.annotation.Nullable;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.Date;

/**
 * Class to encapsulate a chat message from a contact.
 *
 * @author Nathan Brown
 * @version 5/10/2023
 */
public class ChatRoomMessage implements Serializable {

    /** ID of message. */
    private final int mMessageId;

    /** Body of message. */
    private final String mMessageBody;

    /** Message sender. */
    private final String mSender;

    /** Timestamp of when message was sent. */
    private final String mTimestamp;

    /**
     * Constructor for each message.
     * @param messageId id of message.
     * @param messageBody body of message.
     * @param user user that sent message.
     */
    public ChatRoomMessage(final int messageId, final String messageBody, final String user) {
        this.mMessageId = messageId;
        this.mMessageBody = messageBody;
        this.mSender = user;
        this.mTimestamp = new SimpleDateFormat("HH.mm.ss").format(new Date());
    }

    /**
     * Static factory method to turn a properly formatted JSON String into a
     * ChatMessage object.
     * @param cmAsJson the String to be parsed into a ChatMessage Object.
     * @return a ChatMessage Object with the details contained in the JSON String.
     * @throws JSONException when cmAsString cannot be parsed into a ChatMessage.
     */
    public static ChatRoomMessage createFromJsonString(final String cmAsJson) throws JSONException {
        final JSONObject msg = new JSONObject(cmAsJson);
        return new ChatRoomMessage(
                msg.getInt("messageid"),
                msg.getString("message"),
                msg.getString("email")
        );
    }

    /**
     * Get ID of message.
     * @return ID as int.
     */
    public int getMessageId() {
        return mMessageId;
    }

    /**
     * Get message body.
     * @return String of body.
     */
    public String getMessage() {
        return mMessageBody;
    }

    /**
     * Get message sender.
     * @return String name of sender.
     */
    public String getSender() {
        return mSender;
    }

    /**
     * Get timestamp of message.
     * @return String of date.
     */
    public String getTimestamp() {
        return mTimestamp;
    }

    /**
     * Provides equality solely based on MessageId.
     * @param other the other object to check for equality
     * @return true if other message ID matches this message ID, false otherwise
     */
    @Override
    public boolean equals(final @Nullable Object other) {
        boolean result = false;
        if (other instanceof ChatRoomMessage) {
            result = mMessageId == ((ChatRoomMessage) other).mMessageId;
        }
        return result;
    }
}
