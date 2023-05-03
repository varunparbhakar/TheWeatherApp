package edu.uw.tcss450.varpar.weatherapp.auth.register;

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
 * A simple {@link Fragment} subclass.
 * Use the {@link RegisterFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RegisterFragment extends Fragment {
    private FragmentRegisterBinding mBinding;
    private RegisterViewModel mRegisterModel;

    private PasswordValidator mNameValidator = checkPwdLength(1);

    private PasswordValidator mEmailValidator = checkPwdLength(2)
            .and(checkExcludeWhiteSpace())
            .and(checkPwdSpecialChar("@"));

    private PasswordValidator mPassWordValidator =
            checkClientPredicate(pwd -> pwd.equals(mBinding.etPassword.getText().toString()))
                    .and(checkPwdLength(7))
                    .and(checkPwdSpecialChar())
                    .and(checkExcludeWhiteSpace())
                    .and(checkPwdDigit())
                    .and(checkPwdLowerCase().or(checkPwdUpperCase()));

    public RegisterFragment() {
        // Required empty public constructor
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
        mRegisterModel.addResponseObserver(getViewLifecycleOwner(),
                this::observeResponse);
        mBinding.buttonRegister.setOnClickListener(this::attemptRegister);
    }
    private void attemptRegister(final View button) {
        validateFirst();
    }
    private void validateFirst() {
        mNameValidator.processResult(
                mNameValidator.apply(mBinding.etFirstName.getText().toString().trim()),
                this::validateLast,
                result -> mBinding.etFirstName.setError("Please enter a first name."));
    }
    private void validateLast() {
        mNameValidator.processResult(
                mNameValidator.apply(mBinding.etLastName.getText().toString().trim()),
                this::validateEmail,
                result -> mBinding.etLastName.setError("Please enter a last name."));
    }
    private void validateEmail() {
        mEmailValidator.processResult(
                mEmailValidator.apply(mBinding.etEmailText.getText().toString().trim()),
                this::validatePasswordsMatch,
                result -> mBinding.etEmailText.setError("Please enter a valid Email address."));
    }
    private void validatePasswordsMatch() {
        PasswordValidator matchValidator =
                checkClientPredicate(
                        pwd -> pwd.equals(mBinding.etConfirmPassword.getText().toString().trim()));

        mEmailValidator.processResult(
                matchValidator.apply(mBinding.etPassword.getText().toString().trim()),
                this::validatePassword,
                result -> mBinding.etPassword.setError("Passwords must match."));
    }
    private void validatePassword() {
        mPassWordValidator.processResult(
                mPassWordValidator.apply(mBinding.etPassword.getText().toString()),
                this::verifyAuthWithServer,
                result -> mBinding.etPassword.setError("Please enter a valid Password."));
    }
    private void verifyAuthWithServer() {
        mRegisterModel.connect(
                mBinding.etFirstName.getText().toString(),
                mBinding.etLastName.getText().toString(),
                mBinding.etEmailText.getText().toString(),
                mBinding.etPassword.getText().toString());
        //This is an Asynchronous call. No statements after should rely on the
        //result of connect().

    }
    private void observeResponse(final JSONObject response) {
        if (response.length() > 0) {
            if (response.has("code")) {
                try {
                    mBinding.etEmailText.setError(
                            "Error Authenticating: " +
                                    response.getJSONObject("data").getString("message"));
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
    private void navigateToLogin() {

//        RegisterFragmentDirections.ActionRegisterFragmentToLoginFragment directions =
//                RegisterFragmentDirections.actionRegisterFragmentToLoginFragment();
//
//        directions.setEmail(binding.editEmail.getText().toString());
//        directions.setPassword(binding.editPassword1.getText().toString());

        Navigation.findNavController(getView()).navigate(RegisterFragmentDirections.actionRegisterFragmentToLoginFragment());

    }
}