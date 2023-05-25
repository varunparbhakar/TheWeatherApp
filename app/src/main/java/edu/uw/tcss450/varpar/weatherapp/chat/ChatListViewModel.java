package edu.uw.tcss450.varpar.weatherapp.chat;
import static com.android.volley.DefaultRetryPolicy.*;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.IntFunction;

import edu.uw.tcss450.varpar.weatherapp.MainActivityArgs;
import edu.uw.tcss450.varpar.weatherapp.R;
import edu.uw.tcss450.varpar.weatherapp.model.UserInfoViewModel;

public class ChatListViewModel extends AndroidViewModel {

    private MutableLiveData<List<ChatListRoom>> mChatList;

    private UserInfoViewModel mUser;

    public ChatListViewModel(@NonNull Application application) {
        super(application);
        mChatList = new MutableLiveData<>();
        mChatList.setValue(new ArrayList<>());
        mUser = new ViewModelProvider(getApplication()).get(UserInfoViewModel.class);
    }

    public void addChatListObserver(@NonNull LifecycleOwner owner,
                                    @NonNull Observer<? super List<ChatListRoom>> observer) {
        mChatList.observe(owner, observer);
    }

    private void handleSuccessForGetChatIds(final JSONObject response) {
        List<ChatListRoom> list;
        if (!response.has("chatId")) {
            throw new IllegalStateException("Unexpected response in ChatViewModel: " + response);
        }
        try {
            ChatListRoom cRoom = new ChatListRoom(
                response.getInt("chatId"),
                response.getString("email"),
                response.getString("message")
            );
            //inform observers of the change (setValue)
//            getOrCreateMapEntry(response.getInt("chatId")).setValue(list);
        }catch (JSONException e) {
            Log.e("JSON PARSE ERROR", "Found in handle Success ChatViewModel");
            Log.e("JSON PARSE ERROR", "Error: " + e.getMessage());
        }
    }

    private void handleErrorForGetChatIds(final VolleyError error) {
        if (Objects.isNull(error.networkResponse)) {
            Log.e("NETWORK ERROR", error.getMessage());
        }
        else {
            String data = new String(error.networkResponse.data, Charset.defaultCharset());
            Log.e("CLIENT ERROR",
                    error.networkResponse.statusCode +
                            " " +
                            data);
        }
    }


}
