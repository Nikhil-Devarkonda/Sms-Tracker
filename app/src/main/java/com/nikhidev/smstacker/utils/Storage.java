package com.nikhidev.smstacker.utils;

import android.os.Environment;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.Buffer;

public class Storage {
    private String TAG = "Storage";
    final private String FILENAME = "storage.json";
    final static private String IMAGENAME = "lock_wallpaper.jpg";
    final static private File storageDir = new File("/data/user/0/com.nikhidev.smstacker/files/");
    public JSONObject root = null;
    final static  public String SOUNDKEY = "SOUND_VIBRATE";
    final static  public String FINDPHONEKEY = "FIND_PHONE";
    private File storageFile;
    private int VERSION = 1;

    public Storage(){

        // initialise
        storageFile = new File(storageDir,FILENAME);
        Log.d(TAG, "Storage: "+storageFile.getAbsolutePath());
        if(!storageFile.exists()){
         initialise(storageFile);
        }else{
            load();
        }
    }

    void load(){
        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(storageFile));
            StringBuffer stringBuffer = new StringBuffer();

            int i;
            while ((i = bufferedReader.read()) != -1){
                stringBuffer.append((char)i);
            }
            root = new  JSONObject(stringBuffer.toString());

            if(root.getInt("VERSION") < VERSION){
                initialise(storageFile);
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    void initialise(File storageFile){
        try {
            // Making JSON data
            root = new JSONObject();

            // Sound and Vibration [Module]
            JSONObject sounds = new JSONObject();
            JSONObject s1 = new JSONObject();
            s1.put("enabled", false);
            s1.put("name", "SILENT");
            s1.put("command","smstracker#silent");
            JSONObject s2 = new JSONObject();
            s2.put("enabled", false);
            s2.put("name", "VOLUME");
            s2.put("command","smstracker#volume");
            JSONObject s3 = new JSONObject();
            s3.put("enabled", false);
            s3.put("name", "VIBRATE");
            s3.put("command","smstracker#vibrate");
            sounds.put("SILENT",s1);
            sounds.put("VOLUME",s2);
            sounds.put("VIBRATE",s3);
            sounds.put("enable",s3);
            root.put(SOUNDKEY,sounds);

            // Find my phone [Module]

            JSONObject f1 = new JSONObject();
            f1.put("enabled", false);
            f1.put("name", "LOCATION");
            JSONObject f2= new JSONObject();
            f2.put("enabled", false);
            f2.put("name", "WALLPAPER");
            f2.put("message", "This mobile is lost by\nowner\nPlease call\n{}\n to return it");
            JSONObject findPhone = new JSONObject();
            findPhone.put("command","smstracker#find_phone");
            findPhone.put("LOCATION",f1);
            findPhone.put("WALLPAPER",f2);
            root.put(FINDPHONEKEY,findPhone);


            root.put("VERSION",VERSION);

            // Write JSON data to file
            String jsonString = root.toString(2);

            BufferedWriter bW = new BufferedWriter(new FileWriter(storageFile));
            bW.write(jsonString);
            bW.close();
            Log.d(TAG, "initialise: Successfully");

        }
        catch (Exception e){
            Log.d(TAG, "initialise: Error : "+e);
        }
    }

    public void update() throws JSONException, IOException {
        String jsonString = root.toString(2);
        BufferedWriter bW = new BufferedWriter(new FileWriter(storageFile));
        bW.write(jsonString);
        bW.close();
        Log.d(TAG, "update: Successfully");
    }

    public static File getWallpaperFile(){
        File file = new File(storageDir,IMAGENAME);
        return file;
    }


}


