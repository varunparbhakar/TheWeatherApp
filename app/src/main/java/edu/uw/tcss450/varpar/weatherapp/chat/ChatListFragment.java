package edu.uw.tcss450.varpar.weatherapp.chat;

import android.app.AlertDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.jetbrains.annotations.Nullable;
import org.json.JSONObject;

import java.util.Objects;

import edu.uw.tcss450.varpar.weatherapp.databinding.FragmentChatListBinding;
import edu.uw.tcss450.varpar.weatherapp.model.UserInfoViewModel;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ChatListFragment} factory method to
 * create an instance of this fragment.
 */
public class ChatListFragment extends Fragment {

    private ChatListViewModel mChatListModel;

    private FragmentChatListBinding mBinding;

    private ChatListRecyclerViewAdapter myChatListAdapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ViewModelProvider provider = new ViewModelProvider(requireActivity());
        UserInfoViewModel mUserModel = provider.get(UserInfoViewModel.class);
        mChatListModel = provider.get(ChatListViewModel.class);
        mChatListModel.getChatIds(mUserModel.getMemberID(), mUserModel.getJwt());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mBinding = FragmentChatListBinding.inflate(inflater, container, false);
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Attach binding to access components
        mBinding = FragmentChatListBinding.bind(getView());

        // Initialize View Adapter and set to intended component
        myChatListAdapter = new ChatListRecyclerViewAdapter(
                mChatListModel.getChatList(), this
        );
        mBinding.recyclerChatMessages.setAdapter(myChatListAdapter);

        // Re-rendering the views when getting data from server
        mChatListModel.addChatListObserver(getViewLifecycleOwner(), chatList -> {
            myChatListAdapter = new ChatListRecyclerViewAdapter(
                    mChatListModel.getChatList(), this
            );
            mBinding.recyclerChatMessages.setAdapter(myChatListAdapter);
            mBinding.layoutWait.setVisibility(View.GONE);
        });

        // Set button behavior to Add Chat
        mBinding.buttonAddChat.setOnClickListener(
            button -> mChatListModel.connectAddChat(mBinding.textChatSearch.getText().toString())
        );

        mChatListModel.addResponseObserver(getViewLifecycleOwner(), this::observeResponse);
    }

    /**
     * Passes intention of adding chat with given name from View to Model.
     * @param chatName name of chat
     */
    public void addChatWithName(String chatName) {
        mChatListModel.connectAddChatWithName(chatName);
    }

    /**
     * Passes intention of deleting user in a chat from View to Model.
     * @param chatId identifies chat to remove user from
     * @param email identifies user to be removed
     */
    public void deleteUserFromChat(String chatId, String email) {
        mChatRoomModel.connectDeleteUserFromChat(chatId, email);
    }

    /**
     * Observe response from Contact model server connection.
     * @param jsonObject adjusted server response.
     */
    private void observeResponse(final JSONObject jsonObject) {
        if (jsonObject.has("type")) {
            String type = getFromJson("type", jsonObject);
            switch (type) {
                case "post":
                    addChatResponse(jsonObject);
                    break;
                case "delete":
                    deleteChatResponse(jsonObject);
                    break;
                case "chat":
                    chatChatResponse(jsonObject);
                    break;
                default :
                    break;
            }
        }
    }

    /**
     * Action for server response of contact add.
     * @param jsonObject adjusted server response
     */
    private void addChatResponse(final JSONObject jsonObject) {
        String resp = getFromJson("message", jsonObject);
        createAlertDialogue(resp);
    }

    /**
     * Action for server response of contact delete.
     * @param jsonObject adjusted server response
     */
    private void deleteChatResponse(final JSONObject jsonObject) {
        String resp;
        if (getFromJson("success", jsonObject).equals("true")) {
            mChatListModel.getChats();
        }
        resp = getFromJson("message", jsonObject);
        createAlertDialogue(resp);
    }

    /**
     * Action for server response of contact add chat.
     * @param jsonObject adjusted server response
     */
    private void chatChatResponse(final JSONObject jsonObject) {
        if (getFromJson("success", jsonObject).equals("true")) {
            Log.wtf("contacts", "make chat, go to fragment");
            createAlertDialogue("Not implemented yet.");
        } else {
            String resp = getFromJson("message", jsonObject);
            createAlertDialogue(resp);
        }
    }

    /**
     * Peel data from a JSON without making other methods complicated.
     * @param key key of item to retrieve.
     * @param jsonObject object to use.
     * @return value from key.
     */
    private String getFromJson(final String key, final JSONObject jsonObject) {
        String info = "";
        try {
            info = jsonObject.getString(key);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return info;
    }

    /**
     * Dialogs that are used to notify user of events.
     * @param message message to display to user.
     */
    private void createAlertDialogue(final String message) {
        AlertDialog.Builder builder1 = new AlertDialog.Builder(getActivity());
        builder1.setMessage(message);
        builder1.setCancelable(true);

        builder1.setPositiveButton(
                "Ok",
                (dialog, id) -> dialog.cancel());

        AlertDialog alert11 = builder1.create();
        alert11.show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mBinding = null;
    }

    public ChatListFragment() {
        // Required empty public constructor
    }
}