package edu.uw.tcss450.varpar.weatherapp.contact;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import edu.uw.tcss450.varpar.weatherapp.R;
import edu.uw.tcss450.varpar.weatherapp.databinding.FragmentContactCardBinding;

/**
 * Visual logic for Contacts and ContactList.
 * @author Nathan Brown, James Deal
 */
public class ContactRecyclerViewAdapter extends RecyclerView.Adapter<ContactRecyclerViewAdapter.ContactViewHolder> {

    /** Store all of the Contacts to present. */
    private final List<Contact> mContacts;

    /** Store copy all of the Contacts to present for safe search. */
    private List<Contact> mContactsCopy;

    /** Host fragment to bounce view actions to. */
    private final ContactListFragment mFragment;

    public ContactRecyclerViewAdapter(List<Contact> items, ContactListFragment frag) {
        this.mContacts = items;
        this.mContactsCopy = new ArrayList<>();
        this.mContactsCopy.addAll(items);
        this.mFragment = frag;
    }

    /**
     * Filter list contents during live search.
     * Refresh to full list once search is empty.
     * @param text current search text.
     */
    public void filter(String text) {
        mContacts.clear();
        if(text.isEmpty()){
            mContacts.addAll(mContactsCopy);
        } else{
            text = text.toLowerCase();
            for(Contact item: mContactsCopy){
                if(item.getUsername().toLowerCase().contains(text)){
                    mContacts.add(item);
                }
            }
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ContactViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ContactViewHolder(LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.fragment_contact_card, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ContactViewHolder holder, int position) {
        holder.setContact(mContacts.get(position));
    }

    @Override
    public int getItemCount() {
        return mContacts.size();
    }

    /**
     * Objects from this class represent an Individual row View from the List
     * of rows in the Contact Recycler View.
     */
    public class ContactViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public FragmentContactCardBinding binding;
        private Contact mContact;

        public ContactViewHolder(View view) {
            super(view);
            mView = view;
            binding = FragmentContactCardBinding.bind(view);
        }

        /**
         * Dials out to fragment for friend deletion.
         * @param button button that houses functionality.
         */
        private void deleteUser(final View button) {
            mFragment.deleteContact(mContact.getMemberID());
        }

        /**
         * Dials out to fragment for adding a chat with chosen friend.
         * @param button button that houses functionality.
         */
        private void addChat(final View button) {
            mFragment.addContactChat(mContact.getMemberID());
        }

        void setContact(final Contact contact) {
            mContact = contact;
            binding.textContactUser.setText(contact.getUsername());
            binding.deleteUser.setOnClickListener(this::deleteUser);
            binding.contactUser.setOnClickListener(this::addChat);
        }
    }
}
