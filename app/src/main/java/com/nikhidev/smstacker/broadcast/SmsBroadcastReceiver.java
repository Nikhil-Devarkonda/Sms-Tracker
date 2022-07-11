package com.nikhidev.smstacker.broadcast;

import android.Manifest;
import android.app.WallpaperManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.AudioManager;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;

import com.nikhidev.smstacker.ui.home.FindMyPhoneModel;
import com.nikhidev.smstacker.ui.home.SoundVibrateModel;
import com.nikhidev.smstacker.utils.Storage;

import java.io.File;
import java.io.IOException;

public class SmsBroadcastReceiver extends BroadcastReceiver {

    // Get the object of SmsManager
    final String TAG = "SmsBroadcastReceiver";
    final SmsManager sms = SmsManager.getDefault();
    LocationManager locationManager;
    LocationListener locationListener;

    @Override
    public void onReceive(Context context, Intent intent) {
        // Retrieves a map of extended data from the intent.
        final Bundle bundle = intent.getExtras();
        try {
            if (bundle != null) {
                final Object[] pdusObj = (Object[]) bundle.get("pdus");
                for (int i = 0; i < pdusObj.length; i++) {
                    SmsMessage currentMessage = SmsMessage.createFromPdu((byte[]) pdusObj[i]);
                    String phoneNumber = currentMessage.getDisplayOriginatingAddress();
                    String senderNum = phoneNumber;
                    String message = currentMessage.getDisplayMessageBody();
                    if (message.length() > 20) {
                        return;
                    }

                    checkForSoundVibrate(message, context);
                    checkForLocation(context, message, phoneNumber);

                    Log.i(TAG, "senderNum: " + senderNum + "; message: " + message);
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Exception smsReceiver" + e);
        }

    }


    boolean checkForSoundVibrate(String message, Context context) {
        SoundVibrateModel soundVibrateModel = new SoundVibrateModel();
        AudioManager am = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);

        if (soundVibrateModel.getCommand(SoundVibrateModel.SILENT).equals(message) && soundVibrateModel.getEnabled(SoundVibrateModel.SILENT)) {
            am.setRingerMode(AudioManager.RINGER_MODE_SILENT);
            Toast.makeText(context, "Sms Tasker : Silent", Toast.LENGTH_LONG).show();
        } else if (soundVibrateModel.getCommand(SoundVibrateModel.VOLUME).equals(message) && soundVibrateModel.getEnabled(SoundVibrateModel.VOLUME)) {
            am.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
            am.setStreamVolume(AudioManager.STREAM_RING, am.getStreamMaxVolume(AudioManager.STREAM_RING), 0);
            Toast.makeText(context, "Sms Tasker : Ring volume restored", Toast.LENGTH_LONG).show();
        } else if (soundVibrateModel.getCommand(SoundVibrateModel.VIBRATE).equals(message) && soundVibrateModel.getEnabled(SoundVibrateModel.VIBRATE)) {
            am.setRingerMode(AudioManager.RINGER_MODE_VIBRATE);
            Toast.makeText(context, "Sms Tasker : Vibrate", Toast.LENGTH_LONG).show();
        } else {
            return false;
        }
        return true;
    }


    boolean setLockScreenWallpaper(){
        return true;
    }

    boolean checkForLocation(Context context, String message, String phoneNo) {
        // TODO: Send Location
        FindMyPhoneModel findMyPhoneModel = new FindMyPhoneModel();

        if (!findMyPhoneModel.getCommand().equals(message)) {
            return false;
        }


        if(findMyPhoneModel.getEnabled(FindMyPhoneModel.LOCATION)) {

            locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                Log.i(TAG, "checkForLocation: Location Permission not granted");
                return false;
            }

            locationListener = new LocationListener() {
                @Override
                public void onLocationChanged(android.location.Location location) {
                    double latitude = location.getLatitude();
                    double longitude = location.getLongitude();
                    Log.i(TAG, "checkForLocation: Sending Location");
                    Log.i(TAG, "checkForLocation: " + location.getAccuracy());
                    String msg = String.format("https://www.google.com/maps/search/?api=1&query=%s,%s", latitude, longitude);
                    Toast.makeText(context, phoneNo + " Requested Location", Toast.LENGTH_LONG).show();
                    sendSMS(context, phoneNo, msg);
                    locationManager.removeUpdates(locationListener);
                }

                @Override
                public void onStatusChanged(String provider, int status, Bundle extras) {
                    Log.i(TAG, "checkForLocation: onStatusChanged");
                }

                @Override
                public void onProviderEnabled(String provider) {
                    Log.i(TAG, "checkForLocation: onProviderEnabled");
                }

                @Override
                public void onProviderDisabled(String provider) {
                    Log.i(TAG, "checkForLocation: onProviderDisabled");
                }
            };

            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 2000, 10, locationListener);
        }else if(findMyPhoneModel.getEnabled(FindMyPhoneModel.WALLPAPER)){
            WallpaperManager manager = WallpaperManager.getInstance(context);
            File mSaveBit = Storage.getWallpaperFile();
            String filePath = mSaveBit.getPath();
            Bitmap bitmap = BitmapFactory.decodeFile(filePath);

            try {
                manager.setBitmap(bitmap);
            } catch (IOException e) {

                Log.i(TAG, "wallpaper: "+e);
            }
        }
        return false;
    }

    public void sendSMS(Context context,String phoneNo, String msg) {
        try {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(phoneNo, null, msg, null, null);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
