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
 * {@link RecyclerView.Adapter} that can display a {@link Contact} and makes a call to the
 * specified {@link HomeFragment}. This Adapter is meant to be used with a {@link RecyclerView}.
 * It encapsulates chat messages from a contact.
 *
 * @author Jashanpreet Jandu & Deep Singh
 * @version 1.0
 * @since 2023-06-03
 */
public class FriendReqRVAdapter extends RecyclerView.Adapter<FriendReqRVAdapter.ViewHolder> {
    private Context context;
    private ArrayList<Contact> mContacts;
    private HomeFragment mFragment;

    /**
     * Constructs a new FriendReqRVAdapter.
     *
     * @param cont the context for inflating the layout.
     * @param frag the fragment from where this adapter is instantiated.
     * @param contacts an ArrayList of Contact objects to display.
     */
    public FriendReqRVAdapter(final Context cont, final HomeFragment frag, final ArrayList<Contact> contacts) {
        this.mContacts = contacts;
        this.context = cont;
        this.mFragment = frag;
    }

    /**
     * Invoked when a new ViewHolder gets created.
     *
     * @param parent the ViewGroup that the new View will be added to after it is bound to an adapter position.
     * @param viewType the view type of the new View.
     * @return a new ViewHolder that holds a View of the given view type.
     */
    @NonNull
    @Override
    public FriendReqRVAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.fragment_home_fr_card, parent, false);
        return new FriendReqRVAdapter.ViewHolder(view);
    }

    /**
     * Invoked by RecyclerView to display the data at the specified position.
     *
     * @param holder the ViewHolder to be updated to represent the contents of the item at the given position in the data set.
     * @param position the position of the item within the adapter's data set.
     */
    @Override
    public void onBindViewHolder(@NonNull FriendReqRVAdapter.ViewHolder holder, int position) {
        holder.setContact(mContacts.get(position));
    }

    /**
     * Returns the total number of items in the data set held by the adapter.
     *
     * @return the total number of items in this adapter.
     */
    @Override
    public int getItemCount() {
        return mContacts.size();
    }

    /**
     * A ViewHolder describes an item view and metadata about its place within the RecyclerView.
     * It allows to present an individual row View from the List of rows in the Contact Recycler View.
     *
     * @author Jashanpreet Jandu & Deep Singh
     * @version 1.0
     * @since 2023-05-30
     */
    public class ViewHolder extends RecyclerView.ViewHolder {
        private FragmentHomeFrCardBinding binding;
        private Contact mContact;

        /**
         * Constructs a new ViewHolder.
         *
         * @param itemView the item view to be used by this ViewHolder.
         */
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = FragmentHomeFrCardBinding.bind(itemView);
        }

        /**
         * Deletes a friend request.
         *
         * @param button the button that triggers this functionality.
         */
        private void deleteFR(final View button) {
            mFragment.getRemoveFriendRequests(mContact.getMemberID());
        }

        /**
         * Accepts a friend request and adds a chat with the accepted friend.
         *
         * @param button the button that triggers this functionality.
         */
        private void acceptFR(final View button) {
            mFragment.getAcceptFriendRequests(mContact.getMemberID());
        }

        /**
         * Updates the view holder to hold the provided {@link Contact}.
         *
         * @param contact the Contact to be displayed by this view holder.
         */
        void setContact(final Contact contact) {
            mContact = contact;
            binding.idTVUser.setText(contact.getUsername());
            binding.idIVAccept.setOnClickListener(this::acceptFR);
            binding.idIVDecline.setOnClickListener(this::deleteFR);
        }
    }
}
