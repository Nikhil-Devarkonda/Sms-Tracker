package com.nikhidev.smstacker.ui.home;

import androidx.lifecycle.ViewModel;

import com.nikhidev.smstacker.utils.Storage;

import org.json.JSONException;
import org.json.JSONObject;

public class SoundVibrateModel extends ViewModel {
    private boolean isEnabled = false;
    public static final String SILENT = "SILENT";
    public static final String VOLUME = "VOLUME";
    public static final String VIBRATE = "VIBRATE";
    private JSONObject data;
    Storage storage;

    public SoundVibrateModel() {
        try {
            storage = new Storage();
            data = storage.root.getJSONObject(Storage.SOUNDKEY);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void updateCommand(String key, String value) {
        try {
            JSONObject obj = data.getJSONObject(key);
            obj.put("command", value);
            storage.update();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void updateEnabled(String key, boolean value) throws Exception {
        JSONObject obj = data.getJSONObject(key);
        obj.put("enabled", value);
        storage.update();

    }

    public String getCommand(String key) {
        try {
            JSONObject obj = data.getJSONObject(key);
            return obj.getString("command");
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
