package com.nikhidev.smstacker.ui.home;

import android.Manifest;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.android.material.textfield.TextInputEditText;
import com.nikhidev.smstacker.R;
import com.nikhidev.smstacker.SetWallpaperActivity;
import com.nikhidev.smstacker.databinding.FragmentHomeBinding;

public class HomeFragment extends Fragment {


    public final static int LOCATION_CODE_FINE = 101;
    public final static int LOCATION_CODE_ALL = 102;
    public final static int SMS_CODE = 103;
    private static final String TAG = "HomeFragment";

    private FragmentHomeBinding binding;
    HomeViewModel homeViewModel;
    SoundVibrateModel soundVibrateModel;
    FindMyPhoneModel findMyPhoneModel;
    View rootView;

    SwitchMaterial switchSilent;
    SwitchMaterial switchVolume;
    SwitchMaterial switchVibrate;
    SwitchMaterial switchLocation;
    SwitchMaterial switchWallpaper;

    TextInputEditText editTextSilent;
    TextInputEditText editTextVolume;
    TextInputEditText editTextVibrate;
    TextInputEditText editTextFindPhone;

    MaterialButton setWallpaperBtn;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        rootView = binding.getRoot();

        SoundVibrateModel xyz = new SoundVibrateModel();

        homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);





        // Views init
        initSoundVibrateModel();
        initLocationModel();



//        IntentFilter filter=new IntentFilter(AudioManager.RINGER_MODE_CHANGED_ACTION);
//        getContext().registerReceiver(new TestBroadcast(),filter);

        return rootView;
    }


    void initSoundVibrateModel(){
        soundVibrateModel = new ViewModelProvider(this).get(SoundVibrateModel.class);
        switchSilent = rootView.findViewById(R.id.switch_silent);
        switchVolume = rootView.findViewById(R.id.switch_volume);
        switchVibrate = rootView.findViewById(R.id.switch_vibration);
        editTextSilent = rootView.findViewById(R.id.edit_silent);
        editTextVolume = rootView.findViewById(R.id.edit_volume);
        editTextVibrate = rootView.findViewById(R.id.edit_vibration);

        // Data to View init
        switchSilent.setChecked(soundVibrateModel.getEnabled(SoundVibrateModel.SILENT));
        switchVolume.setChecked(soundVibrateModel.getEnabled(SoundVibrateModel.VOLUME));
        switchVibrate.setChecked(soundVibrateModel.getEnabled(SoundVibrateModel.VIBRATE));
        editTextSilent.setText(soundVibrateModel.getCommand(SoundVibrateModel.SILENT));
        editTextVolume.setText(soundVibrateModel.getCommand(SoundVibrateModel.VOLUME));
        editTextVibrate.setText(soundVibrateModel.getCommand(SoundVibrateModel.VIBRATE));

        // Events :
        switchSilent.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                try {
                    if(b){
                        int checkVal = getContext().checkCallingOrSelfPermission(Manifest.permission.READ_SMS);
                        if(checkVal != PackageManager.PERMISSION_GRANTED) {
                            switchSilent.setChecked(false);
                            getSMSReadPermission(checkVal);
                            return;
                        }
                        if(!getSilentPermission()) {
                            Toast.makeText(getActivity(), "Please allow Do Not Disturb access", Toast.LENGTH_SHORT).show();
                            switchSilent.setChecked(false);
                            return;
                        }
                    }
                    soundVibrateModel.updateEnabled(SoundVibrateModel.SILENT, b);

                } catch (Exception e) {
                    Toast.makeText(getActivity(), "Unable to update", Toast.LENGTH_SHORT).show();
                    switchSilent.setChecked(!b);
                }
            }
        });


        switchVolume.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){
                    int checkVal = getContext().checkCallingOrSelfPermission(Manifest.permission.READ_SMS);
                    if(checkVal != PackageManager.PERMISSION_GRANTED) {
                        switchVolume.setChecked(false);
                        getSMSReadPermission(checkVal);
                        switchVolume.setChecked(false);
                        return;
                    }
                }
                try {
                    soundVibrateModel.updateEnabled(SoundVibrateModel.VOLUME,b);
                } catch (Exception e) {
                    Toast.makeText(getActivity(), "Unable to update", Toast.LENGTH_SHORT).show();
                    switchVibrate.setChecked(!b);
                }
            }
        });

        switchVibrate.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){
                    int checkVal = getContext().checkCallingOrSelfPermission(Manifest.permission.READ_SMS);
                    if(checkVal != PackageManager.PERMISSION_GRANTED) {
                        switchVibrate.setChecked(false);
                        getSMSReadPermission(checkVal);
                        return;
                    }
                }
                try {
                    soundVibrateModel.updateEnabled(SoundVibrateModel.VIBRATE,b);
                } catch (Exception e) {
                    Toast.makeText(getActivity(), "Unable to update", Toast.LENGTH_SHORT).show();
                    switchVibrate.setChecked(!b);
                }
            }
        });

        editTextSilent.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(charSequence.toString().equals("")){
                    Toast.makeText(getActivity(), "Text Cannot be empty", Toast.LENGTH_SHORT).show();
                    soundVibrateModel.updateCommand(SoundVibrateModel.SILENT,"#");
                    editTextSilent.setText("#");
                    return;
                }
                soundVibrateModel.updateCommand(SoundVibrateModel.SILENT,charSequence.toString());
            }
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        editTextVolume.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(charSequence.toString().equals("")){
                    Toast.makeText(getActivity(), "Text Cannot be empty", Toast.LENGTH_SHORT).show();
                    soundVibrateModel.updateCommand(SoundVibrateModel.VOLUME,"@");
                    editTextVolume.setText("@");
                    return;
                }
                soundVibrateModel.updateCommand(SoundVibrateModel.VOLUME,charSequence.toString());
            }
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        editTextVibrate.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                if(charSequence.toString().equals("")){
                    Toast.makeText(getActivity(), "Text Cannot be empty", Toast.LENGTH_SHORT).show();
                    soundVibrateModel.updateCommand(SoundVibrateModel.VIBRATE,"!");
                    editTextVibrate.setText("!");
                    return;
                }
                soundVibrateModel.updateCommand(SoundVibrateModel.VIBRATE,charSequence.toString());
            }
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }
    void initLocationModel(){
        findMyPhoneModel = new ViewModelProvider(this).get(FindMyPhoneModel.class);
        switchLocation = rootView.findViewById(R.id.switch_location);
        switchWallpaper = rootView.findViewById(R.id.switch_wallpaper);
        editTextFindPhone = rootView.findViewById(R.id.edit_find_phone);
        setWallpaperBtn = rootView.findViewById(R.id.set_wallpaper_btn);


        setWallpaperBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), SetWallpaperActivity.class));

            }
        });
        // Data to View init
        switchLocation.setChecked(findMyPhoneModel.getEnabled(FindMyPhoneModel.LOCATION));
        editTextFindPhone.setText(findMyPhoneModel.getCommand());

        // Events :
        switchLocation.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                try {
                    if(b) {
                        if (!getLocationPermission()) {
                            switchLocation.setChecked(false);
                            return;
                        }
                        int checkVal = getContext().checkCallingOrSelfPermission(Manifest.permission.READ_SMS);
                        if(checkVal != PackageManager.PERMISSION_GRANTED){
                            switchLocation.setChecked(false);
                            getSMSReadPermission(checkVal);
                            return;
                        }
                    }
                    findMyPhoneModel.updateEnabled(FindMyPhoneModel.LOCATION,b);
                } catch (Exception e) {
                    Toast.makeText(getActivity(), "Unable to update", Toast.LENGTH_SHORT).show();
                    switchLocation.setChecked(!b);
                }
            }
        });


        switchWallpaper.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                try {
                    if(b) {
                        int checkVal = getContext().checkCallingOrSelfPermission(Manifest.permission.READ_SMS);

                        if(checkVal != PackageManager.PERMISSION_GRANTED){
                            switchWallpaper.setChecked(false);
                            getSMSReadPermission(checkVal);
                            return;
                        }
                        checkVal = getContext().checkCallingOrSelfPermission(Manifest.permission.SET_WALLPAPER);
                        if(checkVal != PackageManager.PERMISSION_GRANTED){
                            switchWallpaper.setChecked(false);
                            getWallpaperPermission(checkVal);
                            return;
                        }
                    }
                    findMyPhoneModel.updateEnabled(FindMyPhoneModel.WALLPAPER,b);
                } catch (Exception e) {
                    Toast.makeText(getActivity(), "Unable to update", Toast.LENGTH_SHORT).show();
                    switchWallpaper.setChecked(!b);
                }
            }
        });

        editTextFindPhone.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(charSequence.toString().equals("")){
                    Toast.makeText(getActivity(), "Text Cannot be empty", Toast.LENGTH_SHORT).show();
                    findMyPhoneModel.updateCommand("%");
                    editTextFindPhone.setText("%");
                    return;
                }
                findMyPhoneModel.updateCommand(charSequence.toString());
            }
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    boolean getSMSReadPermission(int isGranted){
        if(isGranted == PackageManager.PERMISSION_GRANTED){
            return true;
        }else {
            Toast.makeText(getActivity(), "SMS Read/Write permission is required to activate commands", Toast.LENGTH_SHORT).show();
            ActivityCompat.requestPermissions(getActivity(), new String[] { Manifest.permission.READ_SMS,Manifest.permission.SEND_SMS,Manifest.permission.RECEIVE_SMS}, 101);
        }

        return false;
    }

    boolean getSilentPermission(){
        NotificationManager mNotificationManager = (NotificationManager) getActivity().getSystemService(Context.NOTIFICATION_SERVICE);
        // Check if the notification policy access has been granted for the app.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!mNotificationManager.isNotificationPolicyAccessGranted()) {
                Intent intent = new Intent(Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS);
                startActivityForResult(intent,123);
                return false;
            }else{
                return true;
            }
        }

        return true;
    }
    boolean getWallpaperPermission(int checkVal){

        if (checkVal == PackageManager.PERMISSION_GRANTED) {
            return true;
        }

        ActivityCompat.requestPermissions(getActivity(),
                new String[]{
                        Manifest.permission.SET_WALLPAPER,
                }, 101);

        Toast.makeText(getActivity(), "Wallpaper Permission is required", Toast.LENGTH_SHORT).show();
        return false;
    }
    boolean getLocationPermission() {

        int c1 = getContext().checkCallingOrSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION);
        int c2 = getContext().checkCallingOrSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION);
        int c3 = 0;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            c3 = getContext().checkCallingOrSelfPermission(Manifest.permission.ACCESS_BACKGROUND_LOCATION);
        }

        int GRANTED = PackageManager.PERMISSION_GRANTED;



        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            if (c2 == GRANTED || c3 == GRANTED) {
                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{
                                Manifest.permission.ACCESS_BACKGROUND_LOCATION,
                                Manifest.permission.ACCESS_FINE_LOCATION,
                                Manifest.permission.ACCESS_COARSE_LOCATION,
                        }, LOCATION_CODE_FINE);
                Toast.makeText(getActivity(), "Go to App Info -> Permission -> Location -> Allow all the time", Toast.LENGTH_LONG).show();
                return false;
            } else {
                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{
                                Manifest.permission.ACCESS_FINE_LOCATION,
                                Manifest.permission.ACCESS_COARSE_LOCATION,
                        }, LOCATION_CODE_FINE);
            }

        } else {
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION,
                    }, 101);
        }

        Toast.makeText(getActivity(), "Select Precise Location and Only this time", Toast.LENGTH_LONG).show();
        return false;

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}