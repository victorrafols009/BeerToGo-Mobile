package com.app.drinktogo;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.app.drinktogo.helper.Ajax;
import com.app.drinktogo.helper.AppConfig;
import com.app.drinktogo.helper.DatabaseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

public class LoginActivity extends AppCompatActivity {

    private EditText email;
    private EditText password;
    private Button login;

    private DatabaseHandler db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        email = (EditText) findViewById(R.id.email);
        password = (EditText) findViewById(R.id.password);
        login = (Button) findViewById(R.id.login);

        final ProgressDialog progress = new ProgressDialog(LoginActivity.this);
        progress.setMessage("Logging in...");
        progress.setIndeterminate(false);
        progress.setCancelable(false);

        db = new DatabaseHandler(this);

        if(!db.getUser().data.isEmpty()){
            Intent activity= new Intent(LoginActivity.this, MainActivity.class);
            startActivity(activity);
            finish();
        }

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isValidEmail(email.getText().toString())){

                        RequestParams data = new RequestParams();
                        data.add("email", email.getText().toString());
                        data.add("password", password.getText().toString());

                    Ajax.post("auth/login", data, new JsonHttpResponseHandler(){
                        @Override
                        public void onStart() {
                            progress.show();
                        }

                        @Override
                        public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                            if (statusCode == 200) {
                                if(response.length() > 0){
                                    db.addUser(response.toString());
                                    Intent activity= new Intent(LoginActivity.this, MainActivity.class);
                                    startActivity(activity);
                                    finish();
                                }else{
                                    AppConfig.showDialog(LoginActivity.this, "Message", "Invalid Username or Password.");
                                }
                            } else {
                                AppConfig.showDialog(LoginActivity.this, "Message", "There is problem in your request. Please try again.");
                            }
                            Log.d("Result", response.toString());
                        }

                        @Override
                        public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                            Log.d("Failed: ", ""+statusCode);
                            Log.d("Error : ", "" + throwable);
                            AppConfig.showDialog(LoginActivity.this, "Message", "There is problem in your request. Please try again.");
                        }

                        @Override
                        public void onFinish() {
                            progress.dismiss();
                        }
                    });
                }else{
                    AppConfig.showDialog(LoginActivity.this, "Message", "Invalid email input. Please check your email.");
                }
            }
        });
    }

    boolean isValidEmail(String email){
        if(email.isEmpty()){
            return false;
        }else{
            String ePattern = "[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*@(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?";
            java.util.regex.Pattern p = java.util.regex.Pattern.compile(ePattern);
            java.util.regex.Matcher m = p.matcher(email);
            return m.matches();
        }
    }
}
