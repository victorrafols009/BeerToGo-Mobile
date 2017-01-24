package com.app.drinktogo.fragments;

import android.app.Fragment;
import android.app.ListFragment;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.app.drinktogo.Adapter.NotificationAdapter;
import com.app.drinktogo.Entity.Notification;
import com.app.drinktogo.R;
import com.app.drinktogo.helper.Ajax;
import com.app.drinktogo.helper.AppConfig;
import com.loopj.android.http.JsonHttpResponseHandler;

import cz.msebera.android.httpclient.Header;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Victor Rafols on 1/24/2017.
 */

public class NotificationFragment extends ListFragment {

    private int user_id;

    NotificationAdapter notificatonAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View  view = inflater.inflate(R.layout.fragment_list, container, false);

        user_id = getArguments().getInt("user_id");

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        notificatonAdapter = new NotificationAdapter(getActivity());

        final ProgressDialog progress = new ProgressDialog(getActivity());
        progress.setMessage("Logging in...");
        progress.setIndeterminate(false);
        progress.setCancelable(true);
        Ajax.get("user/" + user_id + "/transactions", null, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                if(response.length() > 0){
                    for(int i=0;i < response.length(); i++){
                        Notification notification = new Notification();
                        try {
                            JSONObject o = response.getJSONObject(i);
                            if(o.getInt("user_confirm_flag") == 0) {
                                notification.id = o.getInt("id");
                                notification.request_description = o.getString("friend_name") + " would like to drink your " + o.getString("item_name") + " (" + o.getString("brand") + ") at " + o.getString("store_name") + " (" + o.getString("store_address") + ").";
                                notification.request_date = o.getString("date_created");
                                notificatonAdapter.addItem(notification);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
                setListAdapter(notificatonAdapter);
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
