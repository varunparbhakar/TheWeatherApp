//package edu.uw.tcss450.varpar.weatherapp.chat;
//
//import android.os.Bundle;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//
//import androidx.annotation.NonNull;
//import androidx.fragment.app.Fragment;
//import androidx.navigation.Navigation;
//
//import org.jetbrains.annotations.Nullable;
//
//import edu.uw.tcss450.varpar.weatherapp.R;
//import edu.uw.tcss450.varpar.weatherapp.databinding.FragmentChatListCardBinding;
//
//public class ChatListCardFragment extends Fragment {
//
//    public ChatListCardFragment() {
//        // Required empty public constructor
//    }
//
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//    }
//
//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container,
//                             Bundle savedInstanceState) {
//        // Inflate the layout for this fragment
//        return inflater.inflate(R.layout.fragment_chat_room, container, false);
//    }
//
//    @Override
//    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
//        super.onViewCreated(view, savedInstanceState);
//
//        FragmentChatListCardBinding binding = FragmentChatListCardBinding.bind(getView());
//
//        binding.layoutInner.setOnClickListener(
//                card -> Navigation.findNavController(getView())
//                        .navigate(ChatListFragmentDirections.actionNavigationChatToChatRoom())
//        );
//    }
//}