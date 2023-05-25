package edu.uw.tcss450.varpar.weatherapp.contact;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.jetbrains.annotations.Nullable;

import edu.uw.tcss450.varpar.weatherapp.R;
import edu.uw.tcss450.varpar.weatherapp.databinding.FragmentContactListBinding;
import edu.uw.tcss450.varpar.weatherapp.model.UserInfoViewModel;

/**
 * Logic for contact list visual.
 */
public class ContactListFragment extends Fragment {

    private ContactListViewModel mModel;
    private ContactRecyclerViewAdapter myContactAdapter;

    public ContactListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mModel = new ViewModelProvider(getActivity()).get(ContactListViewModel.class);

        //could be made one line if it works
        ViewModelProvider provider = new ViewModelProvider(getActivity());
        UserInfoViewModel uivm = provider.get(UserInfoViewModel.class);
        mModel.setJwt(uivm.getJwt());
        mModel.setMemberID("4");

        mModel.getContacts();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_contact_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        FragmentContactListBinding binding = FragmentContactListBinding.bind(getView());

        myContactAdapter = new ContactRecyclerViewAdapter(mModel.getContactList());
        binding.recyclerChatMessages.setAdapter(myContactAdapter);

        mModel.addContactListObserver(getViewLifecycleOwner(), contactList -> {
            if (!contactList.isEmpty()) {
                binding.recyclerChatMessages.setAdapter(
                        new ContactRecyclerViewAdapter(contactList)
                );
                binding.layoutWait.setVisibility(View.GONE);
            }
        });

        binding.layoutWait.setVisibility(View.GONE); //make the visibility good when stuff broken

        binding.buttonAddContact.setOnClickListener(v -> {
//            mModel.addContact(new Contact.Builder("Varun", "420").build());
            Log.wtf("TODO", "Yeah idk man");
        });
    }
}