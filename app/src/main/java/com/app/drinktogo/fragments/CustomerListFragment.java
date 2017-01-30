package com.app.drinktogo.fragments;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.app.drinktogo.Adapter.CustomerAdapter;
import com.app.drinktogo.Entity.Customer;
import com.app.drinktogo.Entity.User;
import com.app.drinktogo.R;
import com.app.drinktogo.helper.Ajax;
import com.app.drinktogo.helper.AppConfig;
import com.app.drinktogo.helper.DatabaseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

/**
 * Created by Victor Rafols on 1/31/2017.
 */

public class CustomerListFragment extends ListFragment {
    CustomerAdapter customerAdapter;
    private int user_id;
    private int store_id;

    private DatabaseHandler db;
    private JSONObject user_json;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View  view = inflater.inflate(R.layout.fragment_list, container, false);
        user_id = getArguments().getInt("user_id");

        db = new DatabaseHandler(getActivity());
        User user = db.getUser();
        user_json = user.record();

        try {
            store_id = user_json.getInt("store_id");
        } catch(JSONException e) {
            e.printStackTrace();
        }

        return view;
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        CustomerAdapter.ViewHolder view = (CustomerAdapter.ViewHolder) v.getTag();
        Customer c = view.customer;

        FragmentManager fm = getActivity().getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        CustomerFragment myFragment = (CustomerFragment) getFragmentManager().findFragmentByTag("CUSTOMER_FRAGMENT");
        Bundle args = new Bundle();
        args.putInt("user_id", c.customer_id);
        if (myFragment != null && myFragment.isVisible()) {
            myFragment.setArguments(args);
            ft.replace(R.id.fragment_container, myFragment, "CUSTOMER_FRAGMENT");
        }else{
            CustomerFragment frag = new CustomerFragment();
            frag.setArguments(args);
            ft.add(R.id.fragment_container, frag, "CUSTOMER_FRAGMENT").addToBackStack("CUSTOMER_FRAGMENT");
        }
        ft.commit();

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        customerAdapter = new CustomerAdapter(getActivity());

        Ajax.get("store/" + store_id + "/customer", null, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                if(response.length() > 0){
                    for(int x=0;x < response.length(); x++){
                        Customer c = new Customer();
                        try {
                            JSONObject o = response.getJSONObject(x);
                            c.id = o.getInt("id");
                            c.full_name = o.getString("full_name");
                            c.email = o.getString("email");
                            c.date_created = o.getString("date_stored");
                            c.customer_id = o.getInt("user_id");
                            customerAdapter.addItem(c);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
                setListAdapter(customerAdapter);
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
