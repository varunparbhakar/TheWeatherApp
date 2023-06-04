package edu.uw.tcss450.varpar.weatherapp.auth.login;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import static edu.uw.tcss450.varpar.weatherapp.util.PasswordValidator.*;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.auth0.android.jwt.JWT;

import org.json.JSONException;
import org.json.JSONObject;

import edu.uw.tcss450.varpar.weatherapp.R;
import edu.uw.tcss450.varpar.weatherapp.databinding.FragmentLoginBinding;
import edu.uw.tcss450.varpar.weatherapp.model.PushyTokenViewModel;
import edu.uw.tcss450.varpar.weatherapp.model.UserInfoViewModel;
import edu.uw.tcss450.varpar.weatherapp.util.PasswordValidator;

/**
 * Login fragment.
 */
public class LoginFragment extends Fragment {

    /** Binding for views. */
    private FragmentLoginBinding mBinding;

    /** View model for login data flow. */
    private LoginViewModel mLoginVModel;

    /** View model for user information. */
    private UserInfoViewModel mUserViewModel;

    /** View model for pushy functionality. */
    private PushyTokenViewModel mPushyTokenViewModel;

    /** Minimum password length. */
    private static final int PASSWORD_MIN_LENGTH = 6;

    /** Ensures email length, no whitespace, and has at-sign char. */
    private final PasswordValidator mEmailValidator = checkPwdLength(2)
            .and(checkExcludeWhiteSpace())
            .and(checkPwdSpecialChar("@"));

    /** Ensures password length. */
    private final PasswordValidator mPassWordValidator = checkPwdLength(PASSWORD_MIN_LENGTH)
            .and(checkExcludeWhiteSpace());

    /** Required empty constructor. */
    public LoginFragment() {
        //Empty on purpose.
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.i("Oncreate", "OnCreate just started");
        super.onCreate(savedInstanceState);
        mPushyTokenViewModel = new ViewModelProvider(getActivity())
                .get(PushyTokenViewModel.class);
        mLoginVModel = new ViewModelProvider(getActivity())
                .get(LoginViewModel.class);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mBinding = FragmentLoginBinding.inflate(inflater);

        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mBinding.buttonRegister.setOnClickListener(button -> {
            Navigation.findNavController(getView()).navigate(edu.uw.tcss450.varpar.weatherapp.auth.login.LoginFragmentDirections.actionLoginFragmentToRegisterFragment());

        });

        mLoginVModel.addResponseObserver(
                getViewLifecycleOwner(),
                this::loginResponseObserver
        );

        mBinding.buttonLogin.setOnClickListener(this::attemptSignIn);

        mBinding.buttonForgotpassword.setOnClickListener(button -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
            builder.setTitle("Forget you password");

            // Create an EditText view to accept email input
            final EditText emailEditText = new EditText(requireContext());
            emailEditText.setHint(getResources().getString(R.string.profile_fragment_username));
            builder.setView(emailEditText);

            // Set up the Submit button
            builder.setPositiveButton(getResources().getString(R.string.text_activity_auth_forget_password_submit), (dialog, which) -> {
                String email = emailEditText.getText().toString();
                mLoginVModel.forgetpasswordconnect(email);
                Toast.makeText(requireContext(), "Please check you email", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            });

            // Set up the Cancel button
            builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

            // Show the dialog
            AlertDialog dialog = builder.create();
            dialog.show();

        });

        //don't allow sign in until pushy token retrieved
        mPushyTokenViewModel.addTokenObserver(getViewLifecycleOwner(), token ->
                mBinding.buttonLogin.setEnabled(!token.isEmpty()));

        mPushyTokenViewModel.addResponseObserver(
                getViewLifecycleOwner(),
                this::observePushyPutResponse);

        autoLogin(); //REMOVE WHEN DONE
    }

    /**
     * Helper to abstract the request to send the pushy token to the web service.
     */
    private void sendPushyToken() {
        mPushyTokenViewModel.sendTokenToWebservice(mUserViewModel.getJwt());
    }

    /**
     * An observer on the HTTP Response from the web server. This observer should be
     * attached to PushyTokenViewModel.
     *
     * @param response the Response from the server
     */
    private void observePushyPutResponse(final JSONObject response) {
        if (response.length() > 0) {
            if (response.has("code")) {
                //this error cannot be fixed by the user changing credentials...
                mBinding.etEmail.setError(
                        "Error Authenticating on Push Token. Please contact support");
            } else if (response.has("success")) {
                Log.i("Pushy Observer", response.toString());
                navigateToSuccess(
                        mUserViewModel.getJsonString()
                );
            } else {
                Log.e("ERROR", "observePushyPutResponse: Pushy returned an unexpected response");
            }
        }
    }

    private void autoLogin() {
        //EASE OF LOGGIN IN
        mBinding.etEmail.setText("test1@test.com");
        mBinding.etPassword.setText("test1First1666");
        //EASE OF LOGGING IN
    }

    @Override
    public void onStart() {
        Log.i("On Start", "onStart was invoked");
        super.onStart();
        SharedPreferences prefs =
                getActivity().getSharedPreferences(
                        getString(R.string.keys_shared_prefs),
                        Context.MODE_PRIVATE);
        if (prefs.contains(getString(R.string.keys_prefs_jwt))) {
            Log.i("Local Data", "There was data stored on the phone");
            JSONObject json;
            String token;
            try {

                json = new JSONObject(prefs.getString(getString(R.string.keys_prefs_jwt), ""));
                Log.i("DISPLAYING STORED JSON", json.toString());

                token = json.getString("token");
                JWT jwt = new JWT(token);
                // Check to see if the web token is still valid or not. To make a JWT expire after a
                // longer or shorter time period, change the expiration time when the JWT is
                // created on the web service.
                if (!jwt.isExpired(0)) {
                    Log.i("Token is not EXPIRED", String.valueOf(jwt.isExpired(0)));
                    navigateToSuccess(json.toString());
                    return;
                }
                Log.i("Token is EXPIRED", String.valueOf(jwt.isExpired(0)));

            } catch (JSONException e) {
                Log.e("ERROR", "Error converting string back into JSON");
                prefs.edit().remove(getString(R.string.keys_prefs_jwt)).apply();
            }

        }
        Log.i("Local Data", "There was no data stored on the phone");
    }

    private void attemptSignIn(final View button) {
        validateEmail();
    }
    private void validateEmail() {
        mEmailValidator.processResult(
                mEmailValidator.apply(mBinding.etEmail.getText().toString().trim()),
                this::validatePassword,
                result -> mBinding.etEmail.setError("Please enter a valid Email address."));
    }
    private void validatePassword() {
        mPassWordValidator.processResult(
                mPassWordValidator.apply(mBinding.etPassword.getText().toString()),
                this::verifyAuthWithServer,
                result -> mBinding.etPassword.setError("Please enter a valid Password."));
    }
    private void verifyAuthWithServer() {
        mLoginVModel.connect(
                mBinding.etEmail.getText().toString(),
                mBinding.etPassword.getText().toString());

        //This is an Asynchronous call. No statements after should rely on the
        //result of connect().
    }
    private void loginResponseObserver(final JSONObject response) {
        if (response.length() > 0) {
            if (response.has("code")) {
                try {
                    if (String.valueOf(response.get("code")).equals("401")) {
                        AlertDialog.Builder dlgAlert  = new AlertDialog.Builder(getContext());
                        dlgAlert.setMessage(response.getJSONObject("data").getString("message"));
                        dlgAlert.setTitle("Please Verify");
                        dlgAlert.setPositiveButton("OK", null);
                        dlgAlert.setCancelable(true);
                        dlgAlert.create().show();
                    } else {
                        mBinding.etEmail.setError(
                                "Error Authenticating: "
                                        + response.getJSONObject("data").getString("message"));

                    }

                } catch (JSONException e) {
                    Log.e("JSON Parse Error in login response observer", e.getMessage());
                }
            } else if (response.has("message")) {
                try {
                    if (response.getString("message").equals("A password recovery email has been sent")) {
                        Log.i("Login", "User forgot their password");
                    } else if ((response.getString("message")).equals("Authentication successful!")) {
                        Log.i("Login", "Auth was successfull");
                        try {
                            mUserViewModel = new ViewModelProvider(getActivity()).get(UserInfoViewModel.class);
                            mUserViewModel.setJSON(response);
                            sendPushyToken();
                        } catch (Exception e) {
                            Log.e("Login", "Error setting the JSON to the user model in response");
                        }

//                            navigateToSuccess(response.toString());
                    }

                } catch (JSONException e) {
                    Log.e("JSON Parse Error in login response observer", e.getMessage());
                }

            } else {
                //Taking the user's json and passing to the other activity
                Log.i("Login", "Logging in from the response observer");
                Log.i("Login", "This is whats being sent to navigate to success " + response.toString());
                navigateToSuccess(response.toString());

            }
        } else {
            Log.d("JSON Response in login response observer", "No Response");
        }

    }
    private void navigateToSuccess(final String json) {
        Log.i("NavigateToSuccess", "Is the remember me switch turned on? " + mBinding.switchSignin.isChecked());
        Log.i("NavigateToSuccess", "This is getting stored on local device" + json);
        if (mBinding.switchSignin.isChecked()) {
            Log.i("Remember me Switch", "Switch is on");
            SharedPreferences prefs =
                    getActivity().getSharedPreferences(
                            getString(R.string.keys_shared_prefs),
                            Context.MODE_PRIVATE);
            //Store the credentials in SharedPrefs
            prefs.edit().putString(getString(R.string.keys_prefs_jwt), json).apply();
        }
        Navigation.findNavController(getView()).navigate(LoginFragmentDirections.actionLoginFragmentToMainActivity2(json));

        //Remove THIS activity from the Task list. Pops off the backstack
        getActivity().finish();
    }
}
