package com.app.beertogo.fragments;

import android.app.ListFragment;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.app.beertogo.Adapter.ItemAdapter;
import com.app.beertogo.Adapter.StoreAdapter;
import com.app.beertogo.Entity.Item;
import com.app.beertogo.Entity.Store;
import com.app.beertogo.Entity.User;
import com.app.beertogo.LoginActivity;
import com.app.beertogo.R;
import com.app.beertogo.helper.Ajax;
import com.app.beertogo.helper.AppConfig;
import com.app.beertogo.helper.DatabaseHandler;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;

/**
 * Created by Ken on 11/01/2017.
 */

public class ItemListFragment extends ListFragment {

    ItemAdapter itemAdapter;
    private int store_id;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View  view = inflater.inflate(R.layout.fragment_list, container, false);
        store_id = getArguments().getInt("store_id");
        return view;
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        ItemAdapter.ViewHolder view = (ItemAdapter.ViewHolder) v.getTag();
        Item i = view.item;
        Log.d("Item", i.id + " - " + i.name);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        itemAdapter = new ItemAdapter(getActivity());

        Ajax.get("item/" + store_id, null, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                if(response.length() > 0){
                    for(int x=0;x < response.length(); x++){
                        Item i = new Item();
                        try {
                            JSONObject o = response.getJSONObject(x);
                            i.id = o.getInt("id");
                            i.name = o.getString("name");
                            i.brand = o.getString("brand");
                            i.qty = o.getInt("qty");
                            i.store_id = o.getInt("store_id");
                            itemAdapter.addItem(i);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
                setListAdapter(itemAdapter);
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
