package edu.uw.tcss450.varpar.weatherapp.login;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import org.json.JSONObject;

public class LoginViewModel extends AndroidViewModel {
    private MutableLiveData<Boolean> mValidLogin;
    public LoginViewModel(@NonNull Application application) {
        super(application);
        mValidLogin = new MutableLiveData<>();
        mValidLogin.setValue(false);

    }
    public boolean getmValidLogin() {
        return mValidLogin.getValue();
    }

    public void setmValidLogin(boolean value) {
        mValidLogin.setValue(value);
    }

}
