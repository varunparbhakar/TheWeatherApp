package edu.uw.tcss450.varpar.weatherapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;

import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.json.JSONException;
import org.json.JSONObject;

import edu.uw.tcss450.varpar.weatherapp.auth.login.LoginFragmentDirections;
import edu.uw.tcss450.varpar.weatherapp.chat.ChatRoomMessage;
import edu.uw.tcss450.varpar.weatherapp.chat.ChatRoomViewModel;
import edu.uw.tcss450.varpar.weatherapp.databinding.ActivityMainBinding;
import edu.uw.tcss450.varpar.weatherapp.model.NewMessageCountViewModel;
import edu.uw.tcss450.varpar.weatherapp.model.PushyTokenViewModel;
import edu.uw.tcss450.varpar.weatherapp.model.UserInfoViewModel;
import edu.uw.tcss450.varpar.weatherapp.services.PushReceiver;

/**
 * Activity that holds all the main content for the app.
 * @author James Deal
 */
public class MainActivity extends AppCompatActivity {

    /** Config for bottom navigation.  */
    private AppBarConfiguration mAppBarConfiguration;

    private ActivityMainBinding binding;

    private MainPushMessageReceiver mPushMessageReceiver;

    private NewMessageCountViewModel mNewMessageModel;

    /**
     * Creates activity and performs setup of bottom navigation.
     * @param savedInstanceState If the activity is being re-initialized after
     *     previously being shut down then this Bundle contains the data it most
     *     recently supplied in {@link #onSaveInstanceState}.  <b><i>Note: Otherwise it is null.</i></b>
     */
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Importing Arguments
        MainActivityArgs args = MainActivityArgs.fromBundle(getIntent().getExtras());
        UserInfoViewModel model = new ViewModelProvider(this).get(UserInfoViewModel.class);
        try {
            model.setJSON(new JSONObject(args.getJson()));
        } catch (JSONException e) {
            Log.i("CONVERTING ERROR", "onCreate: while converting from string back into the json object there was an error ");
            throw new RuntimeException(e);
        }

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_weather,
                R.id.navigation_contacts, R.id.navigation_chat)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);

        mNewMessageModel = new ViewModelProvider(this).get(NewMessageCountViewModel.class);

        navController.addOnDestinationChangedListener((controller, destination, arguments) -> {
            if (destination.getId() == R.id.navigation_chat) {
                //When the user navigates to the chats page, reset the new message count.
                //This will need some extra logic for your project as it should have
                //multiple chat rooms.
                mNewMessageModel.reset();
            }
        });

        mNewMessageModel.addMessageCountObserver(this, count -> {
            BadgeDrawable badge = binding.navView.getOrCreateBadge(R.id.navigation_chat);
            badge.setMaxCharacterCount(2);
            if (count > 0) {
                //new messages! update and show the notification badge.
                badge.setNumber(count);
                badge.setVisible(true);
            } else {
                //user did some action to clear the new messages, remove the badge
                badge.clearNumber();
                badge.setVisible(false);
            }
        });

        // This is the code for the add chat button
        // TODO: add chat button to chat fragment functionality
//        Button addChatButton = findViewById(R.id.);
//        addChatButton.setOnClickListener(button -> {
//            Navigation.findNavController(this, R.id.nav_host_fragment).navigate(
//                    MainActivityDirections.actionNavigationChatToAddChatFragment());
//        });
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mPushMessageReceiver == null) {
            mPushMessageReceiver = new MainPushMessageReceiver();
        }
        IntentFilter iFilter = new IntentFilter(PushReceiver.RECEIVED_NEW_MESSAGE);
        registerReceiver(mPushMessageReceiver, iFilter);
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mPushMessageReceiver != null){
            unregisterReceiver(mPushMessageReceiver);
        }
    }

    /**
     * Allows for back-navigation when digging into fragments.
     * @return if navigation would be successful.
     */
    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration) || super.onSupportNavigateUp();
    }

    /**
     * Inflates options menu present in project.
     * @param menu The options menu in which you place your items.
     *
     * @return if create successful.
     */
    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar, menu);
        return true;
    }

    /**
     * Supports navigation for options menu options.
     * @param item The menu item that was selected.
     *
     * @return if nav successful.
     */
    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        if (item.getItemId() == R.id.action_sign_out) {
            signOut();
//            return true;
        }
        return NavigationUI.onNavDestinationSelected(item, navController)
                || super.onOptionsItemSelected(item);
    }

    /**
     * Sign-out functionality for users.
     * Commented out code may be used later to store user being able to sign in again automatically.
     */
    private void signOut() {
        SharedPreferences prefs =
                getSharedPreferences(
                        getString(R.string.keys_shared_prefs),
                        Context.MODE_PRIVATE);
        prefs.edit().remove(getString(R.string.keys_prefs_jwt)).apply();
//        End the app completely
        PushyTokenViewModel model = new ViewModelProvider(this)
                .get(PushyTokenViewModel.class);
//when we hear back from the web service quit
        model.addResponseObserver(this, result -> finishAndRemoveTask());
        model.deleteTokenFromWebservice(
                new ViewModelProvider(this)
                        .get(UserInfoViewModel.class)
                        .getJwt()
        );
        finishAndRemoveTask();
        Intent i = new Intent(getApplicationContext(),AuthActivity.class);
        startActivity(i);
        setContentView(R.layout.activity_auth);
    }

    /**
     * A BroadcastReceiver that listens for messages sent from PushReceiver
     */
    private class MainPushMessageReceiver extends BroadcastReceiver {

        private ChatRoomViewModel mModel =
                new ViewModelProvider(MainActivity.this)
                        .get(ChatRoomViewModel.class);

        @Override
        public void onReceive(Context context, Intent intent) {
            NavController nc =
                    Navigation.findNavController(
                            MainActivity.this, R.id.nav_host_fragment);
            NavDestination nd = nc.getCurrentDestination();

            if (intent.hasExtra("chatMessage")) {

                ChatRoomMessage cm = (ChatRoomMessage) intent.getSerializableExtra("chatMessage");

                //If the user is not on the chat screen, update the
                //NewMessageCountView Model
                if (nd.getId() != R.id.navigation_chat) {
                    mNewMessageModel.increment();
                }
                //Inform the view model holding chatroom messages of the new
                //message.
                mModel.addMessage(intent.getIntExtra("chatid", -1), cm);
            }
        }
    }
}
