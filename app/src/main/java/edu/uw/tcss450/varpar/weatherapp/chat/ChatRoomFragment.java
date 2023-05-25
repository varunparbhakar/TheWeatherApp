package edu.uw.tcss450.varpar.weatherapp.chat;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.jetbrains.annotations.Nullable;

import edu.uw.tcss450.varpar.weatherapp.R;
import edu.uw.tcss450.varpar.weatherapp.databinding.FragmentChatRoomBinding;
import edu.uw.tcss450.varpar.weatherapp.model.UserInfoViewModel;

public class ChatRoomFragment extends Fragment {

    //The chat ID for "global" chat
    private static final int HARD_CODED_CHAT_ID = 1;

    private ChatRoomViewModel mChatRoomModel;
    private UserInfoViewModel mUserModel;
    private ChatSendViewModel mSendModel;
    public ChatRoomFragment() {
        // Required empty public constructor
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ViewModelProvider provider = new ViewModelProvider(getActivity());
        mUserModel = provider.get(UserInfoViewModel.class);
        mChatRoomModel = provider.get(ChatRoomViewModel.class);
        mChatRoomModel.getFirstMessages(HARD_CODED_CHAT_ID, mUserModel.getJwt());
//        mSendModel = provider.get(ChatSendViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_chat_room, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        FragmentChatRoomBinding binding = FragmentChatRoomBinding.bind(getView());

        binding.swipeContainer.setRefreshing(true);

        binding.recyclerChatMessages.setAdapter(
            new ChatRoomRecyclerViewAdapter(
                mChatRoomModel.getMessageListByChatId(HARD_CODED_CHAT_ID),
                mUserModel.getEmail()
            )
        );

        binding.swipeContainer.setOnRefreshListener(() -> {
            mChatRoomModel.getNextMessages(HARD_CODED_CHAT_ID, mUserModel.getJwt());
        });

        mChatRoomModel.addMessageObserver(HARD_CODED_CHAT_ID, getViewLifecycleOwner(),
                list -> {
                    /*
                     * This solution needs work on the scroll position. As a group,
                     * you will need to come up with some solution to manage the
                     * recyclerview scroll position. You also should consider a
                     * solution for when the keyboard is on the screen.
                     */
                    //inform the RV that the underlying list has (possibly) changed
                    binding.recyclerChatMessages.getAdapter().notifyDataSetChanged();
                    binding.recyclerChatMessages.scrollToPosition(binding.recyclerChatMessages.getAdapter().getItemCount() - 1);
                    binding.swipeContainer.setRefreshing(false);
                }
        );

//        //Send button was clicked. Send the message via the SendViewModel
//        binding.buttonSend.setOnClickListener(button -> {
//            mSendModel.sendMessage(HARD_CODED_CHAT_ID,
//                    mUserModel.getmJwt(),
//                    binding.editMessage.getText().toString());
//        });
//        //when we get the response back from the server, clear the edittext
//        mSendModel.addResponseObserver(getViewLifecycleOwner(), response ->
//                binding.editMessage.setText("")
//        );
    }
}