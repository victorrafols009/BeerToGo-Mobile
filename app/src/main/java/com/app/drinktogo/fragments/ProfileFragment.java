package com.app.drinktogo.fragments;

import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.app.drinktogo.Adapter.InventoryAdapter;
import com.app.drinktogo.Entity.Inventory;
import com.app.drinktogo.MainActivity;
import com.app.drinktogo.R;
import com.app.drinktogo.helper.Ajax;
import com.app.drinktogo.helper.AppConfig;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

/**
 * Created by Ken on 17/01/2017.
 */

public class ProfileFragment extends ListFragment {

    private int user_id;

    private View headerView;
    private ImageView user_logo;
    private TextView full_name;
    private TextView email;
    private TextView inventory_count;
    private TextView friend_count;
    private TextView trans_count;
    private TextView date_created;

    InventoryAdapter inventoryAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View  view = inflater.inflate(R.layout.fragment_list, container, false);

        headerView = inflater.inflate(R.layout.user_profile, null, false);

        user_id = getArguments().getInt("user_id");

        user_logo = (ImageView) headerView.findViewById(R.id.user_logo);
        full_name = (TextView) headerView.findViewById(R.id.user_full_name);
        email = (TextView) headerView.findViewById(R.id.user_email);
        inventory_count = (TextView) headerView.findViewById(R.id.inventory_count);
        friend_count = (TextView) headerView.findViewById(R.id.friends_count);
        trans_count = (TextView) headerView.findViewById(R.id.transaction_count);
        date_created = (TextView) headerView.findViewById(R.id.date_created);

        return view;
    }

    public void onListItemClick(ListView l, View v, int position, long id) {

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        final ProgressDialog progress = new ProgressDialog(getActivity());
        progress.setMessage("Getting your data...");
        progress.setIndeterminate(false);
        progress.setCancelable(true);
        Ajax.get("user/" + user_id, null, new JsonHttpResponseHandler() {
            @Override
            public void onStart() {
                progress.show();
            }

            @Override
            public void onFinish() {
                progress.dismiss();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                if(response.length() > 0){
                    for(int x=0;x < response.length(); x++){
                        try {
                            JSONObject o = response.getJSONObject(x);
                            full_name.setText(o.getString("full_name"));
                            date_created.setText("Since : " + o.getString("date_created_format"));
                            inventory_count.setText(o.getString("inventory_count"));
                            trans_count.setText("0"); //set value if messages is okay already
                            friend_count.setText(o.getString("friend_count"));
                            email.setText(o.getString("email"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.d("Failed: ", ""+statusCode);
                Log.d("Error : ", "" + throwable);
                AppConfig.showDialog(getActivity(), "Message", "There is problem in your request. Please try again.");
            }
        });

        this.getListView().addHeaderView(headerView);

        inventoryAdapter = new InventoryAdapter(getActivity());

        Ajax.get("user/" + user_id + "/inventory", null, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                if(response.length() > 0){
                    for(int i=0;i < response.length(); i++){
                        Inventory inventory = new Inventory();
                        try {
                            JSONObject o = response.getJSONObject(i);
                            inventory.id = o.getInt("id");
                            inventory.user_id = o.getInt("user_id");
                            inventory.item_id = o.getInt("item_id");
                            inventory.store_id = o.getInt("store_id");
                            inventory.is_empty = o.getInt("is_empty");
                            inventory.date_stored = o.getString("date_stored");
                            inventory.name = o.getString("item_name");
                            inventory.brand = o.getString("item_brand");
                            inventory.store_name = o.getString("store_name");
                            inventory.store_address = o.getString("store_address");
                            inventoryAdapter.addItem(inventory);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
                setListAdapter(inventoryAdapter);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.d("Failed: ", ""+statusCode);
                Log.d("Error : ", "" + throwable);
                AppConfig.showDialog(getActivity(), "Message", "There is problem in your request. Please try again.");
            }

        });
    }
}
