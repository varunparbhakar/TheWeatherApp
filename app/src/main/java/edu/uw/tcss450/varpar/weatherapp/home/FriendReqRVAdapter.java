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
    private Context context;
    private ArrayList<Contact> mContacts;
    private HomeFragment mFragment;

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
        private FragmentHomeFrCardBinding binding;
        private Contact mContact;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = FragmentHomeFrCardBinding.bind(itemView);
        }

        /**
         * Dials out to fragment for friend deletion.
         *
         * @param button button that houses functionality.
         */
        private void deleteFR(final View button) {
            mFragment.getRemoveFriendRequests(mContact.getMemberID());
        }

        /**
         * Dials out to fragment for adding a chat with chosen friend.
         *
         * @param button button that houses functionality.
         */
        private void acceptFR(final View button) {
            mFragment.getAcceptFriendRequests(mContact.getMemberID());
        }

        void setContact(final Contact contact) {
            mContact = contact;
            binding.idTVUser.setText(contact.getUsername());
            binding.idIVAccept.setOnClickListener(this::acceptFR);
            binding.idIVDecline.setOnClickListener(this::deleteFR);
        }
    }
}
