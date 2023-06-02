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

public class ChatRoomViewModel extends AndroidViewModel {

    /**
     * A Map of Lists of Chat Messages.
     * The Key represents the Chat ID
     * The value represents the List of (known) messages for that that room.
     */
    private Map<Integer, MutableLiveData<List<ChatRoomMessage>>> mMessages;

    /** Network responses, for observer. */
    private MutableLiveData<String> mAddedUser;

    public ChatRoomViewModel(@NonNull Application application) {
        super(application);
        mMessages = new HashMap<>();
    }

    /**
     * Register as an observer to listen to a specific chat room's list of messages.
     * @param chatId the chatid of the chat to observer
     * @param owner the fragments lifecycle owner
     * @param observer the observer
     */
    public void addMessageObserver(int chatId,
                                   @NonNull LifecycleOwner owner,
                                   @NonNull Observer<? super List<ChatRoomMessage>> observer) {
        getOrCreateMapEntry(chatId).observe(owner, observer);
    }

//    /**
//     * Add observer to network responses.
//     * @param owner Lifecycle parent.
//     * @param observer the observer
//     */
//    public void addAddChatUserObserver(final @NonNull LifecycleOwner owner,
//                                    final @NonNull Observer<? super JSONObject> observer) {
//        mAddedUser = new MutableLiveData<>();
//        mAddedUser.observe(owner, observer);
//    }

    /**
     * Return a reference to the List<> associated with the chat room. If the View Model does
     * not have a mapping for this chatID, it will be created.
     *
     * WARNING: While this method returns a reference to a mutable list, it should not be
     * mutated externally in client code. Use public methods available in this class as
     * needed.
     *
     * @param chatId the id of the chat room List to retrieve
     * @return a reference to the list of messages
     */
    public List<ChatRoomMessage> getMessageListByChatId(final int chatId) {
        return getOrCreateMapEntry(chatId).getValue();
    }

    private MutableLiveData<List<ChatRoomMessage>> getOrCreateMapEntry(final int chatId) {
        if(!mMessages.containsKey(chatId)) {
            mMessages.put(chatId, new MutableLiveData<>(new ArrayList<>()));
        }
        return mMessages.get(chatId);
    }

    /**
     * Makes a request to the web service to get the first batch of messages for a given Chat Room.
     * Parses the response and adds the ChatRoomRecyclerItem object to the List associated with the
     * ChatRoom. Informs observers of the update.
     *
     * Subsequent requests to the web service for a given chat room should be made from
     * getNextMessages()
     *
     * @param chatId the chatroom id to request messages of
     * @param jwt the users signed JWT
     */
    public void getFirstMessages(final int chatId, final String jwt) {
        String url =
                getApplication()
                    .getResources()
                    .getString(R.string.url)
                + "messages/" + chatId;

        Request request = new JsonObjectRequest(
                Request.Method.GET,
                url,
                null, //no body for this get request
                this::handleSuccessForGetFirstOrNextMessages,
                this::handleErrorForGetFirstOrNextMessages) {

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
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        //Instantiate the RequestQueue and add the request to the queue
        RequestQueueSingleton.getInstance(getApplication().getApplicationContext())
                .addToRequestQueue(request);

        //code here will run
    }

    /**
     * Makes a request to the web service to get the next batch of messages for a given Chat Room.
     * This request uses the earliest known ChatRoomRecyclerItem in the associated list and passes that
     * messageId to the web service.
     * Parses the response and adds the ChatRoomRecyclerItem object to the List associated with the
     * ChatRoom. Informs observers of the update.
     *
     * Subsequent calls to this method receive earlier and earlier messages.
     *
     * @param chatId the chatroom id to request messages of
     * @param jwt the users signed JWT
     */
    public void getNextMessages(final int chatId, final String jwt) {
        String url =
                getApplication()
                    .getResources()
                    .getString(R.string.url)
                + "messages/" + chatId + "/"
                + mMessages.get(chatId).getValue().get(0).getMessageId();

        Request request = new JsonObjectRequest(
            Request.Method.GET,
            url,
            null, //no body for this get request
            this::handleSuccessForGetFirstOrNextMessages,
            this::handleErrorForGetFirstOrNextMessages
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

    /**
     * When a chat message is received externally to this ViewModel, add it
     * with this method.
     * @param chatId
     * @param message
     */
    public void addMessage(final int chatId, final ChatRoomMessage message) {
        List<ChatRoomMessage> list = getMessageListByChatId(chatId);
        list.add(message);
        getOrCreateMapEntry(chatId).setValue(list);
    }

    private void handleSuccessForGetFirstOrNextMessages(final JSONObject response) {
        List<ChatRoomMessage> list;
        if (!response.has("chatId")) {
            throw new IllegalStateException("Unexpected response in ChatRoomViewModel: " + response);
        }
        try {
            list = getMessageListByChatId(response.getInt("chatId"));
            JSONArray messages = response.getJSONArray("rows");
            for(int i = 0; i < messages.length(); i++) {
                JSONObject message = messages.getJSONObject(i);
                ChatRoomMessage cMessage = new ChatRoomMessage(
                        message.getInt("messageid"),
                        message.getString("message"),
                        message.getString("email")
                );
                if (!list.contains(cMessage)) {
                    // don't add a duplicate
                    list.add(0, cMessage);
                } else {
                    // this shouldn't happen but could with the asynchronous
                    // nature of the application
                    Log.wtf("Chat message already received",
                            "Or duplicate id:" + cMessage.getMessageId());
                }

            }
            //inform observers of the change (setValue)
            getOrCreateMapEntry(response.getInt("chatId")).postValue(list);
        }catch (JSONException e) {
            Log.e("JSON PARSE ERROR", "Found in handle Success ChatRoomViewModel");
            Log.e("JSON PARSE ERROR", "Error: " + e.getMessage());
        }
    }

    private void handleErrorForGetFirstOrNextMessages(final VolleyError error) {
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
     * @param chatId Name of chat to add.
     */
    public void connectPutUserInChatByEmail(final int chatId, final String email, final String jwt) {
        String url =
                getApplication().getResources().getString(R.string.url)
                        + "chats/"
                        + chatId;

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
            mAddedUser.setValue(response.getString("email"));
        } catch (JSONException e) {
            Log.e("JSON Error", e.getMessage());
        }
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
    }
}
