package com.nikhidev.smstacker;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.nikhidev.smstacker.ui.home.FindMyPhoneModel;
import com.nikhidev.smstacker.ui.home.HomeViewModel;

import java.util.Set;

public class SetWallpaperActivity extends AppCompatActivity {

    TextInputEditText editMessage;
    LinearLayout screenLay;
    TextView textView;
    FindMyPhoneModel findMyPhoneModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_wallpaper);
        findMyPhoneModel = new ViewModelProvider(this).get(FindMyPhoneModel.class);
        WallpaperPreviewActivity.status = false;


        editMessage = findViewById(R.id.edit_message);
//        preview = findViewById(R.id.preview);
        textView = findViewById(R.id.tv_message);
        screenLay = findViewById(R.id.screen_lay);

        editMessage.setText(findMyPhoneModel.getWallpaperMsg());
        textView.setText(editMessage.getText().toString());

        ((MaterialButton)findViewById(R.id.preview_btn)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(SetWallpaperActivity.this,WallpaperPreviewActivity.class);
                i.putExtra("message",editMessage.getText().toString());
                i.putExtra("is_set",false);
                startActivity(i);
            }
        });

        ((MaterialButton)findViewById(R.id.set_wall_btn)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                String msg  = editMessage.getText().toString();
                if(msg.equals("")){
                    Toast.makeText(SetWallpaperActivity.this,"Message cannot be empty",Toast.LENGTH_SHORT).show();
                    return;
                }

                Intent i = new Intent(SetWallpaperActivity.this,WallpaperPreviewActivity.class);
                i.putExtra("message",msg);
                i.putExtra("is_set",true);
                startActivity(i);

            }
        });





        editMessage.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                textView.setText(charSequence.toString());



            }
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
            @Override
            public void afterTextChanged(Editable editable) {

            }
        });



    }


    @Override
    protected void onResume() {
        super.onResume();
        Log.d("SetWallpaper", "onResume: ");

        if(WallpaperPreviewActivity.status){
            findMyPhoneModel.updateWallpaperMsg(editMessage.getText().toString());
            finish();
        }
    }
}