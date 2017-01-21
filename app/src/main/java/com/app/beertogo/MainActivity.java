package com.app.beertogo;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.TextView;

import com.app.beertogo.Entity.User;
import com.app.beertogo.fragments.FriendListFragment;
import com.app.beertogo.fragments.StoreListFragment;
import com.app.beertogo.helper.AppConfig;
import com.app.beertogo.helper.DatabaseHandler;

import org.json.JSONException;
import org.json.JSONObject;

import static android.icu.lang.UCharacter.GraphemeClusterBreak.T;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private DatabaseHandler db;
    private JSONObject user_json;

    private TextView full_name;
    private TextView email;

    public static final String STORE_LIST_FRAGMENT = "STORE_LIST_FRAGMENT";
    public static final String FRIEND_LIST_FRAGMENT = "FRIEND_LIST_FRAGMENT";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View headerLayout = navigationView.getHeaderView(0);

        full_name = (TextView) headerLayout.findViewById(R.id.full_name);
        email = (TextView) headerLayout.findViewById(R.id.email);

        db = new DatabaseHandler(this);
        User user = db.getUser();
        JSONObject data = user.record();
        try {
            user_json = data;
            full_name.setText(data.getString("full_name"));
            email.setText(data.getString("email"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if (getFragmentManager().getBackStackEntryCount() > 0) {
                getFragmentManager().popBackStack();
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
            AppConfig.showDialog(MainActivity.this, "Message", "Logout");
            db.delete();
            Intent activity= new Intent(MainActivity.this, LoginActivity.class);
            startActivity(activity);
            finish();
        }else if(id == R.id.qrcode) {
            Intent activity = new Intent(MainActivity.this, QRCodeGenerator.class);
            startActivity(activity);
        }else if(id == R.id.friends){
            displayFragment(FRIEND_LIST_FRAGMENT);
        }else if(id == R.id.stores){
            displayFragment(STORE_LIST_FRAGMENT);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void displayFragment(String FRAGMENT_TAG){

        FragmentManager fm = getFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        Bundle args = new Bundle();

        switch (FRAGMENT_TAG){
            case STORE_LIST_FRAGMENT:
                StoreListFragment storeFragment = (StoreListFragment) getFragmentManager().findFragmentByTag(FRAGMENT_TAG);
                if (storeFragment != null && storeFragment.isVisible()) {
                    ft.replace(R.id.fragment_container, storeFragment, FRAGMENT_TAG);
                }else{
                    ft.add(R.id.fragment_container, new StoreListFragment(), FRAGMENT_TAG).addToBackStack(FRAGMENT_TAG);
                }
                break;

            case FRIEND_LIST_FRAGMENT:
                FriendListFragment friendFragment = (FriendListFragment) getFragmentManager().findFragmentByTag(FRAGMENT_TAG);
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
}
