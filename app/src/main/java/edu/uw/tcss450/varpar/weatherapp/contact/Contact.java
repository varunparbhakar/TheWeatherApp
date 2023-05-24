package edu.uw.tcss450.varpar.weatherapp.contact;

import java.io.Serializable;

/**
 * Class to encapsulate a chat message from a contact.
 *
 * @author Nathan Brown
 * @version 4/30/2023
 */
public class Contact implements Serializable, Comparable<Contact> {

    private final String mUser;

    @Override
    public int compareTo(Contact o) {
        return mUser.compareTo(o.mUser);
    }

    public String getUser() {
        return mUser;
    }

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

        public Contact build() {
            return new Contact(this);
        }

    }

    private Contact(final Builder builder) {
        this.mUser = builder.mUser;
    }

}
