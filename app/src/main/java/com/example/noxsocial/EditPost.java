package com.example.noxsocial;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatImageView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.bumptech.glide.Glide;
import com.github.ybq.android.spinkit.SpinKitView;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import app.app;
import cz.msebera.android.httpclient.Header;
import app.*;
import fragments.ProfileFragment;
import objects.PostObject;

public class EditPost extends AppCompatActivity implements View.OnClickListener {

    AppCompatImageView imageView, submit , delete;
    AppCompatEditText caption ;
    PostObject postObject ;
    SpinKitView progressBar ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_post);

        init();

    }

    private void init() {
        imageView = findViewById(R.id.image);
        submit    = findViewById(R.id.submit);
        delete    = findViewById(R.id.delete) ;
        caption   = findViewById(R.id.caption) ;
        progressBar = findViewById(R.id.progressBar);
        postObject = (PostObject) getIntent().getSerializableExtra("object");



        Glide.with(this).load(app.main.URL + postObject.getImage()).into(imageView);
        caption.setText(postObject.getCaption());


        submit.setOnClickListener(this);
        delete.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v == submit){
        edit();
        finish();
        }else if (v == delete){

            AlertDialog.Builder alert = new AlertDialog.Builder(this);
            alert.setTitle("Delete post");
            alert.setMessage( "Are you sure you want to delete the post?");
            alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    deleteFunction();
                }
            });

            alert.setNegativeButton("No", null);
            alert.show();


        }
    }

    private void deleteFunction() {

        RequestParams params = app.getRequestParams();
        params.put(ROUTER.ROUTE , ROUTER.ROUTE_POSTS);
        params.put(ROUTER.ACTION , ROUTER.ACTION_DELETE);
        params.put(ROUTER.INPUT_POST_ID , postObject.getId());

        SocialClient.post(params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {

                String response = new String(responseBody);


                if (response.equals(ROUTER.SUCCESS)){

                    finish();
                }

                else
                    try {
                        JSONObject jsonObject = new JSONObject(response);

                        if (jsonObject.has("error")){
                            app.t(jsonObject.getString("error") , ToastType.ERROR);
                        }


                    }catch (JSONException e){

                        app.t("Something Went Wrong" , ToastType.ERROR);
                    }

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

            }

            @Override
            public void onStart() {
                progressBar.setVisibility(View.VISIBLE);
                super.onStart();
            }

            @Override
            public void onFinish() {
                progressBar.setVisibility(View.INVISIBLE);
                super.onFinish();
            }
        });



    }

    private void edit() {

        if (caption.getText().toString().length() == 0){
            app.t("Please enter a title" , ToastType.ERROR);
            app.animateError(caption);
            return;
        }



        RequestParams params = app.getRequestParams();
        params.put(ROUTER.ROUTE , ROUTER.ROUTE_POSTS);
        params.put(ROUTER.ACTION , ROUTER.ACTION_EDIT);
        params.put(ROUTER.INPUT_POST_ID , postObject.getId());
        params.put(ROUTER.INPUT_CAPTION   , caption.getText().toString());

        SocialClient.post(params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {

                String response = new String(responseBody);

                try {
                    JSONObject object = new JSONObject(response);

                    if(object.has("error")) {
                        app.t(object.getString("error") , ToastType.ERROR);
                    }
                    else if(object.has("status") && object.getString("status").equals("SUCCESS")){

                        spref.getOurInstance().edit().remove(ROUTER.INPUT_CAPTION).apply();
                        finish();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

            }

            @Override
            public void onStart() {
                super.onStart();

            }

            @Override
            public void onFinish() {
                super.onFinish();
            }
        });


    }
}