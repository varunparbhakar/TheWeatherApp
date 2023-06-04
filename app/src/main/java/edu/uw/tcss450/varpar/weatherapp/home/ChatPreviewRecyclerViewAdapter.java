package edu.uw.tcss450.varpar.weatherapp.home;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import edu.uw.tcss450.varpar.weatherapp.R;
import edu.uw.tcss450.varpar.weatherapp.databinding.FragmentChatPreviewBinding;

/**
 * {@link RecyclerView.Adapter} that can display a {@link ChatPreview} and makes a call to the
 * specified {@link ChatPreview}.
 * This Adapter is meant to be used with a {@link RecyclerView}.
 *
 * @author Jashanpreet Jandu
 * @version 1.0
 * @since 2023-06-03
 */
public class ChatPreviewRecyclerViewAdapter extends RecyclerView.Adapter<ChatPreviewRecyclerViewAdapter.ChatPreviewViewHolder> {

    /**
     * A list containing all ChatPreviews to present.
     */
    private final List<ChatPreview> mChatPreviews;

    /**
     * Constructs a new ChatPreviewRecyclerViewAdapter.
     *
     * @param items a List of ChatPreview objects to display.
     */
    public ChatPreviewRecyclerViewAdapter(List<ChatPreview> items) {
        this.mChatPreviews = items;
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
    public ChatPreviewViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ChatPreviewViewHolder(LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.fragment_contact_card, parent, false));
    }

    /**
     * Invoked by RecyclerView to display the data at the specified position.
     *
     * @param holder the ViewHolder to be updated to represent the contents of the item at the given position in the data set.
     * @param position the position of the item within the adapter's data set.
     */
    @Override
    public void onBindViewHolder(@NonNull ChatPreviewViewHolder holder, int position) {
        holder.setChatPreviews(mChatPreviews.get(position));
    }

    /**
     * Returns the total number of items in the data set held by the adapter.
     *
     * @return the total number of items in this adapter.
     */
    @Override
    public int getItemCount() {
        return mChatPreviews.size();
    }

    /**
     * A ViewHolder describes an item view and metadata about its place within the RecyclerView.
     * It allows to present an individual row View from the List of rows in the Contact Recycler View.
     *
     * @author Jashanpreet Jandu
     * @version 1.0
     * @since 2023-06-03
     */
    public class ChatPreviewViewHolder extends RecyclerView.ViewHolder {
        /**
         * The main view for this ViewHolder
         */
        public final View mView;

        /**
         * The binding for this view holder
         */
        public FragmentChatPreviewBinding binding;

        /**
         * The ChatPreview for this view holder
         */
        private ChatPreview mChatPreview;

        /**
         * Constructs a new ChatPreviewViewHolder.
         *
         * @param view the View that you inflated in {@link #onCreateViewHolder(ViewGroup, int)}
         */
        public ChatPreviewViewHolder(View view) {
            super(view);
            mView = view;
            binding = FragmentChatPreviewBinding.bind(view);
        }

        /**
         * Updates the view holder to hold the provided {@link ChatPreview}.
         *
         * @param ChatPreview the chat preview to be displayed by this view holder.
         */
        void setChatPreviews(final ChatPreview ChatPreview) {
            mChatPreview = ChatPreview;
        }
    }
}
