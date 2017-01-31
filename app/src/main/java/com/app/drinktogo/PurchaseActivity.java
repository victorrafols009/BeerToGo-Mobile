package com.app.drinktogo;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.app.drinktogo.helper.Ajax;
import com.app.drinktogo.helper.AppConfig;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

/**
 * Created by Victor Rafols on 1/29/2017.
 */

public class PurchaseActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.payment_login);

        Intent i = getIntent();
        final String user_id = i.getStringExtra("user_id");
        final ArrayList<Integer> items_id = (ArrayList<Integer>) getIntent().getSerializableExtra("items_id");
        final String store_id = i.getStringExtra("store_id");

        final EditText username = (EditText) findViewById(R.id.payment_username);
        final EditText password = (EditText) findViewById(R.id.payment_password);
        Button login = (Button) findViewById(R.id.payment_login);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!username.getText().toString().equals("") && !password.getText().toString().equals("")) {
                    //this is just a dummy login
                    final ProgressDialog progress = new ProgressDialog(PurchaseActivity.this);
                    progress.setMessage("Confirming payment...");
                    progress.setIndeterminate(false);
                    progress.setCancelable(false);

                    for(int i=0; i < items_id.size(); i++) {
                        final int counter = i;
                        RequestParams data = new RequestParams();
                        data.add("user_id", user_id);
                        data.add("item_id", items_id.get(i).toString());
                        data.add("store_id", store_id);

                        Ajax.post("inventory/new", data, new JsonHttpResponseHandler(){
                            @Override
                            public void onStart() {
                                progress.show();
                            }

                            @Override
                            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                                if (statusCode == 200) {
                                    if(response.length() > 0){
                                        if(counter == items_id.size() - 1) {
                                            AppConfig.showDialog(PurchaseActivity.this, "Message", "Successfully bought drink!");
                                            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                            startActivity(intent);
                                        }
                                    }else{
                                        AppConfig.showDialog(PurchaseActivity.this, "Message", "Request not send. Sorry. :(");
                                    }
                                } else {
                                    AppConfig.showDialog(PurchaseActivity.this, "Message", "There is problem in your request. Please try again.");
                                }
                                Log.d("Result", response.toString());
                            }

                            @Override
                            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                                Log.d("Failed: ", ""+statusCode);
                                Log.d("Error : ", "" + throwable);
                                AppConfig.showDialog(PurchaseActivity.this, "Message", "There is problem in your request. Please try again.");
                            }

                            @Override
                            public void onFinish() {
                                progress.dismiss();
                            }
                        });
                    }
                } else {
                    AppConfig.showDialog(PurchaseActivity.this, "Required", "Username and password are required.");
                }
            }
        });
    }
}
