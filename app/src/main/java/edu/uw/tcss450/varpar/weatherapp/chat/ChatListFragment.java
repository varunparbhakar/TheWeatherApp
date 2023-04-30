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

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ChatListFragment} factory method to
 * create an instance of this fragment.
 */
public class ChatListFragment extends Fragment {

    private ChatListViewModel mModel;

    public ChatListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mModel = new ViewModelProvider(getActivity()).get(ChatListViewModel.class);
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
        return inflater.inflate(R.layout.fragment_chat_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


//        FragmentChatListBinding binding = FragmentChatListBinding.bind(getView());
//
//        mModel.addChatListObserver(getViewLifecycleOwner(), blogList -> {
//            if (!blogList.isEmpty()) {
//                binding.listRoot.setAdapter(
//                        new ChatRecyclerViewAdapter(blogList)
//                );
//                binding.layoutWait.setVisibility(View.GONE);
//            }
//        });
    }
}