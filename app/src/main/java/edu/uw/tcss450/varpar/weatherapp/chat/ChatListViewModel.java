package edu.uw.tcss450.varpar.weatherapp.chat;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import edu.uw.tcss450.varpar.weatherapp.R;
import edu.uw.tcss450.varpar.weatherapp.io.RequestQueueSingleton;

public class ChatListViewModel extends AndroidViewModel {

    private MutableLiveData<List<ChatListRoom>> mChatList;

    /** Network responses, for observer. */
    private MutableLiveData<JSONObject> mResponse;

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

    /**
     * Add observer to network responses.
     * @param owner Lifecycle parent.
     * @param observer UserInfoViewModel.class
     */
    public void addResponseObserver(final @NonNull LifecycleOwner owner,
                                    final @NonNull Observer<? super JSONObject> observer) {
        mResponse = new MutableLiveData<>();
        mResponse.observe(owner, observer);
    }

    /**
     * Remove observer to network responses.
     * @param observer UserInfoViewModel.class
     */
    public void removeResponseObserver(final @NonNull Observer<? super JSONObject> observer) {
        mResponse.removeObserver(observer);
    }

    public List<ChatListRoom> getChatList(){
        return mChatList.getValue();
    }

    public boolean isEmpty(){
        return mChatList.getValue().isEmpty();
    }

    public void connectGetChatIds(final String memberId, final String jwt) {
        String url =
                getApplication()
                    .getResources()
                    .getString(R.string.url)
                + "chats/memberId=" + memberId;

        Request<JSONObject> request = new JsonObjectRequest(
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

    /**
     * Connect to server to add new chat.
     * @param chatName Name of chat to add.
     */
    public void connectAddChatWithName(final String chatName, final String jwt) {
        String url = getApplication().getResources().getString(R.string.url) + "chats/";

        JSONObject body = new JSONObject();
        try {
            body.put("name", chatName);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Request<JSONObject> request = new JsonObjectRequest(
            Request.Method.POST,
            url,
            body,
            this::handleSuccessForPost,
            this::handleErrorForPost
        ){
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                // add headers <key,value>
                headers.put("Authorization", jwt);
                return headers;
            }
        };

        request.setRetryPolicy(new DefaultRetryPolicy(
            10_000,
            DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        ));

        //Instantiate the RequestQueue and add the request to the queue
        RequestQueueSingleton.getInstance(
            getApplication().getApplicationContext()
        ).addToRequestQueue(request);
    }



    /**
     * Success handler for adding contact.
     * @param response server response.
     */
    private void handleSuccessForPost(final JSONObject response) {
        try { //note what type of connection happened
            response.put("type", "post");
        } catch (JSONException e) {
            Log.e("JSON Error", e.getMessage());
        }
        mResponse.setValue(response);
    }

    /**
     * Error handler for adding contact.
     * @param error server response.
     */
    private void handleErrorForPost(final VolleyError error) {
        JSONObject resp = new JSONObject();
        try { //note what type of connection happened
            resp.put("type", "post");
        } catch (JSONException e) {
            Log.e("JSON Error", e.getMessage());
        }

        if (Objects.isNull(error.networkResponse)) { //server error?
            Log.e("NETWORK ERROR", error.getMessage());
            try {
                resp.put("message", "Network Error");
            } catch (JSONException e) {
                Log.e("JSON Error", e.getMessage());
            }
        } else { //client error?
            String data = new String(error.networkResponse.data, Charset.defaultCharset());
            Log.e("CLIENT ERROR",
                    error.networkResponse.statusCode + " " + data);
            try {
                JSONObject dat = new JSONObject(new String(error.networkResponse.data, Charset.defaultCharset()));
                resp.put("message", dat.getString("message"));
            } catch (JSONException e) {
                Log.e("JSON Error", e.getMessage());
            }
        }

        //notify
        mResponse.setValue(resp);
    }

    /**
     * Connect to server to add new chat.
     * @param chatId Name of chat to add.
     */
    public void connectPutMeInChat(final String chatId, final String jwt) {
        String url =
            getApplication().getResources().getString(R.string.url)
            + "chats/"
            + chatId
        ;

        Request<JSONObject> request = new JsonObjectRequest(
                Request.Method.PUT,
                url,
                null,
                this::handleSuccessForPut,
                this::handleErrorForPut
        ){
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                // add headers <key,value>
                headers.put("Authorization", jwt);
                return headers;
            }
        };

        request.setRetryPolicy(new DefaultRetryPolicy(
                10_000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        ));

        //Instantiate the RequestQueue and add the request to the queue
        RequestQueueSingleton.getInstance(
                getApplication().getApplicationContext()
        ).addToRequestQueue(request);
    }



    /**
     * Success handler for adding contact.
     * @param response server response.
     */
    private void handleSuccessForPut(final JSONObject response) {
        try { //note what type of connection happened
            response.put("type", "put");
            response.put("success", "true");
        } catch (JSONException e) {
            Log.e("JSON Error", e.getMessage());
        }
        mResponse.setValue(response);
    }

    /**
     * Error handler for adding contact.
     * @param error server response.
     */
    private void handleErrorForPut(final VolleyError error) {
        JSONObject resp = new JSONObject();
        try { //note what type of connection happened
            resp.put("type", "put");
        } catch (JSONException e) {
            Log.e("JSON Error", e.getMessage());
        }

        if (Objects.isNull(error.networkResponse)) { //server error?
            Log.e("NETWORK ERROR", error.getMessage());
            try {
                resp.put("message", "Network Error");
            } catch (JSONException e) {
                Log.e("JSON Error", e.getMessage());
            }
        } else { //client error?
            String data = new String(error.networkResponse.data, Charset.defaultCharset());
            Log.e("CLIENT ERROR",
                    error.networkResponse.statusCode + " " + data);
            try {
                JSONObject dat = new JSONObject(new String(error.networkResponse.data, Charset.defaultCharset()));
                resp.put("message", dat.getString("message"));
            } catch (JSONException e) {
                Log.e("JSON Error", e.getMessage());
            }
        }

        //notify
        mResponse.setValue(resp);
    }

    /**
     * Connect to server to delete contact.
     * @param chatId identifies chat to remove user from
     * @param email identifies the user to delete from chat
     */
    public void connectDeleteUserFromChat(final String chatId, final String email, final String jwt) {
        String url =
            getApplication().getResources().getString(R.string.url)
            + "chats/delete/"
            + chatId + "/"
            + email
        ;

        Request<JSONObject> request = new JsonObjectRequest(
            Request.Method.DELETE,
            url,
            null,
            this::handleSuccessForDelete,
            this::handleErrorForDelete
        ){
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                // add headers <key,value>
                headers.put("Authorization", jwt);
                return headers;
            }
        };

        request.setRetryPolicy(new DefaultRetryPolicy(
            10_000,
            DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        ));

        //Instantiate the RequestQueue and add the request to the queue
        RequestQueueSingleton.getInstance(
            getApplication().getApplicationContext()
        ).addToRequestQueue(request);

    }

    /**
     * Success handler for deleting contact.
     * @param response server response.
     */
    private void handleSuccessForDelete(final JSONObject response) {
        try { //note what type of connection happened
            response.put("type", "delete");
            response.put("success", "true");
        } catch (JSONException e) {
            Log.e("JSON Error", e.getMessage());
        }
        mResponse.setValue(response);
    }

    /**
     * Error handler for deleting contact.
     * @param error server response.
     */
    private void handleErrorForDelete(final VolleyError error) {
        JSONObject resp = new JSONObject();
        try { //note what type of connection happened
            resp.put("type", "delete");
            resp.put("success", "false");
        } catch (JSONException e) {
            Log.e("JSON Error", e.getMessage());
        }

        if (Objects.isNull(error.networkResponse)) { //server error?
            Log.e("NETWORK ERROR", error.getMessage());
            try {
                resp.put("message", "Network Error");
            } catch (JSONException e) {
                Log.e("JSON Error", e.getMessage());
            }
        } else { //client error?
            String data = new String(error.networkResponse.data, Charset.defaultCharset());
            Log.e("CLIENT ERROR",
                    error.networkResponse.statusCode + " " + data);
            try {
                JSONObject dat = new JSONObject(new String(error.networkResponse.data, Charset.defaultCharset()));
                resp.put("message", dat.getString("message"));
            } catch (JSONException e) {
                Log.e("JSON Error", e.getMessage());
            }
        }

        //notify
        mResponse.setValue(resp);
    }


}
