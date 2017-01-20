package com.app.beertogo.fragments;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
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

import com.app.beertogo.Adapter.StoreAdapter;
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

public class StoreListFragment extends ListFragment {
    StoreAdapter storeAdapter;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View  view = inflater.inflate(R.layout.fragment_list, container, false);
        return view;
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        StoreAdapter.ViewHolder view = (StoreAdapter.ViewHolder) v.getTag();
        FragmentManager fm = getActivity().getFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ItemListFragment myFragment = (ItemListFragment)getFragmentManager().findFragmentByTag("STORE_ITEM_LIST_FRAGMENT");
        Bundle args = new Bundle();
        args.putInt("store_id", view.store.id);
        if (myFragment != null && myFragment.isVisible()) {
            myFragment.setArguments(args);
            ft.replace(R.id.fragment_container, myFragment, "STORE_ITEM_LIST_FRAGMENT");
        }else{
            ItemListFragment frag = new ItemListFragment();
            frag.setArguments(args);
            ft.add(R.id.fragment_container, frag, "STORE_ITEM_LIST_FRAGMENT").addToBackStack("STORE_ITEM_LIST_FRAGMENT");
        }
        ft.commit();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        storeAdapter = new StoreAdapter(getActivity());

        Ajax.get("store", null, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                if(response.length() > 0){
                    for(int i=0;i < response.length(); i++){
                        Store s = new Store();
                        try {
                            JSONObject o = response.getJSONObject(i);
                            s.id = o.getInt("id");
                            s.name = o.getString("name");
                            s.address = o.getString("address");
                            s.contact = o.getString("contact");
                            s.logo = o.getString("logo");
                            s.user_id = o.getInt("user_id");
                            storeAdapter.addItem(s);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
                setListAdapter(storeAdapter);
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
