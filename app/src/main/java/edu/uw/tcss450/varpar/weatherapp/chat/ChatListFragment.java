package edu.uw.tcss450.varpar.weatherapp.chat;

import android.app.AlertDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.jetbrains.annotations.Nullable;
import org.json.JSONException;
import org.json.JSONObject;

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

    private UserInfoViewModel mUserModel;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ViewModelProvider provider = new ViewModelProvider(requireActivity());
        mUserModel = provider.get(UserInfoViewModel.class);
        mChatListModel = provider.get(ChatListViewModel.class);
        mChatListModel.connectGetChatIds(mUserModel.getMemberID(), mUserModel.getJwt());
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

        mChatListModel.addResponseObserver(getViewLifecycleOwner(), this::observeResponse);

        // Set button behavior to Add Chat
        mBinding.buttonAddChat.setOnClickListener(
            button -> addChatWithName(mBinding.textChatSearch.getText().toString())
        );
    }

    /**
     * Passes intention of adding chat with given name from View to Model.
     * @param chatName name of chat
     */
    private void addChatWithName(final String chatName) {
        mChatListModel.connectAddChatWithName(chatName, mUserModel.getJwt());
    }

    private void putMeInChat(final String chatId) {
        mChatListModel.connectPutMeInChat(chatId, mUserModel.getJwt());
    }

    /**
     * Passes intention of deleting user in a chat from View to Model.
     * @param chatId identifies chat to remove user from
     */
    public void deleteUserFromChat(final String chatId) {
        mChatListModel.connectDeleteUserFromChat(chatId, mUserModel.getEmail(), mUserModel.getJwt());
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
                    try {
                        putMeInChat(jsonObject.getString("chatID"));
                        Log.wtf("OOOYEEEAH", "called putMeInChat");
                    } catch (JSONException e){
                        Log.e("JSON Error", e.getMessage());
                    }
                    break;
                case "delete":
                    deleteChatResponse(jsonObject);
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
            mChatListModel.getChatList();
        }
        resp = getFromJson("message", jsonObject);
        createAlertDialogue(resp);
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