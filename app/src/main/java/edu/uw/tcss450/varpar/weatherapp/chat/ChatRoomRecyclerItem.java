package edu.uw.tcss450.varpar.weatherapp.chat;

import java.io.Serializable;

/**
 * Class to encapsulate a chat message from a contact.
 *
 * @author Nathan Brown
 * @version 5/10/2023
 */
public class ChatRoomRecyclerItem implements Serializable {
    private final String mMessage;

    private final String mUser;

    /**
     * Helper class for building messages.
     *
     * @author Charles Bryan
     */
    public static class Builder {
        private final String mMessage;

        private final String mUser;

        /**
         * Constructs a new Builder.
         *
         * @param message the message string sent by the user in the ChatMessage
         */
        public Builder(String message, String user) {
            this.mMessage = message;
            this.mUser = user;
        }

        public ChatRoomRecyclerItem build() {
            return new ChatRoomRecyclerItem(this);
        }

    }

    private ChatRoomRecyclerItem(final Builder builder) {
        this.mMessage = builder.mMessage;
        this.mUser = builder.mUser;
    }

    public String getMessage() {
        return mMessage;
    }


}
