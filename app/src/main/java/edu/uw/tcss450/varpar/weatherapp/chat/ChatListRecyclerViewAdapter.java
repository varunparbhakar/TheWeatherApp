package edu.uw.tcss450.varpar.weatherapp.chat;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import edu.uw.tcss450.varpar.weatherapp.R;
import edu.uw.tcss450.varpar.weatherapp.databinding.FragmentChatListCardBinding;

public class ChatListRecyclerViewAdapter extends RecyclerView.Adapter<ChatListRecyclerViewAdapter.ChatListViewHolder> {

    //Store all of the blogs to present
    private final List<ChatListRoom> mChatListItems;

    /** Host fragment to bounce view actions to. */
    private final ChatListFragment mFragment;

    public ChatListRecyclerViewAdapter(List<ChatListRoom> items, ChatListFragment frag) {
        this.mChatListItems = items;
        this.mFragment = frag;
    }

    @NonNull
    @Override
    public ChatListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ChatListViewHolder(
                LayoutInflater
                        .from(parent.getContext())
                        .inflate(R.layout.fragment_chat_list_card, parent, false)
        );
    }

    @Override
    public void onBindViewHolder(@NonNull ChatListViewHolder holder, int position) {
        holder.setChat(mChatListItems.get(position));
    }

    @Override
    public int getItemCount() {
        return mChatListItems.size();
    }

    /**
     * Objects from this class represent an Individual row View from the List
     * of rows in the Chat Recycler View.
     */
    public static class ChatListViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public FragmentChatListCardBinding binding;
        private ChatListRoom mChat;

        public ChatListViewHolder(View view) {
            super(view);
            mView = view;
            binding = FragmentChatListCardBinding.bind(view);
        }

        void setChat(final ChatListRoom chatListItem) {
            mChat = chatListItem;

            binding.textChatUser.setText(chatListItem.getName());
            binding.textChatMessage.setText(R.string.chat_card_text);
            binding.layoutInner.setOnClickListener(
                card -> {
                    Navigation.findNavController(mView).navigate(
                        ChatListFragmentDirections.actionNavigationChatToChatRoom(
                                Integer.parseInt(mChat.getChatId())
                        )
                    );
                }
            );
        }
    }

}
