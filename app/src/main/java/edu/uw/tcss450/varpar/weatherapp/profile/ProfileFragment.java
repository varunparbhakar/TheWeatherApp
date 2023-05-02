package edu.uw.tcss450.varpar.weatherapp.profile;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import edu.uw.tcss450.varpar.weatherapp.databinding.FragmentProfileBinding;

/**
 * Logic for displaying user profile information.
 * @author James Deal
 */
public class ProfileFragment extends Fragment {

    /** Binding for layout views. */
    private FragmentProfileBinding mBinding;

    /**
     * Required empty public constructor.
     */
    public ProfileFragment() {
        //Hello World
    }

    /**
     * Default.
     * @param savedInstanceState If the fragment is being re-created from
     * a previous saved state, this is the state.
     */
    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    /**
     * Default, returns view retrieved from using binding.
     * @param inflater The LayoutInflater object that can be used to inflate
     * any views in the fragment,
     * @param container If non-null, this is the parent view that the fragment's
     * UI should be attached to.  The fragment should not add the view itself,
     * but this can be used to generate the LayoutParams of the view.
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     * from a previous saved state as given here.
     *
     * @return view root.
     */
    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             final Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mBinding = FragmentProfileBinding.inflate(inflater);
        return mBinding.getRoot();
    }

    /**
     * TODO: Currently houses mock data.
     * Sets field data to mirror user data, ensures button has ability to change password.
     * @param view The View returned by {@link #onCreateView(LayoutInflater, ViewGroup, Bundle)}.
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     * from a previous saved state as given here.
     */
    @Override
    public void onViewCreated(final @NonNull View view, final @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mBinding.nameField.setText("Chuck" + " " + "Finley");
        mBinding.usernameField.setText("notsamaxe");
        mBinding.emailField.setText("chuck@finley.rad");

        mBinding.buttonChangePassword.setOnClickListener(button -> {
            Log.i("TODO", "Change the password if it all works");
        });
    }
}
