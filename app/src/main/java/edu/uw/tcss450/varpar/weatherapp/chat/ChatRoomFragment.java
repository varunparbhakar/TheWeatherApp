package edu.uw.tcss450.varpar.weatherapp.chat;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

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

    private ChatRoomViewModel mChatModel;

    private UserInfoViewModel mUserModel;

    private ChatSendViewModel mSendModel;

    public ChatRoomFragment() {
        // Required empty public constructor
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
        binding.listRoot.setAdapter(
                new ChatRoomRecyclerViewAdapter(ChatRoomMessageGenerator.getChatRoomMessagesAsList())
        );
    }
}