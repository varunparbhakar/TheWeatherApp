package edu.uw.tcss450.varpar.weatherapp.contact;

import java.io.Serializable;

/**
 * Class to encapsulate a chat message from a contact.
 * @author Nathan Brown, James Deal
 */
public class Contact implements Serializable, Comparable<Contact> {

    /** User's username. */
    private final String mUsername;

    /** User's memberID. */
    private final String mMemberID;

    private Contact(final Builder builder) {
        this.mUsername = builder.mUser;
        this.mMemberID = builder.mMemberID;
    }

    /**
     * Ability to compare users based on name.
     * @param o the object to be compared.
     * @return Normal alphabetic order.
     */
    @Override
    public int compareTo(Contact o) {
        return mUsername.compareTo(o.mUsername);
    }

    public String getUsername() {
        return mUsername;
    }

    public String getMemberID() {
        return mMemberID;
    }

    /**
     * Helper class for building messages.
     */
    public static class Builder {

        private final String mUser;

        private final String mMemberID;

        /**
         * Constructs a new Builder.
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
}
