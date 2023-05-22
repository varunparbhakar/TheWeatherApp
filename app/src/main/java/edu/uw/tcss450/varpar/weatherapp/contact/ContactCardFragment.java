//package edu.uw.tcss450.varpar.weatherapp.contact;
//
//import android.os.Bundle;
//import android.util.Log;
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
//import edu.uw.tcss450.varpar.weatherapp.databinding.FragmentContactCardBinding;
//
//public class ContactCardFragment extends Fragment {
//
//
//    public ContactCardFragment() {
//        // Required empty public constructor
//    }
//
//    @Override
//    public void onCreate(@Nullable Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//    }
//
//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container,
//                             Bundle savedInstanceState) {
//        // Inflate the layout for this fragment
//        return inflater.inflate(R.layout.fragment_contact_list, container, false);
//    }
//
//    @Override
//    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
//        super.onViewCreated(view, savedInstanceState);
//
//        Log.d("OnViewCreated:", "got here");
//
//        FragmentContactCardBinding cardBinding = FragmentContactCardBinding.bind(getView());
//
//        cardBinding.layoutInner.setOnClickListener(
//                card -> Navigation.findNavController(getView())
//                        .navigate(ContactListFragmentDirections.actionNavigationContactsToChatRoom())
//        );
//
//    }
//}