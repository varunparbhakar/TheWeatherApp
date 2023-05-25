package edu.uw.tcss450.varpar.weatherapp.contact;

import java.io.Serializable;

/**
 * Class to encapsulate a chat message from a contact.
 *
 * @author Nathan Brown
 * @version 4/30/2023
 */
public class Contact implements Serializable, Comparable<Contact> {

    private final String mUsername;

    private final String mMemberID;

    @Override
    public int compareTo(Contact o) {
        return mUsername.compareTo(o.mUsername);
    }

    public String getUsername() {
        return mUsername;
    }

    /**
     * Helper class for building messages.
     *
     * @author Charles Bryan
     */
    public static class Builder {

        private final String mUser;

        private final String mMemberID;

        /**
         * Constructs a new Builder.
         *
         * @param user the sender of the ChatMessage
         */
        public Builder(String user, String id) {
            this.mUser = user;
            this.mMemberID = id;
        }

        public Contact build() {
            return new Contact(this);
        }

    }

    private Contact(final Builder builder) {
        this.mUsername = builder.mUser;
        this.mMemberID = builder.mMemberID;
    }

}
