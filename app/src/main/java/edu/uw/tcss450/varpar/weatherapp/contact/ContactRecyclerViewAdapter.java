package edu.uw.tcss450.varpar.weatherapp.contact;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import edu.uw.tcss450.varpar.weatherapp.R;
import edu.uw.tcss450.varpar.weatherapp.databinding.FragmentContactCardBinding;

public class ContactRecyclerViewAdapter extends RecyclerView.Adapter<ContactRecyclerViewAdapter.ContactViewHolder> {

    //Store all of the blogs to present
    private final List<Contact> mContacts;

//    //Store the expanded state for each List item, true -> expanded, false -> not
//    private final Map<Contact, Boolean> mExpandedFlags;

    public ContactRecyclerViewAdapter(List<Contact> items) {
        this.mContacts = items;
//        mExpandedFlags = mContacts.stream()
//                .collect(Collectors.toMap(Function.identity(), blog -> false));

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
     * Adding a contact to the contact list
     * @param myContact
     * @return
     */
    public boolean addContact(Contact myContact){
        return mContacts.add(myContact);
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
//            binding.buittonMore.setOnClickListener(this::handleMoreOrLess);
        }

//        /**
//         * When the button is clicked in the more state, expand the card to display
//         * the blog preview and switch the icon to the less state.  When the button
//         * is clicked in the less state, shrink the card and switch the icon to the
//         * more state.
//         * @param button the button that was clicked
//         */
//        private void handleMoreOrLess(final View button) {
//            mExpandedFlags.put(mContact, !mExpandedFlags.get(mContact));
//        }

//        /**
//         * Helper used to determine if the preview should be displayed or not.
//         */
//        private void displayPreview() {
//            if (mExpandedFlags.get(mContact)) {
//                binding.textPreview.setVisibility(View.VISIBLE);
//                binding.buittonMore.setImageIcon(
//                        Icon.createWithResource(
//                                mView.getContext(),
//                                R.drawable.ic_less_grey_24dp));
//            } else {
//                binding.textPreview.setVisibility(View.GONE);
//                binding.buittonMore.setImageIcon(
//                        Icon.createWithResource(
//                                mView.getContext(),
//                                R.drawable.ic_more_grey_24dp));
//            }
//        }

        void setContact(final Contact contact) {
            mContact = contact;
//            binding.buttonFullPost.setOnClickListener(view -> {
//                Navigation.findNavController(mView).navigate(
//                        ContactListFragmentDirections
//                                .ContactFragment(blog));
//            });
            binding.textContactUser.setText(contact.getUsername());
            //Use methods in the HTML class to format the HTML found in the text
//            final String preview =  Html.fromHtml(
//                            contact.getTeaser(),
//                            Html.FROM_HTML_MODE_COMPACT)
//                    .toString().substring(0,100) //just a preview of the teaser
//                    + "...";
//            binding.textPreview.setText(preview);
//            displayPreview();
        }
    }

}
