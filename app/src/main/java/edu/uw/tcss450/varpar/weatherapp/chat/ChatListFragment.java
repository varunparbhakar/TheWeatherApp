package edu.uw.tcss450.varpar.weatherapp.chat;

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

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ViewModelProvider provider = new ViewModelProvider(requireActivity());
        UserInfoViewModel mUserModel = provider.get(UserInfoViewModel.class);
        mChatListModel = provider.get(ChatListViewModel.class);
//        mChatListModel.getChatIds(mUserModel.getMemberId(), mUserModel.getJwt());
        mChatListModel.getChatIds("4", mUserModel.getJwt());
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


        mBinding = FragmentChatListBinding.bind(getView());

        mBinding.recyclerChatMessages.setAdapter(
            new ChatListRecyclerViewAdapter(
                mChatListModel.getChatList()
            )
        );

        Log.d("ChatListViewModel:", "" + mChatListModel.getChatList().size());


        mChatListModel.addChatListObserver(getViewLifecycleOwner(), chatList -> {
            if (!chatList.isEmpty()) {
                mBinding.recyclerChatMessages.setAdapter(
                        new ChatListRecyclerViewAdapter(chatList)
                );
//                mBinding.layoutRoot.setVisibility(View.GONE);
            }
        });
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