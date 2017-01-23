package com.app.drinktogo.helper;

import android.app.Activity;
import android.support.v7.app.AlertDialog;


/**
 * Created by Ken on 13/01/2017.
 */

public class AppConfig {
    public static final String URL = "http://drinktogo.mybluemix.net/";

    public static void showDialog(Activity activity, String title, String message){
        AlertDialog.Builder dialog = new AlertDialog.Builder(activity);
        dialog.setTitle(title);
        dialog.setMessage(message);
        dialog.show();
    }
}
