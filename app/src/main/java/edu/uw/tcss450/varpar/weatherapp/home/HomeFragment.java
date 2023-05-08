package edu.uw.tcss450.varpar.weatherapp.home;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.Objects;

import edu.uw.tcss450.varpar.weatherapp.R;
import edu.uw.tcss450.varpar.weatherapp.databinding.FragmentHomeBinding;
import edu.uw.tcss450.varpar.weatherapp.databinding.FragmentLoginBinding;
import edu.uw.tcss450.varpar.weatherapp.profile.UserInfoViewModel;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {
    FragmentHomeBinding mBinding;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mBinding = FragmentHomeBinding.inflate(inflater, container, false);
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        UserInfoViewModel model = new ViewModelProvider(getActivity()).get(UserInfoViewModel.class);
        mBinding.textGreeting.setText("Welcome " + model.getEmail());
    }
}