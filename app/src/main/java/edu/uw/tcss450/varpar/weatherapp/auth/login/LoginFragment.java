package edu.uw.tcss450.varpar.weatherapp.auth.login;

import android.app.AlertDialog;
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


import org.json.JSONException;
import org.json.JSONObject;

import edu.uw.tcss450.varpar.weatherapp.databinding.FragmentLoginBinding;
import edu.uw.tcss450.varpar.weatherapp.model.UserInfoViewModel;
import edu.uw.tcss450.varpar.weatherapp.util.PasswordValidator;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link LoginFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LoginFragment extends Fragment {
    private FragmentLoginBinding mBinding;
    private LoginViewModel mLoginVModel;
    private UserInfoViewModel model;

    private PasswordValidator mEmailValidator = checkPwdLength(2)
            .and(checkExcludeWhiteSpace())
            .and(checkPwdSpecialChar("@"));
    private PasswordValidator mPassWordValidator = checkPwdLength(6)
            .and(checkExcludeWhiteSpace());
    public LoginFragment() {
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
        mBinding = FragmentLoginBinding.inflate(inflater);
        mLoginVModel = new ViewModelProvider(getActivity())
                .get(LoginViewModel.class);
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mBinding.buttonRegister.setOnClickListener(button -> {
//                Navigation.findNavController(getView()).navigate(edu.uw.tcss450.varpar.weatherapp.login.LoginFragmentDirections.actionLoginFragmentToRegisterFragment());
//            Navigation.findNavController(getView()).navigate(edu.uw.tcss450.varpar.weatherapp.LoginFragmentDirections.actionLoginFragmentToRegisterFragment());
            Navigation.findNavController(getView()).navigate(edu.uw.tcss450.varpar.weatherapp.auth.login.LoginFragmentDirections.actionLoginFragmentToRegisterFragment());

        });

        mLoginVModel.addResponseObserver(
                getViewLifecycleOwner(),
                this::loginResponseObserver);




        mBinding.buttonLogin.setOnClickListener(this::attemptSignIn);
//        mBinding.buttonLogin.setOnClickListener(button -> {
//            if(mLoginVModel.getmValidLogin()) {
//                Navigation.findNavController(getView()).navigate(
//                        edu.uw.tcss450.varpar.weatherapp.auth.login.LoginFragmentDirections.actionLoginFragmentToMainActivity2());
//            }
//        });

        autoLogin(); //REMOVE WHEN DONE
    }
    private void autoLogin() {
        //EASE OF LOGGIN IN
        mBinding.etEmail.setText("mom@gmail.com");
        mBinding.etPassword.setText("Test123!");
//        attemptSignIn(mBinding.buttonLogin);
        //EASE OF LOGGING IN
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
                    if(String.valueOf(response.get("code")).equals("401")) {
                        AlertDialog.Builder dlgAlert  = new AlertDialog.Builder(getContext());
                        dlgAlert.setMessage(response.getJSONObject("data").getString("message"));
                        dlgAlert.setTitle("Please Verify");
                        dlgAlert.setPositiveButton("OK", null);
                        dlgAlert.setCancelable(true);
                        dlgAlert.create().show();
                    }else{
                    mBinding.etEmail.setError(
                            "Error Authenticating: " +
                                    response.getJSONObject("data").getString("message"));

                    }

                } catch (JSONException e) {
                    Log.e("JSON Parse Error in login response observer", e.getMessage());
                }
            }else {
                //Taking the user's json and passing to the other activity
                navigateToSuccess(response.toString());

            }
        } else {
            Log.d("JSON Response in login response observer", "No Response");
        }

    }
    private void navigateToSuccess(final String json) {
        Navigation.findNavController(getView()).navigate(LoginFragmentDirections.actionLoginFragmentToMainActivity2(json));
    }

}