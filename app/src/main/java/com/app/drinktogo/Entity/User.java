package com.app.drinktogo.Entity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Ken on 13/01/2017.
 */

public class User {
    public String data;

    public JSONObject record(){
        if(data.isEmpty()){
            return null;
        }else{
            try{
                JSONArray a = new JSONArray(data);
                JSONObject o = a.getJSONObject(0);
                if(o != null){
                    return o;
                }else{
                    return null;
                }
            } catch (JSONException e) {
                e.printStackTrace();
                return null;
            }
        }
    }
}
