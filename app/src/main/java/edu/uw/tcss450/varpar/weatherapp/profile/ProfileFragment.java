package edu.uw.tcss450.varpar.weatherapp.profile;

import static edu.uw.tcss450.varpar.weatherapp.util.PasswordValidator.checkClientPredicate;
import static edu.uw.tcss450.varpar.weatherapp.util.PasswordValidator.checkExcludeWhiteSpace;
import static edu.uw.tcss450.varpar.weatherapp.util.PasswordValidator.checkPwdDigit;
import static edu.uw.tcss450.varpar.weatherapp.util.PasswordValidator.checkPwdLength;
import static edu.uw.tcss450.varpar.weatherapp.util.PasswordValidator.checkPwdLowerCase;
import static edu.uw.tcss450.varpar.weatherapp.util.PasswordValidator.checkPwdSpecialChar;
import static edu.uw.tcss450.varpar.weatherapp.util.PasswordValidator.checkPwdUpperCase;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.json.JSONException;
import org.json.JSONObject;

import edu.uw.tcss450.varpar.weatherapp.R;
import edu.uw.tcss450.varpar.weatherapp.databinding.FragmentProfileBinding;
import edu.uw.tcss450.varpar.weatherapp.util.PasswordValidator;

/**
 * Logic for displaying user profile information.
 * @author James Deal
 */
public class ProfileFragment extends Fragment {

    /** Binding for layout views. */
    private FragmentProfileBinding mBinding;
    private UserInfoViewModel mModel;

    private final PasswordValidator mPasswordValidator =
            checkClientPredicate(pwd -> pwd.equals(mBinding.newPassword2.getText().toString()))
                    .and(checkPwdLength(7))
                    .and(checkPwdSpecialChar())
                    .and(checkExcludeWhiteSpace())
                    .and(checkPwdDigit())
                    .and(checkPwdLowerCase().or(checkPwdUpperCase()));

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
     * Sets field data to mirror user data, ensures button has ability to change password.
     * @param view The View returned by {@link #onCreateView(LayoutInflater, ViewGroup, Bundle)}.
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     * from a previous saved state as given here.
     */
    @Override
    public void onViewCreated(final @NonNull View view, final @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mModel = new ViewModelProvider(getActivity())
                .get(UserInfoViewModel.class);
        mModel.addResponseObserver(getViewLifecycleOwner(),
                this::observeResponse);

        String name = mModel.getFirstName() + " " + mModel.getLastName();
        mBinding.nameField.setText(name);
        mBinding.usernameField.setText(mModel.getUsername());
        mBinding.emailField.setText(mModel.getEmail());

        mBinding.buttonChangePassword.setOnClickListener(button -> {
            this.validatePassword();
        });
    }

    /**
     * Functionality for user to change password.
     * Checks if new password is valid, and verifies old password with server.
     */
    private void validatePassword() {
        if (!mBinding.newPassword1.getText().toString().trim()
                .equals(mBinding.newPassword2.getText().toString().trim())) {
            mBinding.newPassword1.setError(getText(R.string.profile_fragment_passwordNoMatch));
            return;
        }

        //is new pw valid, then go
        mPasswordValidator.processResult(
                mPasswordValidator.apply(mBinding.newPassword1.getText().toString()),
                () -> mModel.connectValidatePassword(mBinding.oldPassword.getText().toString().trim(),
                        mBinding.newPassword1.getText().toString().trim()),
                result -> mBinding.newPassword1.setError(getText(R.string.profile_fragment_passwordWeak)));
    }

    private void observeResponse(final JSONObject response) {
        if (response.length() > 0) {
            if (response.has("code")) {
                try {
                    mBinding.newPassword1.setError(
                            "Error Authenticating: " +
                                    response.getJSONObject("data").getString("message"));
                } catch (JSONException e) {
                    Log.e("JSON Parse Error", e.getMessage());
                }
            } else {
                //TODO this is success
                try {
                    mBinding.newPassword1.setText(response.getString("message"));
                    mBinding.newPassword2.setText("");
                    mBinding.oldPassword.setText("");
                } catch (JSONException e) {
                    Log.e("JSON Parse Error in ProfileFrag.observeResponse: ", e.getMessage());
                }
            }
        } else {
            Log.d("JSON Response", "No Response");
        }
    }

}
