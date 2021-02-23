package com.example.noxsocial;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatTextView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.github.ybq.android.spinkit.SpinKitView;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.mahfa.dnswitch.DayNightSwitch;
import com.mahfa.dnswitch.DayNightSwitchListener;

import org.json.JSONException;
import org.json.JSONObject;

import app.*;
import cz.msebera.android.httpclient.Header;
import objects.LoginObject;
import objects.UserObject;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    ImageView sun , night_landscape , day_landscape ;
    View night_background , day_background ;
    DayNightSwitch dayNightSwitch ;
    public static int dn = 1 ;

    AppCompatTextView register , loin ;
    EditText email , password ;
    SpinKitView progressBar ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        initViews();
    }

    private void initViews() {

        sun              = findViewById(R.id.sun) ;
        night_landscape  = findViewById(R.id.night_landscape) ;
        day_landscape    = findViewById(R.id.day_landscape) ;

        night_background = findViewById(R.id.night_background) ;
        day_background   = findViewById(R.id.day_background) ;

        dayNightSwitch   = findViewById(R.id.dayNightSwitch) ;

        register         = findViewById(R.id.register) ;
        loin             = findViewById(R.id.login) ;
        email            = findViewById(R.id.email) ;
        password         = findViewById(R.id.password) ;
        progressBar      = findViewById(R.id.progressBar) ;

        register.setOnClickListener(this);
        loin.setOnClickListener(this);

        sun.setTranslationY(-110);
        dayNightSwitch.setListener(new DayNightSwitchListener() {
            @Override
            public void onSwitch(boolean is_night) {

                if (is_night){
                    dn = 0 ;
                    sun.animate().translationY(110).setDuration(1000);
                    day_landscape.animate().alpha(0).setDuration(1300);
                    day_background.animate().alpha(0).setDuration(1300);

                }else {
                    dn = 1;
                    sun.animate().translationY(-110).setDuration(1000);
                    day_landscape.animate().alpha(1).setDuration(1300);
                    day_background.animate().alpha(1).setDuration(1300);

                }
            }
        });

    }

    @Override
    public void onClick(View v) {
        if (v == register){
            Intent intent = new Intent(this , RegistrationActivity.class);
            startActivity(intent);
        }

        if (v == loin){

            login();
        }
    }

    private void login() {

        if (email.getText().toString().length()<5){

            app.t("Please Enter a Valid Email !" , ToastType.ERROR);
            app.animateError(email);
            return;
        }

        else if (password.getText().toString().length()<3){

            app.t("Please Enter a Password includes at Least 3 Chars !" , ToastType.ERROR);
            app.animateError(password);
            return;
        }

        RequestParams params = app.getRequestParams();
        params.put(ROUTER.ROUTE  , ROUTER.ROUTE_LOGIN);
        params.put(ROUTER.ACTION , ROUTER.ACTION_LOGIN);
        params.put(ROUTER.INPUT_EMAIL    , email.getText().toString());
        params.put(ROUTER.INPUT_PASSWORD , password.getText().toString());

        SocialClient.post(params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {


                String response = new String(responseBody) ;
                app.l("Response : " + response);

                try {

                    JSONObject jsonObject = new JSONObject(response) ;
                    if (jsonObject.has("error")){

                        app.t(jsonObject.getString("error") , ToastType.ERROR);
                    }else {

                        LoginObject loginObject = new Gson().fromJson(response , LoginObject.class);


                        if (loginObject.getState().equals(ROUTER.SUCCESS)){

                            String name = loginObject.getUserObject().getNAME() ;
                            app.t("Welcome " + name , ToastType.SUCCESS);

                            spref.getOurInstance().edit()
                                    .putInt(ROUTER.USER_ID           , loginObject.getUserObject().getUSER_ID())
                                    .putString(ROUTER.INPUT_EMAIL    , email.getText().toString())
                                    .putString(ROUTER.INPUT_USERNAME , loginObject.getUserObject().getUSERNAME())
                                    .putString(ROUTER.INPUT_NAME     , loginObject.getUserObject().getNAME())
                                    .putInt(ROUTER.INPUT_SEX         , loginObject.getUserObject().getSEX())
                                    .putString(ROUTER.SESSION        , loginObject.getUserObject().getSESSION())
                                    .apply();

                            spref.getOurInstance().edit()
                                    .putInt(ROUTER.PROFILE_ID        , loginObject.getProfileObject().getId())
                                    .putString(ROUTER.PROFILE_IMAGE  , loginObject.getProfileObject().getImage())
                                    .apply();


                            Intent intent = new Intent(LoginActivity.this , MainActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);

                        }else {
                            app.t("Something went wrong!" , ToastType.ERROR);
                        }
                    }


                } catch (JSONException e) {
                    app.l("JSONException is : " + e.toString());
                }


            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

                app.t("Check your connection!" , ToastType.ERROR);
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