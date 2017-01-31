package com.app.drinktogo.fragments;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.app.drinktogo.Adapter.RequestAdapter;
import com.app.drinktogo.Entity.Request;
import com.app.drinktogo.QRCodeGenerator;
import com.app.drinktogo.R;
import com.app.drinktogo.helper.Ajax;
import com.app.drinktogo.helper.AppConfig;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

/**
 * Created by Victor Rafols on 1/29/2017.
 */

public class RequestFragment extends ListFragment {

    private int user_id;

    RequestAdapter requestAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View  view = inflater.inflate(R.layout.fragment_list, container, false);

        user_id = getArguments().getInt("user_id");

        return view;
    }

    public void onListItemClick(ListView l, View v, int position, long id) {
        RequestAdapter.ViewHolder view = (RequestAdapter.ViewHolder) v.getTag();
        final Request r = view.request;

        if(r.request_flag == 1) {
            JSONObject o = new JSONObject();
            try {
                o.put("id", Integer.toString(r.id));
                o.put("user_id", Integer.toString(r.user_id));
                o.put("friend_id", Integer.toString(r.friend_id));
                o.put("store_id", Integer.toString(r.store_id));
                o.put("inventory_id", Integer.toString(r.inventory_id));
            } catch (JSONException e) {
                e.printStackTrace();
            }

            RequestParams data = new RequestParams();
            data.add("json_value", o.toString());

            Ajax.post("encrypt", data, new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    try {
                        Intent i = new Intent(getActivity(), QRCodeGenerator.class);
                        i.putExtra("qr_code", response.getString("encrypted_json"));
                        startActivity(i);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                    Log.d("Failed: ", "" + statusCode);
                    Log.d("Error : ", "" + throwable);
                    AppConfig.showDialog(getActivity(), "Message", "There is problem in your request. Please try again.");
                }

            });
        } else if(r.request_flag == -1) {
            DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(final DialogInterface dialog, int which) {
                    final ProgressDialog progress = new ProgressDialog(getActivity());
                    progress.setMessage("Approving this request...");
                    progress.setIndeterminate(false);
                    progress.setCancelable(false);

                    switch (which) {
                        case DialogInterface.BUTTON_NEUTRAL:
                            RequestParams data = new RequestParams();
                            data.add("id", Integer.toString(r.id));

                            Ajax.post("transaction/seen/user", data, new JsonHttpResponseHandler() {
                                @Override
                                public void onStart() {
                                    progress.show();
                                }

                                @Override
                                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                                    if (statusCode == 200) {
                                        if (response.length() > 0) {
                                            dialog.dismiss();

                                            FragmentManager fm = getActivity().getSupportFragmentManager();
                                            FragmentTransaction ft = fm.beginTransaction();
                                            Bundle args = new Bundle();
                                            args.putInt("user_id", user_id);
                                            RequestFragment requestFragment = new RequestFragment();
                                            requestFragment.setArguments(args);
                                            ft.replace(R.id.fragment_container, requestFragment, "REQUEST_FRAGMENT");
                                            ft.commit();
                                        } else {
                                            AppConfig.showDialog(getActivity(), "Message", "Approval not send. Sorry. :(");
                                        }
                                    } else {
                                        AppConfig.showDialog(getActivity(), "Message", "There is problem in your request. Please try again.");
                                    }
                                    Log.d("Result", response.toString());
                                }

                                @Override
                                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                                    Log.d("Failed: ", "" + statusCode);
                                    Log.d("Error : ", "" + throwable);
                                    AppConfig.showDialog(getActivity(), "Message", "There is problem in your request. Please try again.");
                                }

                                @Override
                                public void onFinish() {
                                    progress.dismiss();
                                }
                            });
                            break;
                    }
                }
            };

            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle("Information")
                    .setMessage("The user decline your request to drink this item.")
                    .setNeutralButton("Okay", dialogClickListener)
                    .setCancelable(false)
                    .show();
        } else {
            AppConfig.showDialog(getActivity(), "Pending", "Please wait for your request to be approved.");
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        requestAdapter = new RequestAdapter(getActivity());

        final ProgressDialog progress = new ProgressDialog(getActivity());
        progress.setMessage("Gathering data...");
        progress.setIndeterminate(false);
        progress.setCancelable(true);
        Ajax.get("user/" + user_id + "/requests", null, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                if(response.length() > 0){
                    for(int i=0;i < response.length(); i++){
                        Request request = new Request();
                        try {
                            JSONObject o = response.getJSONObject(i);
                            if(o.getInt("store_confirm_flag") == 0 && o.getInt("seen_flag") == 0) {
                                request.id = o.getInt("id");
                                request.request_flag = o.getInt("user_confirm_flag");
                                request.user_id = o.getInt("user_id");
                                request.friend_id = o.getInt("friend_id");
                                request.store_id = o.getInt("store_id");
                                request.inventory_id = o.getInt("inventory_id");
                                if(o.getInt("user_confirm_flag") == 1) {
                                    request.request_description = o.getString("user_name") + " approved your request to drink " + o.getString("item_name") + " (" + o.getString("brand") + ").\nClick this to show the QR Code and present it to " + o.getString("store_name") + " (" + o.getString("store_address") + ").";
                                } else if(o.getInt("user_confirm_flag") == -1) {
                                    request.request_description = o.getString("user_name") + " declined your request to drink " + o.getString("item_name") + " (" + o.getString("brand") + ").\nSorry. :(";
                                } else {
                                    request.request_description = "Request sent to " + o.getString("user_name") + " to drink " + o.getString("item_name") + " (" + o.getString("brand") + ") at " + o.getString("store_name") + " (" + o.getString("store_address") + ").";
                                }
                                request.request_date = o.getString("date_created");
                                requestAdapter.addItem(request);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
                setListAdapter(requestAdapter);
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
