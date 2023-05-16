package edu.uw.tcss450.varpar.weatherapp.chat;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import edu.uw.tcss450.varpar.weatherapp.R;
import edu.uw.tcss450.varpar.weatherapp.databinding.FragmentChatRoomMessageCardBinding;

public class ChatRoomRecyclerViewAdapter extends RecyclerView.Adapter<ChatRoomRecyclerViewAdapter.ChatRoomViewHolder> {

    //Store all of the blogs to present
    private final List<ChatRoomMessage> mChats;

//    //Store the expanded state for each List item, true -> expanded, false -> not
//    private final Map<ChatMessage, Boolean> mExpandedFlags;

    public ChatRoomRecyclerViewAdapter(List<ChatRoomMessage> items) {
        this.mChats = items;
//        mExpandedFlags = mChats.stream()
//                .collect(Collectors.toMap(Function.identity(), blog -> false));

    }

    @NonNull
    @Override
    public ChatRoomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ChatRoomViewHolder(LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.fragment_chat_room_message_card, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ChatRoomViewHolder holder, int position) {
        holder.setChat(mChats.get(position));
    }

    @Override
    public int getItemCount() {
        return mChats.size();
    }

    /**
     * Objects from this class represent an Individual row View from the List
     * of rows in the Chat Recycler View.
     */
    public class ChatRoomViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public FragmentChatRoomMessageCardBinding binding;
        private ChatRoomMessage mChat;

        public ChatRoomViewHolder(View view) {
            super(view);
            mView = view;
            binding = FragmentChatRoomMessageCardBinding.bind(view);
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
//            mExpandedFlags.put(mChat, !mExpandedFlags.get(mChat));
//        }

//        /**
//         * Helper used to determine if the preview should be displayed or not.
//         */
//        private void displayPreview() {
//            if (mExpandedFlags.get(mChat)) {
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

        void setChat(final ChatRoomMessage chat) {
            mChat = chat;
            binding.textChatRoomMessage.setText(chat.getMessage());

//            binding.buttonFullPost.setOnClickListener(view -> {
//                Navigation.findNavController(mView).navigate(
//                        ChatListFragmentDirections
//                                .actionNavigationChatsToChatMessageFragment(blog));
//            });
            //Use methods in the HTML class to format the HTML found in the text
//            final String preview =  Html.fromHtml(
//                            chat.getTeaser(),
//                            Html.FROM_HTML_MODE_COMPACT)
//                    .toString().substring(0,100) //just a preview of the teaser
//                    + "...";
//            binding.textPreview.setText(preview);
//            displayPreview();
        }
    }

}
