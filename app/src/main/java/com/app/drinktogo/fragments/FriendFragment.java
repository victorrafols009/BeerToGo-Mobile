package com.app.drinktogo.fragments;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.support.v4.app.ListFragment;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.app.drinktogo.Adapter.InventoryAdapter;
import com.app.drinktogo.Adapter.StoreAdapter;
import com.app.drinktogo.Entity.Inventory;
import com.app.drinktogo.Entity.User;
import com.app.drinktogo.MainActivity;
import com.app.drinktogo.R;
import com.app.drinktogo.helper.Ajax;
import com.app.drinktogo.helper.AppConfig;
import com.app.drinktogo.helper.DatabaseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

/**
 * Created by Victor Rafols on 1/24/2017.
 */

public class FriendFragment extends ListFragment {
    private int user_id;

    private View headerView;
    private ImageView user_logo;
    private TextView full_name;
    private TextView email;
    private TextView inventory_count;
    private TextView friend_count;
    private TextView trans_count;
    private TextView date_created;

    private DatabaseHandler db;
    private int curr_user_id;

    InventoryAdapter inventoryAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_list, container, false);

        headerView = inflater.inflate(R.layout.friend_profile, null, false);

        db = new DatabaseHandler(getActivity());
        User user = db.getUser();
        JSONObject data = user.record();
        try {
            curr_user_id = data.getInt("id");
        } catch (JSONException e) {
            e.printStackTrace();
        }

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
        InventoryAdapter.ViewHolder view = (InventoryAdapter.ViewHolder) v.getTag();
        final Inventory i = view.inventory;

        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case DialogInterface.BUTTON_POSITIVE:
                        final ProgressDialog progress = new ProgressDialog(getActivity());
                        progress.setMessage("Logging in...");
                        progress.setIndeterminate(false);
                        progress.setCancelable(false);

                        RequestParams data = new RequestParams();
                        data.add("user_id", Integer.toString(user_id));
                        data.add("friend_id", Integer.toString(curr_user_id));
                        data.add("inventory_id", Integer.toString(i.item_id));
                        data.add("store_id", Integer.toString(i.store_id));

                        Ajax.post("transaction/new", data, new JsonHttpResponseHandler(){
                            @Override
                            public void onStart() {
                                progress.show();
                            }

                            @Override
                            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                                if (statusCode == 200) {
                                    if(response.length() > 0){
                                        AppConfig.showDialog(getActivity(), "Message", "Successfully requested to drink!");
                                    }else{
                                        AppConfig.showDialog(getActivity(), "Message", "Request not send. Sorry. :(");
                                    }
                                } else {
                                    AppConfig.showDialog(getActivity(), "Message", "There is problem in your request. Please try again.");
                                }
                                Log.d("Result", response.toString());
                            }

                            @Override
                            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                                Log.d("Failed: ", ""+statusCode);
                                Log.d("Error : ", "" + throwable);
                                AppConfig.showDialog(getActivity(), "Message", "There is problem in your request. Please try again.");
                            }

                            @Override
                            public void onFinish() {
                                progress.dismiss();
                            }
                        });
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        dialog.dismiss();
                        break;
                }
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Request Confirmation")
                .setMessage("Send request to drink this " + i.name + "?")
                .setPositiveButton("Yes", dialogClickListener)
                .setNegativeButton("No", dialogClickListener)
                .show();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        final ProgressDialog progress = new ProgressDialog(getActivity());
        progress.setMessage("Logging in...");
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
                            trans_count.setText(o.getString("transaction_count"));
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
