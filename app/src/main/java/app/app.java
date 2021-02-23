package app;

import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

import cz.msebera.android.httpclient.Header;
import es.dmoral.toasty.Toasty;
import interfaces.SocialLikeChangeListener;
import objects.PostObject;

import static app.SocialClient.post;

public class app {


    public static class main{

        public static final String TAG = "NoxSocial" ;
        public static final String URL = "http://192.168.88.243/social/" ;
        //http://10.0.2.2/social my fucking fault : i forgot just a " / " after social .
    }

    public static void l(String message){
        Log.e(main.TAG , message);
    }
    public static void t(String message , ToastType type){

        switch (type){
            case ERROR:    Toasty.error(Application.getContext()   , message , Toast.LENGTH_LONG).show(); break;
            case WARNING:  Toasty.warning(Application.getContext() , message , Toast.LENGTH_LONG).show(); break;
            case SUCCESS:  Toasty.success(Application.getContext() , message , Toast.LENGTH_LONG).show(); break;
            case NORMAL:   Toasty.normal(Application.getContext()  , message , Toast.LENGTH_LONG).show(); break;
            case INFO:     Toasty.info(Application.getContext()    , message , Toast.LENGTH_LONG).show(); break;

        }
    }



    public static RequestParams getRequestParams(){
        RequestParams params = new RequestParams();
        params.put(ROUTER.SESSION , spref.getOurInstance().getString(ROUTER.SESSION , ""));
        params.put(ROUTER.USER_ID , spref.getOurInstance().getInt(ROUTER.USER_ID , -1) + "");

        return params;
    }

    public static void animateError(View view){
        YoYo.with(Techniques.Wobble).duration(900).playOn(view);
    }


    public static File Uri2File(Uri uri){

        String [] items = {MediaStore.Images.Media.DATA} ;
        Cursor cursor = Application.getContext().getContentResolver().query(uri , items ,null , null ,null);

        if (cursor == null){
            return null;
        }
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        String s = cursor.getString(column_index);
        cursor.close();
        return new File(s);


    }


    public static void changeLikeState(int position , PostObject object , SocialLikeChangeListener listener) {

        int newState = object.getPost_id();

        RequestParams params = app.getRequestParams();
        params.put(ROUTER.ROUTE, ROUTER.ROUTE_POSTS);
        if (newState == 0) {

            params.put(ROUTER.ACTION, ROUTER.ACTION_LIKE);
            params.put(ROUTER.USER_ID, spref.getOurInstance().getInt("USER_ID", -1));
            params.put(ROUTER.INPUT_POST_ID, object.getId());

            SocialClient.post(params, new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {

                    String response = new String(responseBody);

                        listener.onChange(position , object.getId(), 1, true);


                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        if (jsonObject.has("Error")) {
                            app.t(jsonObject.getString("Error"), ToastType.ERROR);
                            listener.onChange(position, object.getId(), 0, false);

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
                    listener.onStart();
                    super.onStart();
                }

                @Override
                public void onFinish() {
                    listener.onFinish();
                    super.onFinish();
                }
            });
        } else {

        params.put(ROUTER.ACTION, ROUTER.ACTION_UNLIKE);
        params.put(ROUTER.USER_ID, spref.getOurInstance().getInt("USER_ID", -1));
        params.put(ROUTER.INPUT_POST_ID, object.getId());

        SocialClient.post(params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {

                String response = new String(responseBody);

                    listener.onChange(position, object.getId(), 0, true);


                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.has("error")) {
                        app.t(jsonObject.getString("error"), ToastType.ERROR);
                        listener.onChange(position, object.getId(), 1, false);
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
                listener.onStart();
                super.onStart();
            }

            @Override
            public void onFinish() {
                listener.onFinish();
                super.onFinish();
            }
        });
    }

    }
}
