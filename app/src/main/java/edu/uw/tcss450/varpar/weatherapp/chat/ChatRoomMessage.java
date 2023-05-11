package edu.uw.tcss450.varpar.weatherapp.chat;

import java.io.Serializable;

/**
 * Class to encapsulate a chat message from a contact.
 *
 * @author Nathan Brown
 * @version 5/10/2023
 */
public class ChatRoomMessage implements Serializable {
    private final String mMessage;

    /**
     * Helper class for building messages.
     *
     * @author Charles Bryan
     */
    public static class Builder {
        private final String mMessage;


        /**
         * Constructs a new Builder.
         *
         * @param message the message string sent by the user in the ChatMessage
         */
        public Builder(String message) {
            this.mMessage = message;
        }

        public ChatRoomMessage build() {
            return new ChatRoomMessage(this);
        }

    }

    private ChatRoomMessage(final Builder builder) {
        this.mMessage = builder.mMessage;
    }

    public String getMessage() {
        return mMessage;
    }


}
