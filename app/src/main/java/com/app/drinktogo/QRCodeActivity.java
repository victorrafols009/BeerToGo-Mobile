package com.app.drinktogo;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.Toast;
import android.Manifest;

import com.app.drinktogo.Entity.User;
import com.app.drinktogo.helper.Ajax;
import com.app.drinktogo.helper.AppConfig;
import com.app.drinktogo.helper.DatabaseHandler;
import com.google.zxing.Result;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONStringer;

import cz.msebera.android.httpclient.Header;
import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class QRCodeActivity extends AppCompatActivity implements ZXingScannerView.ResultHandler {

    private ZXingScannerView mScannerView;
    private DatabaseHandler db;
    private JSONObject user_json;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.qr_code);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Scan QR");

        db = new DatabaseHandler(this);
        User user = db.getUser();
        user_json = user.record();

        mScannerView = new ZXingScannerView(this);
        setContentView(mScannerView);
        mScannerView.setResultHandler(this);
        mScannerView.startCamera();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(mScannerView != null) mScannerView.stopCamera();
    }

    @Override
    public void handleResult(Result result) {
        RequestParams data = new RequestParams();
        data.add("encrypted_json", result.getText());

        Ajax.post("decrypt", data, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    JSONObject res = new JSONObject(response.getString("json_value"));

                    if(user_json.getInt("store_id") == Integer.parseInt(res.getString("store_id"))) {
//                        AppConfig.showDialog(QRCodeActivity.this, "Success!", "ATTACK!");
                        final ProgressDialog progress = new ProgressDialog(QRCodeActivity.this);
                        progress.setMessage("Getting your data...");
                        progress.setIndeterminate(false);
                        progress.setCancelable(true);

                        RequestParams data = new RequestParams();
                        data.add("id", res.getString("id"));

                        Ajax.post("transaction/confirm/store", data, new JsonHttpResponseHandler() {
                            @Override
                            public void onStart() {
                                progress.show();
                            }

                            @Override
                            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                                if (statusCode == 200) {
                                    if (response.length() > 0) {
                                        AlertDialog.Builder builder = new AlertDialog.Builder(QRCodeActivity.this);
                                        builder.setTitle("Success!")
                                                .setCancelable(false)
                                                .setMessage("You had successfully confirmed this transaction!")
                                                .setNeutralButton("OK", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        finish();
                                                    }
                                                });
                                        AlertDialog alertDialog = builder.create();
                                        alertDialog.show();
                                    } else {
                                        Toast.makeText(QRCodeActivity.this, "Request Failed. Try again.", Toast.LENGTH_LONG).show();
                                        resumeQRScanning();
                                    }
                                } else {
                                    Toast.makeText(QRCodeActivity.this, "Request Failed. Try again.", Toast.LENGTH_LONG).show();
                                    resumeQRScanning();
                                }
                                Log.d("Result", response.toString());
                            }

                            @Override
                            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                                super.onFailure(statusCode, headers, responseString, throwable);
                                Log.d("Failed: ", ""+statusCode);
                                Log.d("Error : ", "" + throwable);
                                Toast.makeText(QRCodeActivity.this, "Request Failed. Try again.", Toast.LENGTH_LONG).show();
                                resumeQRScanning();
                            }

                            @Override
                            public void onFinish() {
                                progress.dismiss();
                            }
                        });
                    } else {
                        AlertDialog.Builder builder = new AlertDialog.Builder(QRCodeActivity.this);
                        builder.setTitle("Store not Match!")
                            .setCancelable(false)
                            .setMessage("The Drink this user want to get does not belong to your store!")
                            .setNeutralButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    resumeQRScanning();
                                }
                            });
                        AlertDialog alertDialog = builder.create();
                        alertDialog.show();
                    }

//                    AlertDialog.Builder builder = new AlertDialog.Builder(QRCodeActivity.this);
//                    builder.setTitle("Scanned Result");
//                    builder.setMessage("ID: " + res.getString("id") + " User ID: " + res.getString("user_id") + " Friend ID: " + res.getString("friend_id") + " Store ID: " + res.getString("store_id") + " Current Store ID: " + user_json.getInt("store_id"));
//                    AlertDialog alertDialog = builder.create();
//                    alertDialog.show();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
                Log.d("Failed: ", ""+statusCode);
                Log.d("Error : ", "" + throwable);
                Toast.makeText(QRCodeActivity.this, "Request Failed. Try again.", Toast.LENGTH_LONG).show();
                resumeQRScanning();
            }
        });
    }

    public void resumeQRScanning() {
        mScannerView.resumeCameraPreview(this);
    }
}
