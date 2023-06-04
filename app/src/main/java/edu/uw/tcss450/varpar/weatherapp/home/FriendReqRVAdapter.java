package edu.uw.tcss450.varpar.weatherapp.home;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import edu.uw.tcss450.varpar.weatherapp.R;
import edu.uw.tcss450.varpar.weatherapp.contact.Contact;
import edu.uw.tcss450.varpar.weatherapp.databinding.FragmentHomeFrCardBinding;

/**
 * Class to encapsulate a chat message from a contact.
 *
 * @version 5/30/2023
 * @author Jashanpreet Jandu & Deep Singh
 */
public class FriendReqRVAdapter extends RecyclerView.Adapter<FriendReqRVAdapter.ViewHolder> {

    /** Context. */
    private Context context;

    /** Contact request list. */
    private ArrayList<Contact> mContacts;

    /** Reference to calling fragment. */
    private HomeFragment mFragment;

    /**
     * Constructor that takes list of contact requests.
     * @param cont context.
     * @param frag calling fragment.
     * @param contacts list of contacts.
     */
    public FriendReqRVAdapter(final Context cont, final HomeFragment frag, final ArrayList<Contact> contacts) {
        this.mContacts = contacts;
        this.context = cont;
        this.mFragment = frag;
    }

    @NonNull
    @Override
    public FriendReqRVAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.fragment_home_fr_card, parent, false);
        return new FriendReqRVAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FriendReqRVAdapter.ViewHolder holder, int position) {
        holder.setContact(mContacts.get(position));
    }

    @Override
    public int getItemCount() {
        return mContacts.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        /** Binding for view elements. */
        private final FragmentHomeFrCardBinding binding;

        /** Contact card for request. */
        private Contact mContact;

        /**
         * Constructor that takes item to bind view.
         * @param itemView view to bind.
         */
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = FragmentHomeFrCardBinding.bind(itemView);
        }

        /**
         * Dials out to fragment for request deletion.
         * @param button button that houses functionality.
         */
        private void deleteFR(final View button) {
            mFragment.getRemoveFriendRequests(mContact.getMemberID());
        }

        /**
         * Dials out to fragment for adding a chat with chosen friend.
         * @param button button that houses functionality.
         */
        private void acceptFR(final View button) {
            mFragment.getAcceptFriendRequests(mContact.getMemberID());
        }

        /**
         * Logic for buttons on card and name to display.
         * @param contact request to bind.
         */
        void setContact(final Contact contact) {
            mContact = contact;
            binding.idTVUser.setText(contact.getUsername());
            binding.idIVAccept.setOnClickListener(this::acceptFR);
            binding.idIVDecline.setOnClickListener(this::deleteFR);
        }
    }
}
