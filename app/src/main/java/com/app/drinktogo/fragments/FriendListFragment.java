package com.app.drinktogo.fragments;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.ListFragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.app.drinktogo.Adapter.FriendAdapter;
import com.app.drinktogo.Entity.Friend;
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

public class FriendListFragment extends ListFragment {

    FriendAdapter friendAdapter;
    private int user_id;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View  view = inflater.inflate(R.layout.fragment_list, container, false);
        user_id = getArguments().getInt("user_id");
        return view;
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        FriendAdapter.ViewHolder view = (FriendAdapter.ViewHolder) v.getTag();
        Friend f = view.friend;

        FragmentManager fm = getActivity().getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        FriendFragment myFragment = (FriendFragment)getFragmentManager().findFragmentByTag("FRIEND_FRAGMENT");
        Bundle args = new Bundle();
        args.putInt("user_id", f.friend_id);
        if (myFragment != null && myFragment.isVisible()) {
            myFragment.setArguments(args);
            ft.replace(R.id.fragment_container, myFragment, "FRIEND_FRAGMENT");
        }else{
            FriendFragment frag = new FriendFragment();
            frag.setArguments(args);
            ft.add(R.id.fragment_container, frag, "FRIEND_FRAGMENT").addToBackStack("FRIEND_FRAGMENT");
        }
        ft.commit();

    }



    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);


        friendAdapter = new FriendAdapter(getActivity());

        Ajax.get("user/" + user_id + "/friends", null, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                if(response.length() > 0){
                    for(int x=0;x < response.length(); x++){
                        Friend f = new Friend();
                        try {
                            JSONObject o = response.getJSONObject(x);
                            f.id = o.getInt("f_id");
                            f.full_name = o.getString("friend_name");
                            f.email = o.getString("friend_email");
                            f.date_created = o.getString("friend_created_date");
                            f.friend_id = o.getInt("friend_id");
                            friendAdapter.addItem(f);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
                setListAdapter(friendAdapter);
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
