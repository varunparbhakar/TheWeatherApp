package edu.uw.tcss450.varpar.weatherapp.auth.login;

import android.os.Bundle;
import static edu.uw.tcss450.varpar.weatherapp.util.PasswordValidator.*;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import edu.uw.tcss450.varpar.weatherapp.databinding.FragmentLoginBinding;
import edu.uw.tcss450.varpar.weatherapp.util.PasswordValidator;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link LoginFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LoginFragment extends Fragment {
    private FragmentLoginBinding mBinding;
    private LoginViewModel mLoginVModel;

    private PasswordValidator mEmailValidator = checkPwdLength(4)
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
                Navigation.findNavController(getView()).navigate(edu.uw.tcss450.varpar.weatherapp.login.LoginFragmentDirections.actionLoginFragmentToRegisterFragment());
//            Navigation.findNavController(getView()).navigate(edu.uw.tcss450.varpar.weatherapp.LoginFragmentDirections.actionLoginFragmentToRegisterFragment());

            });

        mBinding.buttonLogin.setOnClickListener(button -> {
            attemptSignIn();
            if(mLoginVModel.getmValidLogin()) {
                Navigation.findNavController(getView()).navigate(
                        edu.uw.tcss450.varpar.weatherapp.login.LoginFragmentDirections.actionLoginFragmentToMainActivity2());
            }
        });
    }
    private void attemptSignIn() {
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
        mLoginVModel.setmValidLogin(true);


        //        mSignInModel.connect(
//                binding.editEmail.getText().toString(),
//                binding.editPassword.getText().toString());
//        //This is an Asynchronous call. No statements after should rely on the
//        //result of connect().
    }
}