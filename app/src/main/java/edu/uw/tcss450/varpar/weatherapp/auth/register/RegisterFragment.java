package edu.uw.tcss450.varpar.weatherapp.auth.register;

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
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.json.JSONException;
import org.json.JSONObject;

import edu.uw.tcss450.varpar.weatherapp.databinding.FragmentRegisterBinding;
import edu.uw.tcss450.varpar.weatherapp.util.PasswordValidator;

/**
 * Fragment for user registration.
 */
public class RegisterFragment extends Fragment {

    /** Binding for view objects in fragment. */
    private FragmentRegisterBinding mBinding;

    /** View model for registration and connection. */
    private RegisterViewModel mRegisterModel;

    /** Ensures minimum name length. */
    private final PasswordValidator mNameValidator = checkPwdLength(0);

    /** Ensures email proper form. */
    private final PasswordValidator mEmailValidator = checkPwdLength(2)
            .and(checkExcludeWhiteSpace())
            .and(checkPwdSpecialChar("@"));

    /** Ensures username minimum length. */
    private final PasswordValidator mUsernameValidator = checkPwdLength(2);

    /** Ensures password minimum length, valid character set, and complexity. */
    private final PasswordValidator mPassWordValidator =
            checkClientPredicate(pwd -> pwd.equals(mBinding.etPassword.getText().toString()))
                    .and(checkPwdLength(PASSWORD_MIN_LENGTH))
                    .and(checkPwdSpecialChar())
                    .and(checkExcludeWhiteSpace())
                    .and(checkPwdDigit())
                    .and(checkPwdLowerCase().or(checkPwdUpperCase()));

    /** Minimum length for passwords. */
    private static final int PASSWORD_MIN_LENGTH = 6;

    /** Required empty public constructor. */
    public RegisterFragment() {
        //Purposefully left blank.
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mRegisterModel = new ViewModelProvider(getActivity())
                .get(RegisterViewModel.class);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mBinding = FragmentRegisterBinding.inflate(inflater);
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mBinding.etFirstName.requestFocus();
        mRegisterModel.addResponseObserver(getViewLifecycleOwner(),
                this::observeResponse);
        mBinding.buttonRegister.setOnClickListener(this::attemptRegister);

        mBinding.buttonNameHint.setOnClickListener(v -> {
            mBinding.etFirstName.requestFocus();
            AlertDialog.Builder dlgAlert  = new AlertDialog.Builder(getContext());
            dlgAlert.setMessage("- You name can has to be at least 1 character");
            dlgAlert.setTitle("Name Requirements");
            dlgAlert.setPositiveButton("OK", null);
            dlgAlert.setCancelable(true);
            dlgAlert.create().show();
        });

        mBinding.buttonUserNameHint.setOnClickListener(v -> {
            mBinding.etUserName.requestFocus();
            AlertDialog.Builder dlgAlert  = new AlertDialog.Builder(getContext());
            dlgAlert.setMessage("- User name should be at least 3 characters "
                    + "\n- User name can contain numbers"
                    + "\n- User name can contain special characters");
            dlgAlert.setTitle("User Name Requirements");
            dlgAlert.setPositiveButton("OK", null);
            dlgAlert.setCancelable(true);
            dlgAlert.create().show();
        });

        mBinding.buttonEmailHint.setOnClickListener(v -> {
            mBinding.etEmailText.requestFocus();
            AlertDialog.Builder dlgAlert  = new AlertDialog.Builder(getContext());
            dlgAlert.setMessage("- Email should have at least 2 character"
                    + "\n- Email needs to include '@'");
            dlgAlert.setTitle("Email Requirements");
            dlgAlert.setPositiveButton("OK", null);
            dlgAlert.setCancelable(true);
            dlgAlert.create().show();
        });

        mBinding.buttonPasswordHint.setOnClickListener(v -> {
            mBinding.etPassword.requestFocus();
            AlertDialog.Builder dlgAlert  = new AlertDialog.Builder(getContext());
            dlgAlert.setMessage("- Email should have at least 7 character"
                    + "\n- Required a special character (@,#,$,%,&,*,!,?)"
                    + "\n- Required a single digit"
                    + "\n- Required at least 1 upper case and lower case letters");
            dlgAlert.setTitle("Password Requirements");
            dlgAlert.setPositiveButton("OK", null);
            dlgAlert.setCancelable(true);
            dlgAlert.create().show();
        });

    }

    /**
     * Begin verification of user input data for registration.
     * @param button button pressed.
     */
    private void attemptRegister(final View button) {
        validateFirst();
    }

    /**
     * Ensure first name meets standard.
     */
    private void validateFirst() {
        mNameValidator.processResult(
                mNameValidator.apply(mBinding.etFirstName.getText().toString().trim()),
                this::validateLast,
                result -> {
                    if (mBinding.etFirstName.getText().toString().length() < 1) {
                        mBinding.etFirstName.setError("Name has to be at least 1 character");
                    } else {
                        mBinding.etFirstName.setError("Enter a valid name.");
                    }
                }
        );
    }

    /**
     * Ensure last name meets standard.
     */
    private void validateLast() {
        mNameValidator.processResult(
                mNameValidator.apply(mBinding.etLastName.getText().toString().trim()),
                this::validateUserName,
                result -> {
                    if (mBinding.etLastName.getText().toString().length() < 1) {
                        mBinding.etLastName.setError("Name has to be at least 1 character");
                    } else {
                        mBinding.etLastName.setError("Enter a valid name.");
                    }
                }
        );
    }

    /**
     * Ensure username meets standard.
     */
    private void validateUserName() {
        mUsernameValidator.processResult(
                mUsernameValidator.apply(mBinding.etUserName.getText().toString().trim()),
                this::validateEmail,
                result -> {
                    if (mBinding.etUserName.getText().toString().length() < 3) {
                        mBinding.etUserName.setError("Username has to be at least 3 character");
                    } else {
                        mBinding.etUserName.setError("Enter a valid name.");
                    }
                }
        );
    }

    /**
     * Ensure email meets standard.
     */
    private void validateEmail() {
        mEmailValidator.processResult(
                mEmailValidator.apply(mBinding.etEmailText.getText().toString().trim()),
                this::validatePasswordsMatch,
                result -> mBinding.etEmailText.setError("Please enter a valid Email address."));
    }

    /**
     * Ensure password fields match.
     */
    private void validatePasswordsMatch() {
        PasswordValidator matchValidator =
                checkClientPredicate(
                        pwd -> pwd.equals(mBinding.etConfirmPassword.getText().toString().trim()));

        mEmailValidator.processResult(
                matchValidator.apply(mBinding.etPassword.getText().toString().trim()),
                this::validatePassword,
                result -> mBinding.etPassword.setError("Passwords must match."));
    }

    /**
     * Ensure password meets standard.
     */
    private void validatePassword() {
        mPassWordValidator.processResult(
                mPassWordValidator.apply(mBinding.etPassword.getText().toString()),
                this::verifyAuthWithServer,
                result -> {
                    mBinding.etPassword.setError("Please enter a valid Password.");
                });
    }

    /**
     * Calls view model to connect and register user.
     */
    private void verifyAuthWithServer() {
        mRegisterModel.connect(
                mBinding.etFirstName.getText().toString(),
                mBinding.etLastName.getText().toString(),
                mBinding.etUserName.getText().toString(),
                mBinding.etEmailText.getText().toString(),
                mBinding.etPassword.getText().toString());
        //This is an Asynchronous call. No statements after should rely on the
        //result of connect().
    }

    /**
     * Ensures registration was successful from server.
     * @param response response from server.
     */
    private void observeResponse(final JSONObject response) {
        if (response.length() > 0) {
            if (response.has("code")) {
                try {
                    mBinding.etEmailText.setError(
                            "Error Authenticating: "
                                    + response.getJSONObject("data")
                                    .getString("message"));
                } catch (JSONException e) {
                    Log.e("JSON Parse Error", e.getMessage());
                }
            } else {
                navigateToLogin();
            }
        } else {
            Log.d("JSON Response", "No Response");
        }
    }

    /**
     * Navigate to login after registration complete.
     */
    private void navigateToLogin() {
        Navigation.findNavController(getView())
                .navigate(RegisterFragmentDirections.actionRegisterFragmentToLoginFragment());
    }
}
