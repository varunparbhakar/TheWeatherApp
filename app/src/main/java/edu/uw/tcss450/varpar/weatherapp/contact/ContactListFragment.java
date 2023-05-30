package edu.uw.tcss450.varpar.weatherapp.contact;

import android.app.AlertDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.jetbrains.annotations.Nullable;
import org.json.JSONObject;

import edu.uw.tcss450.varpar.weatherapp.R;
import edu.uw.tcss450.varpar.weatherapp.chat.ChatListFragmentDirections;
import edu.uw.tcss450.varpar.weatherapp.databinding.FragmentContactListBinding;
import edu.uw.tcss450.varpar.weatherapp.model.UserInfoViewModel;

/**
 * Logic for contact list visual.
 * @author Nathan Brown, James Deal
 */
public class ContactListFragment extends Fragment {

    /** Model for Contact List state. */
    private ContactListViewModel mModel;

    /** Adapter to hold Contact List. */
    private ContactRecyclerViewAdapter myContactAdapter;

    public ContactListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //initiate contact list.
        mModel = new ViewModelProvider(getActivity()).get(ContactListViewModel.class);

        //get user info, give to contact model
        UserInfoViewModel uivm = new ViewModelProvider(getActivity()).get(UserInfoViewModel.class);
        mModel.setJwt(uivm.getJwt());
        mModel.setMemberID(uivm.getMemberID());

        //get contacts from server
        mModel.getContacts();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_contact_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        FragmentContactListBinding binding = FragmentContactListBinding.bind(getView());

        //default just in case
        myContactAdapter = new ContactRecyclerViewAdapter(mModel.getContactList(), this);
        binding.recyclerContacts.setAdapter(myContactAdapter);

        //Connect data from server to views.
        mModel.addContactListObserver(getViewLifecycleOwner(), contactList -> {
            myContactAdapter = new ContactRecyclerViewAdapter(mModel.getContactList(), this);
            binding.recyclerContacts.setAdapter(myContactAdapter);

            binding.layoutWait.setVisibility(View.GONE);
        });

        //Add friend functionality
        binding.buttonAddContact.setOnClickListener(v ->
                mModel.connectAddContact(binding.textContactSearch.getText().toString()));

        mModel.addResponseObserver(getViewLifecycleOwner(), this::observeResponse);

        //Live search functionality
        binding.textContactSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //Unused
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //Unused
            }

            @Override
            public void afterTextChanged(Editable s) {
                myContactAdapter.filter(s.toString());
            }
        });
    }

    /**
     * Passes intention of deleting contact from View to Model.
     * @param memberID friend to remove.
     */
    public void deleteContact(String memberID) {
        mModel.connectDeleteContact(memberID);
    }

    /**
     * Passes intention of adding chat with contact from View to Model.
     * @param memberID friend to remove.
     */
    public void addContactChat(String memberID) {
        mModel.connectAddContactChat(memberID);
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
                    addContactResponse(jsonObject);
                    break;
                case "delete":
                    deleteContactResponse(jsonObject);
                    break;
                case "chat":
                    chatContactResponse(jsonObject);
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
    private void addContactResponse(final JSONObject jsonObject) {
        String resp = getFromJson("message", jsonObject);
        createAlertDialogue(resp);
    }

    /**
     * Action for server response of contact delete.
     * @param jsonObject adjusted server response
     */
    private void deleteContactResponse(final JSONObject jsonObject) {
        String resp;
        if (getFromJson("success", jsonObject).equals("true")) {
            mModel.getContacts();
        }
        resp = getFromJson("message", jsonObject);
        createAlertDialogue(resp);
    }

    /**
     * Action for server response of contact add chat.
     * @param jsonObject adjusted server response
     */
    private void chatContactResponse(final JSONObject jsonObject) {
        if (getFromJson("success", jsonObject).equals("true")) {
            Log.wtf("contacts", "make chat, go to fragment");
//            createAlertDialogue("Not implemented yet.");
            String idString = getFromJson("chatid", jsonObject);

            Navigation.findNavController(getView()).navigate(
                    ContactListFragmentDirections.actionNavigationContactsToChatRoom(
                            Integer.parseInt(idString)
                    )
            );
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
}
