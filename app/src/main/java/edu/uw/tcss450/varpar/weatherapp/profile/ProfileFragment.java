package edu.uw.tcss450.varpar.weatherapp.profile;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import edu.uw.tcss450.varpar.weatherapp.R;
import edu.uw.tcss450.varpar.weatherapp.databinding.FragmentProfileBinding;
import edu.uw.tcss450.varpar.weatherapp.login.LoginFragmentDirections;

/**
 * Logic for displaying user profile information.
 */
public class ProfileFragment extends Fragment {

private FragmentProfileBinding mBinding;

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mBinding = FragmentProfileBinding.inflate(inflater);
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mBinding.nameLabel.setText("Chuck Finley");
        mBinding.usernameLabel.setText("notsamaxe");
        mBinding.emailLabel.setText("chuck@finley.rad");

        mBinding.buttonChangePassword.setOnClickListener(button -> {
            Log.i("TODO","Change the password if it all works");
        });
    }
}