package edu.uw.tcss450.varpar.weatherapp.home;

import java.io.Serializable;

/**
 * Class to encapsulate a chat message from a contact.
 *
 * @author Jashanpreet Jandu
 * @version 5/01/2023
 */
public class ChatPreview implements Serializable {

    //    private final String mTimestamp;
    private final String mUser;

    /**
     * Helper class for building messages.
     *
     * @author Charles Bryan
     */
    public static class Builder {

        private final String mUser;

        /**
         * Constructs a new Builder.
         *
         * @param user the sender of the ChatMessage
         */
        public Builder(String user) {
            this.mUser = user;
        }

        public ChatPreview build() {
            return new ChatPreview(this);
        }
    }

    /**
     * Helper class for building messages.
     *
     * @param builder the builder to use.
     */
    private ChatPreview(final Builder builder) {
        this.mUser = builder.mUser;
    }

    /**
     * Get the sender of the message.
     *
     * @return the sender.
     */
    public String getUser() {
        return mUser;
    }
}
