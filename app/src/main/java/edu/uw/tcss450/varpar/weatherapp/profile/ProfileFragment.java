package edu.uw.tcss450.varpar.weatherapp.profile;

import static edu.uw.tcss450.varpar.weatherapp.util.PasswordValidator.checkClientPredicate;
import static edu.uw.tcss450.varpar.weatherapp.util.PasswordValidator.checkExcludeWhiteSpace;
import static edu.uw.tcss450.varpar.weatherapp.util.PasswordValidator.checkPwdDigit;
import static edu.uw.tcss450.varpar.weatherapp.util.PasswordValidator.checkPwdLength;
import static edu.uw.tcss450.varpar.weatherapp.util.PasswordValidator.checkPwdLowerCase;
import static edu.uw.tcss450.varpar.weatherapp.util.PasswordValidator.checkPwdSpecialChar;
import static edu.uw.tcss450.varpar.weatherapp.util.PasswordValidator.checkPwdUpperCase;

import android.app.AlertDialog;
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
import edu.uw.tcss450.varpar.weatherapp.model.UserInfoViewModel;
import edu.uw.tcss450.varpar.weatherapp.util.PasswordValidator;

/**
 * Logic for displaying user profile information.
 * @author James Deal
 */
public class ProfileFragment extends Fragment {

    /** Binding for layout views. */
    private FragmentProfileBinding mBinding;

    /** User info view model. */
    private UserInfoViewModel mModel;

    /** Ensures password length, character set, and complexity. */
    private final PasswordValidator mPasswordValidator =
            checkClientPredicate(pwd -> pwd.equals(mBinding.newPassword2.getText().toString()))
                    .and(checkPwdLength(MIN_PASSWORD_LENGTH))
                    .and(checkPwdSpecialChar())
                    .and(checkExcludeWhiteSpace())
                    .and(checkPwdDigit())
                    .and(checkPwdLowerCase().or(checkPwdUpperCase()));

    /** Minimum length for passwords. */
    private static final int MIN_PASSWORD_LENGTH = 7;

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
    public View onCreateView(@NonNull final LayoutInflater inflater, final ViewGroup container,
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

        mModel.removeResponseObserver(this::observeResponse);
        mModel.addResponseObserver(getViewLifecycleOwner(),
                this::observeResponse);

        String name = mModel.getFirstName() + " " + mModel.getLastName();
        mBinding.nameField.setText(name);
        mBinding.usernameField.setText(mModel.getUsername());
        mBinding.emailField.setText(mModel.getEmail());

        mBinding.buttonChangePassword.setOnClickListener(button -> this.validatePassword());
    }

    /**
     * Functionality for user to change password.
     * Checks if new password is valid, and verifies old password with server.
     */
    private void validatePassword() {
        if (!mBinding.newPassword1.getText().toString().trim()
                .equals(mBinding.newPassword2.getText().toString().trim())) {
            mBinding.newPassword1.setError(getText(R.string.profile_fragment_passwordNoMatch));
            createAlertDialogue(getText(R.string.profile_fragment_passwordNoMatch).toString());
            return;
        }

        //is new pw valid, then go
        mPasswordValidator.processResult(
                mPasswordValidator.apply(mBinding.newPassword1.getText().toString()),
                () -> mModel.connectValidatePassword(mBinding.oldPassword.getText().toString().trim(),
                        mBinding.newPassword1.getText().toString().trim()),
                (result) -> {
                    mBinding.newPassword1.setError(getText(R.string.profile_fragment_passwordWeak));
                    createAlertDialogue(getText(R.string.profile_fragment_passwordWeak).toString());
                });
    }

    /**
     * Observe network responses from UserInfoViewModel.
     * These responses are all related to password changing.
     * @param response network response packaged as JSON.
     */
    private void observeResponse(final JSONObject response) {
        if (response.length() > 0) {
            if (response.has("code")) {
                try {
                    mBinding.newPassword1.setError("Error Authenticating: "
                            + response.getString("message"));
                } catch (JSONException e) {
                    Log.e("JSON Parse Error", e.getMessage());
                    createAlertDialogue(getText(R.string.profile_fragment_serverError).toString());
                }
            } else {
                try {
                    if (response.has("message") //This is the get user data success
                            && response.getString("message").equals("PUT SUCCESS")) {
                        return;
                    }

                    //This is password set success
                    createAlertDialogue(getText(R.string.profile_fragment_passwordSuccess).toString());
                    mBinding.newPassword1.setText("");
                    mBinding.newPassword2.setText("");
                    mBinding.oldPassword.setText("");
                } catch (JSONException e) {
                    Log.e("JSON Parse Error in ProfileFrag.observeResponse: ", e.getMessage());
                    createAlertDialogue(getText(R.string.profile_fragment_serverError).toString());
                }
            }
        } else {
            Log.d("JSON Response", "No Response!!");
            createAlertDialogue(getText(R.string.profile_fragment_serverError).toString());
        }
    }

    /**
     * Dialogs that are used to notify user of password events.
     * @param message message to display to user.
     */
    private void createAlertDialogue(final String message) {
        AlertDialog.Builder builder1 = new AlertDialog.Builder(getActivity());
        builder1.setMessage(message);
        builder1.setCancelable(true);

        builder1.setPositiveButton(
                "Ok",
                (dialog, id) -> dialog.cancel());

//        builder1.setNegativeButton(
//                "No",
//                (dialog, id) -> dialog.cancel());

        AlertDialog alert11 = builder1.create();
        alert11.show();
    }
}
