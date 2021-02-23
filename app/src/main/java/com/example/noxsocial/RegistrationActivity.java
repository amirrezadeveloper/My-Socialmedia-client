package com.example.noxsocial;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;

import com.github.ybq.android.spinkit.SpinKitView;
import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import app.app;
import app.*;
import cz.msebera.android.httpclient.Header;
import objects.LoginObject;

public class RegistrationActivity extends AppCompatActivity implements View.OnClickListener {

    ImageView sun , night_landscape , day_landscape ;
    View night_background , day_background ;
    AppCompatImageView back ;

    EditText email , username , password , repassword , name ;
    RadioGroup sex ;
    AppCompatTextView signUp ;
    SpinKitView progressBar ;
    int sexInt = 1 ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        initViews();
    }
    

    private void initViews() {

        sun              = findViewById(R.id.sun) ;
        night_landscape  = findViewById(R.id.night_landscape) ;
        day_landscape    = findViewById(R.id.day_landscape) ;

        night_background = findViewById(R.id.night_background) ;
        day_background   = findViewById(R.id.day_background) ;

        back             = findViewById(R.id.back) ;
        back.setOnClickListener(this);


        email       = findViewById(R.id.email);
        username    = findViewById(R.id.username);
        password    = findViewById(R.id.password);
        repassword  = findViewById(R.id.repassword);
        name        = findViewById(R.id.name);
        sex         = findViewById(R.id.sex);
        signUp      = findViewById(R.id.signUp);
        progressBar = findViewById(R.id.progressBar) ;

        signUp.setOnClickListener(this);

                if (LoginActivity.dn == 0){
                    sun.setTranslationY(110);
                    day_landscape.setAlpha(0f);
                    day_background.setAlpha(0f);
                }else if (LoginActivity.dn == 1){
                    sun.setTranslationY(-110);
                    day_landscape.setAlpha(1f);
                    day_background.setAlpha(1f);
                }


    }

    @Override
    public void onClick(View v) {
        if (v == back){
            finish();
        }else {

            submit();
        }
    }

    private void submit() {

        if (email.getText().toString().length()<5 ){
            app.t("Please enter your correct email" , ToastType.ERROR);
            app.animateError(email);
            return;
        }

        if (password.getText().toString().length()<3){
            app.t("Your password is too short Enter a more secure password" , ToastType.ERROR);
            app.animateError(password);
            return;
        }
        if (!password.getText().toString().equals(repassword.getText().toString())){
            app.t("The passwords entered do not match" , ToastType.ERROR);
            app.animateError(password);
            app.animateError(repassword);
            return;
        }

        if (username.getText().toString().length()<3){
            app.t("Please enter your correct first name" , ToastType.ERROR);
            app.animateError(username);
            return;
        }
        if (name.getText().toString().length()<3){
            app.t("Please enter your correct last name" , ToastType.ERROR);
            app.animateError(name);
            return;
        }

        if (sex.getCheckedRadioButtonId() == R.id.female) sexInt = 0 ;
        else if (sex.getCheckedRadioButtonId() == R.id.other) sexInt = 2 ;


        RequestParams params = app.getRequestParams();
        params.put(ROUTER.ROUTE , ROUTER.ROUTE_LOGIN);
        params.put(ROUTER.ACTION , ROUTER.ACTION_REGISTRATION);
        params.put(ROUTER.INPUT_EMAIL , email.getText().toString());
        params.put(ROUTER.INPUT_USERNAME , username.getText().toString());
        params.put(ROUTER.INPUT_PASSWORD , password.getText().toString());
        params.put(ROUTER.INPUT_NAME , name.getText().toString());
        params.put(ROUTER.INPUT_SEX , sexInt+"");


        SocialClient.post(params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {

                String response = new String(responseBody);

                try {

                    JSONObject jsonObject = new JSONObject(response);

                    if (jsonObject.has("error")){
                        app.t(jsonObject.getString("error") , ToastType.ERROR);
                    }
                    else {
                        LoginObject loginObject = new Gson().fromJson(response , LoginObject.class);

                        if (loginObject.getState().equals(ROUTER.SUCCESS)){

                            String welcomeName = name.getText().toString() ;
                            app.t( "Welcome " + welcomeName, ToastType.SUCCESS);

                            spref.getOurInstance().edit()
                                    .putString(ROUTER.SESSION     , loginObject.getSession())
                                    .putString(ROUTER.INPUT_EMAIL     , email.getText().toString())
                                    .putInt(ROUTER.USER_ID        , loginObject.getId())
                                    .putString(ROUTER.INPUT_USERNAME , username.getText().toString())
                                    .putString(ROUTER.INPUT_NAME , name.getText().toString())
                                    .putInt(ROUTER.INPUT_SEX      , sexInt).apply();


                            Intent intent = new Intent(RegistrationActivity.this , MainActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                        }
                        else {

                            app.t("Something Went Wrong" , ToastType.ERROR);
                        }
                    }

                }catch (JSONException e){

                    app.t("Something Went Wrong" , ToastType.ERROR);
                    // app.l(e.toString());
                }

                app.l("response : " + response);

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

                app.l("onFailure");
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
}