package edu.uw.tcss450.varpar.weatherapp.chat;

import java.io.Serializable;

/**
 * Class to encapsulate a chat message from a contact.
 *
 * @author Nathan Brown
 * @version 4/30/2023
 */
public class ChatListRecyclerItem implements Serializable {

    //    private final String mTimestamp;
    private final String mUser;
    private final String mMessage;

    /**
     * Helper class for building messages.
     *
     * @author Charles Bryan
     */
    public static class Builder {

        private final String mUser;
        private final String mMessage;


        /**
         * Constructs a new Builder.
         *
         * @param user the sender of the ChatMessage
         * @param message the message string sent by the user in the ChatMessage
         */
        public Builder(String user, String message) {
            this.mUser = user;
            this.mMessage = message;
        }

        public ChatListRecyclerItem build() {
            return new ChatListRecyclerItem(this);
        }

    }

    private ChatListRecyclerItem(final Builder builder) {
        this.mUser = builder.mUser;
        this.mMessage = builder.mMessage;
    }

    public String getUser() {
        return mUser;
    }

    public String getMessage() {
        return mMessage;
    }


}
