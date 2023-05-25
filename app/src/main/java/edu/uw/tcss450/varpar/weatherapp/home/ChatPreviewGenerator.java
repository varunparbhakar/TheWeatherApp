package edu.uw.tcss450.varpar.weatherapp.home;

import java.util.Arrays;
import java.util.List;

import edu.uw.tcss450.varpar.weatherapp.contact.Contact;

public class ChatPreviewGenerator {

    private static final Contact[] CONTACTS;
    public static final int COUNT = 20;


    static {
        CONTACTS = new Contact[COUNT];
        for (int i = 0; i < CONTACTS.length; i++) {
            CONTACTS[i] = new Contact
                    .Builder("User_" + i, String.valueOf(i))
                    .build();
        }
    }

    public static List<Contact> getContactList() {
        return Arrays.asList(CONTACTS);
    }

    public static Contact[] getContacts() {
        return Arrays.copyOf(CONTACTS, CONTACTS.length);
    }

    private ChatPreviewGenerator() {
    }

    public static List<Contact> getRecentChats() {
        return Arrays.asList(Arrays.copyOfRange(CONTACTS, 0, 4));
    }
}