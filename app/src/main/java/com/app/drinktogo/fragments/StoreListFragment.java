package com.app.drinktogo.fragments;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.ListFragment;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;

import com.app.drinktogo.Adapter.StoreAdapter;
import com.app.drinktogo.Entity.Store;
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
 * Created by Ken on 11/01/2017.
 */

public class StoreListFragment extends ListFragment {
    StoreAdapter storeAdapter;
    private int user_id;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View  view = inflater.inflate(R.layout.fragment_list, container, false);
        user_id = getArguments().getInt("user_id");
        return view;
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        StoreAdapter.ViewHolder view = (StoreAdapter.ViewHolder) v.getTag();
        FragmentManager fm = getActivity().getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ItemListFragment myFragment = (ItemListFragment)getFragmentManager().findFragmentByTag("STORE_ITEM_LIST_FRAGMENT");
        Bundle args = new Bundle();
        args.putInt("store_id", view.store.id);
        args.putInt("user_id", user_id);
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

        ImageView imgView = new ImageView(getActivity());
        imgView.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.ic_store));
        imgView.setLayoutParams(new ListView.LayoutParams(ListView.LayoutParams.MATCH_PARENT, 600));
        imgView.setScaleType(ImageView.ScaleType.FIT_XY);
        this.getListView().addHeaderView(imgView);

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
