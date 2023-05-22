package edu.uw.tcss450.varpar.weatherapp.home;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import edu.uw.tcss450.varpar.weatherapp.R;
import edu.uw.tcss450.varpar.weatherapp.databinding.FragmentChatPreviewBinding;

public class ChatPreviewRecyclerViewAdapter extends RecyclerView.Adapter<ChatPreviewRecyclerViewAdapter.ChatPreviewViewHolder> {

    //Store all of the blogs to present
    private final List<ChatPreview> mChatPreviews;

//    //Store the expanded state for each List item, true -> expanded, false -> not
//    private final Map<Contact, Boolean> mExpandedFlags;

    public ChatPreviewRecyclerViewAdapter(List<ChatPreview> items) {
        this.mChatPreviews = items;
//        mExpandedFlags = mContacts.stream()
//                .collect(Collectors.toMap(Function.identity(), blog -> false));
    }

    @NonNull
    @Override
    public ChatPreviewViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ChatPreviewViewHolder(LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.fragment_contact_card, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ChatPreviewViewHolder holder, int position) {
        holder.setChatPreviews(mChatPreviews.get(position));
    }

    @Override
    public int getItemCount() {
        return mChatPreviews.size();
    }

    /**
     * Objects from this class represent an Individual row View from the List
     * of rows in the Contact Recycler View.
     */
    public class ChatPreviewViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public FragmentChatPreviewBinding binding;
        private ChatPreview mChatPreview;

        public ChatPreviewViewHolder(View view) {
            super(view);
            mView = view;
            binding = FragmentChatPreviewBinding.bind(view);
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

        void setChatPreviews(final ChatPreview ChatPreview) {
            mChatPreview = ChatPreview;
//            binding.buttonFullPost.setOnClickListener(view -> {
//                Navigation.findNavController(mView).navigate(
//                        ContactListFragmentDirections
//                                .ContactFragment(blog));
//            });
//            binding.textUser.setText(ChatPreview.getUser());
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
