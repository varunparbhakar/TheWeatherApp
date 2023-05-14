package edu.uw.tcss450.varpar.weatherapp.contact;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.jetbrains.annotations.Nullable;

import edu.uw.tcss450.varpar.weatherapp.R;
import edu.uw.tcss450.varpar.weatherapp.chat.ChatGenerator;
import edu.uw.tcss450.varpar.weatherapp.chat.ChatRecyclerViewAdapter;
import edu.uw.tcss450.varpar.weatherapp.databinding.FragmentContactListBinding;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ContactListFragment} factory method to
 * create an instance of this fragment.
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
//        mModel = new ViewModelProvider(getActivity()).get(ContactListViewModel.class);
//        mModel.connectGet();
    }

//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container,
//                             Bundle savedInstanceState) {
//        return inflater.inflate(R.layout.fragment_chat_list, container, false);
//    }

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
        myContactAdapter = new ContactRecyclerViewAdapter(ContactGenerator.getContactList());
        binding.listRoot.setAdapter(myContactAdapter);

        //This code causes a crash!!!!!
//        binding.buttonAddContact.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                myContactAdapter.addContact(new Contact.Builder("Varun").build());
//                myContactAdapter.notifyItemInserted(myContactAdapter.getItemCount() - 1);
//            }
//        });



//        int position = myContactAdapter.getItemCount() - 1;
//        myContactAdapter.notifyItemInserted(position);



//        mModel.addContactListObserver(getViewLifecycleOwner(), blogList -> {
//            if (!blogList.isEmpty()) {
//                binding.listRoot.setAdapter(
//                        new ContactRecyclerViewAdapter(blogList)
//                );
//                binding.layoutWait.setVisibility(View.GONE);
//            }
//        });
    }
}