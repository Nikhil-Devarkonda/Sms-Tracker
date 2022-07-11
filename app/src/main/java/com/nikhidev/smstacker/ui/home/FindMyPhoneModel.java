package com.nikhidev.smstacker.ui.home;

import android.util.Log;

import androidx.lifecycle.ViewModel;

import com.nikhidev.smstacker.utils.Storage;

import org.json.JSONObject;

public class FindMyPhoneModel extends ViewModel {
    private boolean isEnabled = false;
    private JSONObject data;
    Storage storage;
    final String TAG = "FindMyPhoneModel";

    public final static String LOCATION = "LOCATION";
    public final static String WALLPAPER = "WALLPAPER";

    public FindMyPhoneModel() {
        try {
            storage = new Storage();
            data = storage.root.getJSONObject(Storage.FINDPHONEKEY);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void updateCommand(String value) {
        try {
            data.put("command", value);
            storage.update();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void updateWallpaperMsg(String msg){
        try {
            JSONObject obj = data.getJSONObject(WALLPAPER);
            obj.put("message",msg);
            storage.update();
        } catch (Exception e) {
            Log.d(TAG, "updateWallpaperMsg: "+e);
        }
    }
    public String getWallpaperMsg(){
        try {
            JSONObject obj = data.getJSONObject(WALLPAPER);
            return obj.getString("message");
        } catch (Exception e) {
            Log.d(TAG, "getWallpaperMsg: "+e);
        }
        return "error";
    }

    public void updateEnabled(String key, boolean value) throws Exception {
        JSONObject obj = data.getJSONObject(key);
        obj.put("enabled", value);
        storage.update();
    }

    public String getCommand() {
        try {
            return data.getString("command");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "error";
    }

    public boolean getEnabled(String key) {
        try {
            JSONObject obj = data.getJSONObject(key);
            return obj.getBoolean("enabled");
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

}
