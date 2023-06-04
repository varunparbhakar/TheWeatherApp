package edu.uw.tcss450.varpar.weatherapp.home;

import java.io.Serializable;

/**
 * Class to encapsulate a chat message from a contact.
 * This class is used to model the chat preview which includes sender information.
 * It implements {@link Serializable} to allow the chat preview to be easily passed between activities or fragments.
 *
 * @author Jashanpreet Jandu
 * @version 1.0
 * @since 2023-06-03
 */
public class ChatPreview implements Serializable {

    private final String mUser;

    /**
     * Helper class to facilitate the construction of a {@link ChatPreview} instance.
     * The Builder design pattern makes it easier to create complex objects step by step.
     * It’s especially useful when you need to create an object with lots of possible configuration options.
     *
     * @author Charles Bryan
     */
    public static class Builder {

        private final String mUser;

        /**
         * Constructs a new Builder.
         *
         * @param user the sender of the ChatPreview
         */
        public Builder(String user) {
            this.mUser = user;
        }

        /**
         * Constructs a new {@link ChatPreview} instance with the configuration of this builder.
         *
         * @return a new instance of {@link ChatPreview}.
         */
        public ChatPreview build() {
            return new ChatPreview(this);
        }
    }

    /**
     * Constructs a new {@link ChatPreview} instance with the configuration of the provided builder.
     *
     * @param builder the builder to use.
     */
    private ChatPreview(final Builder builder) {
        this.mUser = builder.mUser;
    }

    /**
     * Returns the sender of the chat preview.
     *
     * @return the sender of the chat preview.
     */
    public String getUser() {
        return mUser;
    }
}
