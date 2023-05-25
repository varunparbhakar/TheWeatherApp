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
import edu.uw.tcss450.varpar.weatherapp.io.RequestQueueSingleton;
import edu.uw.tcss450.varpar.weatherapp.model.UserInfoViewModel;

public class ChatListViewModel extends AndroidViewModel {

    private MutableLiveData<List<ChatListRoom>> mChatList;

    public ChatListViewModel(@NonNull Application application) {
        super(application);
        mChatList = new MutableLiveData<>();
        mChatList.setValue(new ArrayList<>());
    }

    public void addChatListObserver(
            @NonNull LifecycleOwner owner,
            @NonNull Observer<? super List<ChatListRoom>> observer
    ) {
        mChatList.observe(owner, observer);
    }

    public List<ChatListRoom> getChatList(){
        return mChatList.getValue();
    }

    public boolean isEmpty(){
        return mChatList.getValue().isEmpty();
    }

    public void getChatIds(final String memberId, final String jwt) {
        String url =
                getApplication()
                    .getResources()
                    .getString(R.string.url)
                + "chats/memberId=" + memberId;

        Request request = new JsonObjectRequest(
            Request.Method.GET,
            url,
            null,
            this::handleSuccessForGetChatIds,
            this::handleErrorForGetChatIds
        ){
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                // add headers <key,value>
                headers.put("Authorization", jwt);
                return headers;
            }
        };

        request.setRetryPolicy(
            new DefaultRetryPolicy(
                10_000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
            )
        );

        //Instantiate the RequestQueue and add the request to the queue
        RequestQueueSingleton.getInstance(
                        getApplication().getApplicationContext()
                ).addToRequestQueue(request);

        //code here will run
    }

    private void handleSuccessForGetChatIds(final JSONObject response) {
        List<ChatListRoom> list = new ArrayList<>();

        try {
            JSONArray rooms = response.getJSONArray("rows");
            for(int i = 0; i < rooms.length(); i++) {
                JSONObject room = rooms.getJSONObject(i);
                ChatListRoom cRoom = new ChatListRoom(
                    room.getString("chatid"),
                    room.getString("name")
                );
                list.add(cRoom);
                Log.d(
                        "Endpoint Response",
                        "ChatList JSONArray Row: " + cRoom.getChatId() + ", " + cRoom.getName()
                );
            }
            //inform observers of the change (setValue)
            mChatList.setValue(list);
            Log.d(
                    "Endpoint Response",
                    "ChatList Array Size: " + mChatList.getValue().size()
            );
        } catch (JSONException e) {
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
