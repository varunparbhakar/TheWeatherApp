package edu.uw.tcss450.varpar.weatherapp.contact;

import android.app.AlertDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.jetbrains.annotations.Nullable;
import org.json.JSONObject;

import edu.uw.tcss450.varpar.weatherapp.R;
import edu.uw.tcss450.varpar.weatherapp.databinding.FragmentContactListBinding;
import edu.uw.tcss450.varpar.weatherapp.model.UserInfoViewModel;

/**
 * Logic for contact list visual.
 */
public class ContactListFragment extends Fragment{

    private ContactListViewModel mModel;
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

        myContactAdapter = new ContactRecyclerViewAdapter(mModel.getContactList());
        binding.recyclerContacts.setAdapter(myContactAdapter);

        mModel.addContactListObserver(getViewLifecycleOwner(), contactList -> {
            if (!contactList.isEmpty()) {
                myContactAdapter = new ContactRecyclerViewAdapter(mModel.getContactList());
                binding.recyclerContacts.setAdapter(myContactAdapter);

                binding.layoutWait.setVisibility(View.GONE);
            }
        });

//        binding.layoutWait.setVisibility(View.GONE); //make the visibility good when stuff broken

        binding.buttonAddContact.setOnClickListener(v -> {
            mModel.connectAddContact(binding.textContactSearch.getText().toString());
        });

        mModel.addResponseObserver(getViewLifecycleOwner(), this::observeResponse);

        binding.textContactSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                myContactAdapter.filter(s.toString());
            }
        });

        myContactAdapter.mMemberID = mModel.getMemberID();




    }

    public void deleteContact() {

    }



    private void observeResponse(final JSONObject jsonObject) {
        if (jsonObject.has("type")) {
            if (getFromJson("type", jsonObject).equals("post")) {
                String resp = getFromJson("message",jsonObject);
                createAlertDialogue(resp);
            } else if (getFromJson("type", jsonObject).equals("delete")) {
                Log.wtf("delete", "not implemented yet");
            }
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