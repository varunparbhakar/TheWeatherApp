package edu.uw.tcss450.varpar.weatherapp.chat;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.jetbrains.annotations.Nullable;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import edu.uw.tcss450.varpar.weatherapp.R;
import edu.uw.tcss450.varpar.weatherapp.databinding.FragmentChatRoomBinding;
import edu.uw.tcss450.varpar.weatherapp.model.UserInfoViewModel;

public class ChatRoomFragment extends Fragment {

    /** Stores id for chat 1. */
    private static final int HARD_CODED_CHAT_ID = 1;

    /** Stores name "User" for default Chat Room name. */
    private static final String HARD_CODED_CHAT_NAME = "User";

    /** View Model for ChatRoomFragment. */
    private ChatRoomViewModel mChatRoomModel;

    /** View Model for UserInfo, used across many fragments. */
    private UserInfoViewModel mUserModel;

    /** View Model for Sending Messages to chat. */
    private ChatSendViewModel mSendModel;

    /** Binding for this fragment. */
    private FragmentChatRoomBinding mBinding;

    /** Used to tell different chats apart and request info from endpoints. */
    private int mChatId;

    /** Used to create a display name for chat. */
    private String mChatName;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mChatId = getArguments() != null ? getArguments().getInt("chatId") : HARD_CODED_CHAT_ID;
        mChatName = getArguments() != null ? getArguments().getString("chatName") : HARD_CODED_CHAT_NAME;

        ViewModelProvider provider = new ViewModelProvider(requireActivity());
        mUserModel = provider.get(UserInfoViewModel.class);
        mChatRoomModel = provider.get(ChatRoomViewModel.class);
        mChatRoomModel.connectGetFirstMessages(mChatId, mUserModel.getJwt());
        mSendModel = provider.get(ChatSendViewModel.class);
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

        mBinding = FragmentChatRoomBinding.bind(getView());

        mBinding.textChatroomUser.setText(mChatName);

        mBinding.buttonAddChatroomMember.setOnClickListener(button -> {
                addUserToChat(mBinding.textChatroomAdd.getText().toString());
                mBinding.textChatroomAdd.setText("");
                ;
            }
        );

        mBinding.swipeContainer.setRefreshing(true);

        mChatRoomModel.connectGetUsersByChatId(mChatId, mUserModel.getJwt());

        mBinding.recyclerChatMessages.setAdapter(
            new ChatRoomRecyclerViewAdapter(
                mChatRoomModel.getMessageListByChatId(mChatId),
                mUserModel.getEmail()
            )
        );

        mBinding.swipeContainer.setOnRefreshListener(() -> {
            mChatRoomModel.connectGetNextMessages(mChatId, mUserModel.getJwt());
        });

        mChatRoomModel.addMessageObserver(mChatId, getViewLifecycleOwner(),
                list -> {
                    /*
                     * This solution needs work on the scroll position. As a group,
                     * you will need to come up with some solution to manage the
                     * recyclerview scroll position. You also should consider a
                     * solution for when the keyboard is on the screen.
                     */
                    //inform the RV that the underlying list has (possibly) changed
                    mBinding.recyclerChatMessages.getAdapter().notifyDataSetChanged();
                    mBinding.recyclerChatMessages.scrollToPosition(mBinding.recyclerChatMessages.getAdapter().getItemCount() - 1);
                    mBinding.swipeContainer.setRefreshing(false);
                }
        );

        mChatRoomModel.addResponseObserver(getViewLifecycleOwner(), this::observeResponse);

        //Send button was clicked. Send the message via the SendViewModel
        mBinding.buttonSend.setOnClickListener(button -> {
            mSendModel.sendMessage(mChatId,
                mUserModel.getJwt(),
                mBinding.editMessage.getText().toString());
        });
        //when we get the response back from the server, clear the edittext
        mSendModel.addResponseObserver(getViewLifecycleOwner(), response ->
                mBinding.editMessage.setText("")
        );
    }

    /**
     * Peel data from a JSON without making other methods complicated.
     * @param key key of item to retrieve.
     * @param jsonObject object to use.
     * @return value from key.
     */
    private String getStringFromJson(final String key, final JSONObject jsonObject) {
        String info = "";
        try {
            info = jsonObject.getString(key);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return info;
    }

    /**
     * Peel data from a JSON without making other methods complicated.
     * @param key key of item to retrieve.
     * @param jsonObject object to use.
     * @return value from key.
     */
    private JSONArray getJsonArrayFromJson(final String key, final JSONObject jsonObject) {
        JSONArray info = new JSONArray();
        try {
            info = jsonObject.getJSONArray(key);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return info;
    }

    /**
     * Adds a user to current chat by their email.
     * @param email Indicates which user to add into chat, passed by text field
     */
    private void addUserToChat(final String email) {
        mChatRoomModel.connectPutUserInChatByEmail(mChatId, email, mUserModel.getJwt());
    }

    /**
     * Observe response from Contact model server connection.
     * @param jsonObject adjusted server response.
     */
    private void observeResponse(final JSONObject jsonObject) {
        if (jsonObject.has("type")) {
            String type = getStringFromJson("type", jsonObject);
            switch (type) {
                case "getUsers":
                    try {
                        mBinding.textChatroomUser.setText(
                                getJsonArrayFromJson("email", jsonObject)
                                        .join(", ")
                                        .replaceAll("\"", "")
                        );
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                    break;
                case "put":
                    mChatRoomModel.connectGetUsersByChatId(mChatId, mUserModel.getJwt());
                    break;
                default :
                    break;
            }
        }
    }

    /** Only here cause it has to be. */
    public ChatRoomFragment() {
        // Required empty public constructor
    }
}
