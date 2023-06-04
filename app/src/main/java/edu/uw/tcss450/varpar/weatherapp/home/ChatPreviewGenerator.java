package edu.uw.tcss450.varpar.weatherapp.home;

import java.util.Arrays;
import java.util.List;

import edu.uw.tcss450.varpar.weatherapp.contact.Contact;

/**
 * This class provides a set of static methods to generate and retrieve
 * pre-constructed instances of Contact objects for the chat feature.
 * This class should not be instantiated.
 *
 * @author Jashanpreet Jandu
 * @version 1.0
 * @since 2023-06-03
 */
public class ChatPreviewGenerator {

    /**
     * A fixed array of Contact objects.
     */
    private static final Contact[] CONTACTS;

    /**
     * Constant representing the number of Contact instances to create.
     */
    public static final int COUNT = 20;


    // Static block to populate the CONTACTS array
    static {
        CONTACTS = new Contact[COUNT];
        for (int i = 0; i < CONTACTS.length; i++) {
            CONTACTS[i] = new Contact
                    .Builder("User_" + i, String.valueOf(i))
                    .build();
        }
    }

    /**
     * Returns the list of all Contact instances.
     *
     * @return a List of Contact objects
     */
    public static List<Contact> getContactList() {
        return Arrays.asList(CONTACTS);
    }

    /**
     * Returns a copy of the array of all Contact instances.
     *
     * @return an array of Contact objects
     */
    public static Contact[] getContacts() {
        return Arrays.copyOf(CONTACTS, CONTACTS.length);
    }

    // Private constructor to prevent instantiation
    private ChatPreviewGenerator() {
    }

    /**
     * Returns the list of the first four most recent Contact instances.
     *
     * @return a List of the four most recent Contact objects
     */
    public static List<Contact> getRecentChats() {
        return Arrays.asList(Arrays.copyOfRange(CONTACTS, 0, 4));
    }
}
