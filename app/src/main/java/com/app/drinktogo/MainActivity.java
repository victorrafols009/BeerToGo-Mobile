package com.app.drinktogo;

import android.*;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.app.drinktogo.Entity.Request;
import com.app.drinktogo.Entity.User;
import com.app.drinktogo.fragments.FriendListFragment;
import com.app.drinktogo.fragments.NotificationFragment;
import com.app.drinktogo.fragments.RequestFragment;
import com.app.drinktogo.fragments.StoreListFragment;
import com.app.drinktogo.fragments.ProfileFragment;
import com.app.drinktogo.helper.AppConfig;
import com.app.drinktogo.helper.DatabaseHandler;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private DatabaseHandler db;
    private JSONObject user_json;

    private TextView full_name;
    private TextView email;
    private ImageView logo;

    public static final String PROFILE_FRAGMENT = "PROFILE_FRAGMENT";
    public static final String STORE_LIST_FRAGMENT = "STORE_LIST_FRAGMENT";
    public static final String FRIEND_LIST_FRAGMENT = "FRIEND_LIST_FRAGMENT";
    public static final String FRIEND_FRAGMENT = "FRIEND_FRAGMENT";
    public static final String NOTIFICATION_FRAGMENT = "NOTIFICATION_FRAGMENT";
    public static final String REQUEST_FRAGMENT = "REQUEST_FRAGMENT";

    boolean doubleBackToExitPressedOnce = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Home");

        final DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View headerLayout = navigationView.getHeaderView(0);

        full_name = (TextView) headerLayout.findViewById(R.id.full_name);
        email = (TextView) headerLayout.findViewById(R.id.email);
        logo = (ImageView) headerLayout.findViewById(R.id.logo);
        logo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displayFragment(PROFILE_FRAGMENT);
                getSupportActionBar().setTitle("Your Profile");
                drawer.closeDrawer(GravityCompat.START);
            }
        });

        db = new DatabaseHandler(this);
        User user = db.getUser();
        JSONObject data = user.record();
        try {
            user_json = data;
            full_name.setText(data.getString("full_name"));
            email.setText(data.getString("email"));

            if (data.getInt("store_id") == 0) {
                Menu navMenu = navigationView.getMenu();
                navMenu.findItem(R.id.qrcode).setVisible(false);
            }

            getSupportActionBar().setTitle("Your Profile");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        displayFragment(PROFILE_FRAGMENT);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            int fragmentCount = getSupportFragmentManager().getBackStackEntryCount();
            if(fragmentCount == 1) {
                if (doubleBackToExitPressedOnce) {
                    finish();
                }

                this.doubleBackToExitPressedOnce = true;
                Toast.makeText(this, "Please tap BACK again to exit.", Toast.LENGTH_LONG).show();

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        doubleBackToExitPressedOnce = false;
                    }
                }, 3500);
            } else if(fragmentCount > 1) {
                getSupportFragmentManager().popBackStack();
            } else {
                super.onBackPressed();
            }
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if(id == R.id.logout){
            DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    switch (which){
                        case DialogInterface.BUTTON_POSITIVE:
                            db.delete();
                            Intent activity= new Intent(MainActivity.this, LoginActivity.class);
                            startActivity(activity);
                            finish();
                            break;

                        case DialogInterface.BUTTON_NEGATIVE:
                            dialog.dismiss();
                            break;
                    }
                }
            };

            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setTitle("Confirmation")
                    .setMessage("Are you sure you want to log out?")
                    .setPositiveButton("Yes", dialogClickListener)
                    .setNegativeButton("No", dialogClickListener)
                    .show();
        } else if(id == R.id.qrcode) {
            verifyCameraPermission(MainActivity.this);
        } else if(id == R.id.friends){
            displayFragment(FRIEND_LIST_FRAGMENT);
            getSupportActionBar().setTitle("Friend List");
        } else if(id == R.id.stores){
            displayFragment(STORE_LIST_FRAGMENT);
            getSupportActionBar().setTitle("Store List");
        } else if(id == R.id.notifications) {
            displayFragment(NOTIFICATION_FRAGMENT);
            getSupportActionBar().setTitle("Notifications");
        } else if(id == R.id.requests) {
            displayFragment(REQUEST_FRAGMENT);
            getSupportActionBar().setTitle("Requests");
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void displayFragment(String FRAGMENT_TAG){

        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        Bundle args = new Bundle();

        switch (FRAGMENT_TAG){

            case REQUEST_FRAGMENT:
                RequestFragment requestFragment = (RequestFragment) getSupportFragmentManager().findFragmentByTag(FRAGMENT_TAG);
                try {
                    args.putInt("user_id", user_json.getInt("id"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (requestFragment != null && requestFragment.isVisible()) {
                    ft.replace(R.id.fragment_container, requestFragment, FRAGMENT_TAG);
                }else{
                    RequestFragment frag = new RequestFragment();
                    frag.setArguments(args);
                    ft.add(R.id.fragment_container, frag, FRAGMENT_TAG).addToBackStack(FRAGMENT_TAG);
                }
                break;

            case NOTIFICATION_FRAGMENT:
                NotificationFragment notificationFragment = (NotificationFragment) getSupportFragmentManager().findFragmentByTag(FRAGMENT_TAG);
                try {
                    args.putInt("user_id", user_json.getInt("id"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (notificationFragment != null && notificationFragment.isVisible()) {
                    ft.replace(R.id.fragment_container, notificationFragment, FRAGMENT_TAG);
                }else{
                    NotificationFragment frag = new NotificationFragment();
                    frag.setArguments(args);
                    ft.add(R.id.fragment_container, frag, FRAGMENT_TAG).addToBackStack(FRAGMENT_TAG);
                }
                break;

            case PROFILE_FRAGMENT:
                ProfileFragment profileFragment = (ProfileFragment) getSupportFragmentManager().findFragmentByTag(FRAGMENT_TAG);
                try {
                    args.putInt("user_id", user_json.getInt("id"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (profileFragment != null && profileFragment.isVisible()) {
                    ft.replace(R.id.fragment_container, profileFragment, FRAGMENT_TAG);
                }else{
                    ProfileFragment frag = new ProfileFragment();
                    frag.setArguments(args);
                    ft.add(R.id.fragment_container, frag, FRAGMENT_TAG).addToBackStack(FRAGMENT_TAG);
                }
                break;

            case STORE_LIST_FRAGMENT:
                StoreListFragment storeFragment = (StoreListFragment) getSupportFragmentManager().findFragmentByTag(FRAGMENT_TAG);
                if (storeFragment != null && storeFragment.isVisible()) {
                    ft.replace(R.id.fragment_container, storeFragment, FRAGMENT_TAG);
                }else{
                    ft.add(R.id.fragment_container, new StoreListFragment(), FRAGMENT_TAG).addToBackStack(FRAGMENT_TAG);
                }
                break;

            case FRIEND_LIST_FRAGMENT:
                FriendListFragment friendFragment = (FriendListFragment) getSupportFragmentManager().findFragmentByTag(FRAGMENT_TAG);
                try {
                    args.putInt("user_id", user_json.getInt("id"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (friendFragment != null && friendFragment.isVisible()) {
                    ft.replace(R.id.fragment_container, friendFragment, FRAGMENT_TAG);
                }else{
                    FriendListFragment frag = new FriendListFragment();
                    frag.setArguments(args);
                    ft.add(R.id.fragment_container, frag, FRAGMENT_TAG).addToBackStack(FRAGMENT_TAG);
                }
                break;
        }
        ft.commit();
    }

    private static final int REQUEST_CAMERA = 1;
    private static String[] PERMISSION_CAMERA = {
            android.Manifest.permission.CAMERA
    };

    public void verifyCameraPermission(Activity activity) {
        int permission = ActivityCompat.checkSelfPermission(activity, android.Manifest.permission.CAMERA);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSION_CAMERA,
                    REQUEST_CAMERA
            );
        } else {
            Intent i = new Intent(MainActivity.this, QRCodeActivity.class);
            startActivity(i);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Intent activity = new Intent(MainActivity.this, QRCodeActivity.class);
                    startActivity(activity);
                } else {
                    Toast.makeText(MainActivity.this, "Cannot open QR Code Scanning if you denied us on accessing your camera. :(", Toast.LENGTH_LONG).show();
                }
                return;
            }
        }
    }
}
