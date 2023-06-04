package edu.uw.tcss450.varpar.weatherapp.chat;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import edu.uw.tcss450.varpar.weatherapp.R;
import edu.uw.tcss450.varpar.weatherapp.contact.Contact;
import edu.uw.tcss450.varpar.weatherapp.databinding.FragmentChatListCardBinding;

public class ChatListRecyclerViewAdapter extends RecyclerView.Adapter<ChatListRecyclerViewAdapter.ChatListViewHolder> {

    /** Store all of the Chats to present. */
    private final List<ChatListRoom> mChatListItems;

    /** Store copy all of the Chats to present for safe search. */
    private List<ChatListRoom> mChatListItemsCopy;

    /** Host fragment to bounce view actions to. */
    private final ChatListFragment mFragment;

    public ChatListRecyclerViewAdapter(List<ChatListRoom> items, ChatListFragment frag) {
        this.mChatListItems = items;
        this.mChatListItemsCopy = new ArrayList<>();
        this.mChatListItemsCopy.addAll(items);
        this.mFragment = frag;
    }

    /**
     * Filter list contents during live search.
     * Refresh to full list once search is empty.
     * @param text current search text.
     */
    public void filter(String text) {
        mChatListItems.clear();
        if(text.isEmpty()){
            mChatListItems.addAll(mChatListItemsCopy);
        } else{
            text = text.toLowerCase();
            for(ChatListRoom item: mChatListItemsCopy){
                if(item.getName().toLowerCase().contains(text)){
                    mChatListItems.add(item);
                }
            }
        }
        notifyDataSetChanged();
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
    public class ChatListViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public FragmentChatListCardBinding binding;
        private ChatListRoom mChat;

        public ChatListViewHolder(View view) {
            super(view);
            mView = view;
            binding = FragmentChatListCardBinding.bind(view);
        }

        /**
         * Dials out to fragment for chat deletion.
         * @param button button that houses functionality.
         */
        private void deleteUser(final View button) {
            mFragment.deleteUserFromChat(mChat.getChatId());
        }

        void setChat(final ChatListRoom chatListItem) {
            mChat = chatListItem;

            binding.textChatUser.setText(chatListItem.getName());
            binding.layoutInner.setOnClickListener(
                card -> {
                    Navigation.findNavController(mView).navigate(
                        ChatListFragmentDirections.actionNavigationChatToChatRoom(
                                Integer.parseInt(mChat.getChatId())
                        )
                    );
                }
            );
            binding.deleteChat.setOnClickListener(this::deleteUser);
        }
    }

}
