package edu.uw.tcss450.varpar.weatherapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.json.JSONException;
import org.json.JSONObject;

import edu.uw.tcss450.varpar.weatherapp.model.UserInfoViewModel;

/**
 * Activity that holds all the main content for the app.
 * @author James Deal
 */
public class MainActivity extends AppCompatActivity {

    /** Config for bottom navigation.  */
    private AppBarConfiguration mAppBarConfiguration;

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

        setContentView(R.layout.activity_main);

        //this stuff works with binding existing fragment to nav
        BottomNavigationView navView = findViewById(R.id.nav_view);

        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_weather,
                R.id.navigation_contacts, R.id.navigation_chat)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);

        // This is the code for the add chat button
        // TODO: add chat button to chat fragment functionality
//        Button addChatButton = findViewById(R.id.);
//        addChatButton.setOnClickListener(button -> {
//            Navigation.findNavController(this, R.id.nav_host_fragment).navigate(
//                    MainActivityDirections.actionNavigationChatToAddChatFragment());
//        });
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
        }
        return NavigationUI.onNavDestinationSelected(item, navController)
                || super.onOptionsItemSelected(item);
    }

    /**
     * Sign-out functionality for users.
     * Commented out code may be used later to store user being able to sign in again automatically.
     */
    private void signOut() {
//        SharedPreferences prefs =
//                getSharedPreferences(
//                        getString(R.string.keys_shared_prefs),
//                        Context.MODE_PRIVATE);
//        prefs.edit().remove(getString(R.string.keys_prefs_jwt)).apply();
        //End the app completely
        finishAndRemoveTask();
    }

}
