package com.app.beertogo.helper;

import android.util.Log;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

/**
 * Created by Ken on 13/01/2017.
 */

public class Ajax {

    private static final String BASE_URL = AppConfig.URL;

    private static AsyncHttpClient client;

    public static AsyncHttpClient get(String url, RequestParams params, JsonHttpResponseHandler responseHandler) {
        Log.d("GET REQUEST", getAbsoluteUrl(url));
        client = new AsyncHttpClient();
        client.setMaxRetriesAndTimeout(1, 500);
        client.get(getAbsoluteUrl(url), params, responseHandler);
        return client;
    }

    public static AsyncHttpClient post(String url, RequestParams params, JsonHttpResponseHandler responseHandler) {
        Log.d("POST REQUEST", getAbsoluteUrl(url));
        client = new AsyncHttpClient();
        client.setMaxRetriesAndTimeout(1, 500);
        client.post(getAbsoluteUrl(url), params, responseHandler);
        return client;
    }

    private static String getAbsoluteUrl(String relativeUrl) {
        return BASE_URL + relativeUrl;
    }

}
