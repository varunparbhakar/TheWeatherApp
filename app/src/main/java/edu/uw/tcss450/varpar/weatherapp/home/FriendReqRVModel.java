package edu.uw.tcss450.varpar.weatherapp.home;

/**
 * Class to encapsulate a chat message from a contact.
 * This is a model class for the friend request recycler view.
 * It is used to display the friend requests in the home fragment.
 * It is also used to handle the accept and decline buttons.
 *
 * @Author Jashanpreet Jandu & Deep Singh
 * @version 5/30/2023
 */
public class FriendReqRVModel {
    private String username;

    public FriendReqRVModel(String username) {
        this.username = username;
    }

    public String getName() {
        return username;
    }

    public void setName(String username) {
        this.username = username;
    }
}
