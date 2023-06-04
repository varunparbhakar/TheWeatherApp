package edu.uw.tcss450.varpar.weatherapp.home;

/**
 * Class to encapsulate a chat message from a contact.
 * This is a model class for the friend request recycler view.
 * It is used to display the friend requests in the home fragment.
 * It is also used to handle the accept and decline buttons.
 *
 * The class provides getter and setter for the username of the friend request.
 *
 * @author Jashanpreet Jandu & Deep Singh
 * @version 1.0
 * @since 2023-06-03
 */
public class FriendReqRVModel {
    private String username;

    /**
     * Constructs a new FriendReqRVModel with the given username.
     *
     * @param username the username of the friend request.
     */
    public FriendReqRVModel(String username) {
        this.username = username;
    }

    /**
     * Returns the username of the friend request.
     *
     * @return the username of the friend request.
     */
    public String getName() {
        return username;
    }

    /**
     * Sets the username of the friend request.
     *
     * @param username the new username of the friend request.
     */
    public void setName(String username) {
        this.username = username;
    }
}
