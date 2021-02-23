package com.example.noxsocial;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import app.app;
import de.hdodenhof.circleimageview.CircleImageView;

import static app.Application.config;
import static app.Application.options;
import app.*;

public class EditProfileActivity extends AppCompatActivity {
    CircleImageView profile_image ;
    AppCompatEditText email , username , name , bio ;
    AppCompatImageView back ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        init();
    }

    private void init() {

        profile_image = findViewById(R.id.profile_image) ;
        email     =  findViewById(R.id. email   ) ;
        username  =  findViewById(R.id. username) ;
        name      =  findViewById(R.id. name    ) ;
        bio       =  findViewById(R.id. bio     ) ;
        back      =  findViewById(R.id. back    ) ;

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        email.setText(spref.getOurInstance().getString(ROUTER.INPUT_EMAIL , ""));
        username.setText(spref.getOurInstance().getString(ROUTER.INPUT_USERNAME , ""));
        name.setText(spref.getOurInstance().getString(ROUTER.INPUT_NAME , ""));
        bio.setText(spref.getOurInstance().getString(ROUTER.INPUT_BIO , ""));

        ImageLoader imageLoader = ImageLoader.getInstance();
        imageLoader.init(config);

        imageLoader.displayImage(app.main.URL + spref.getOurInstance().getString("PROFILE_IMAGE" , ""), profile_image, options, new ImageLoadingListener() {
            @Override
            public void onLoadingStarted(String imageUri, View view) {

                app.l("onLoadingStarted");
            }
            @Override
            public void onLoadingFailed(String imageUri, View view, FailReason failReason) {

                app.l("onLoadingFailed");
            }
            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {

                app.l("onLoadingComplete");
            }
            @Override
            public void onLoadingCancelled(String imageUri, View view) {

                app.l("onLoadingCancelled");
            }
        });

    }
}