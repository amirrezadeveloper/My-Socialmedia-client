package fragments;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.fragment.app.Fragment;
import com.example.noxsocial.R;
import com.github.ybq.android.spinkit.SpinKitView;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.text.SimpleDateFormat;
import java.util.Date;
import app.app;
import app.spref;
import static android.app.Activity.RESULT_OK;
import app.*;
import cz.msebera.android.httpclient.Header;

public class AddFragment extends Fragment implements View.OnClickListener {

    AppCompatImageView imageView, submit , plus;
    AppCompatEditText caption ;
    SpinKitView progressBar ;

    public static final int CAMERA_REQUEST_CODE = 102 ;
    public static final int GALLERY_REQUEST_CODE = 103 ;
    public static  File imageURI ;
    public AddFragment(){

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add , container , false );
        return init(view) ;
    }

    private View init(View view){


        imageView = view.findViewById(R.id.image);
        plus   = view.findViewById(R.id.plus);
        submit = view.findViewById(R.id.submit);
        caption = view.findViewById(R.id.caption) ;
        progressBar = view.findViewById(R.id.progressBar);


        plus.setOnClickListener(this);
        submit.setOnClickListener(this);


        return view ;
    }

    @Override
    public void onClick(View v) {

        if (v == plus){
        AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
        alert.setTitle(R.string.photoTitle);
        alert.setMessage(R.string.photoMessage);
        alert.setPositiveButton(R.string.gallery, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {


                chooseFromGallery();

            }
        });

        alert.setNeutralButton(R.string.capture, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {


                captureImageStart();
            }
        });


        alert.show();}

        else if (v == submit){

            uploadToServer();
        }

    }




    private File getMediaFile(){

        File storageDirectory = new File(
                Environment.getExternalStoragePublicDirectory(
                        Environment.DIRECTORY_PICTURES) , "UploadPhoto");


        if (!storageDirectory.exists()){
            if(!storageDirectory.mkdirs()){
                return null;
            }
        }

        String fileName = "IMG_" + new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String image2CaptureURL = storageDirectory.getPath()  + File.separator + fileName + ".jpg";

        spref.getOurInstance().edit().putString("image2CaptureURL" , image2CaptureURL).apply();



        File finalFile = new File(image2CaptureURL);


        return finalFile;
    }

    private void captureImageStart(){

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        Uri imageCapture = Uri.fromFile(getMediaFile());

        intent.putExtra(MediaStore.EXTRA_OUTPUT , imageCapture);
        startActivityForResult(intent , CAMERA_REQUEST_CODE);

    }

    private void chooseFromGallery(){

        Intent gallery = new Intent(Intent.ACTION_PICK);
        gallery.setType("image/*");
        startActivityForResult(gallery , GALLERY_REQUEST_CODE);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        super.onActivityResult(requestCode, resultCode, data);


        if (resultCode == RESULT_OK) {

            if (requestCode == CAMERA_REQUEST_CODE) {



               showImage( new File(spref.getOurInstance().getString("image2CaptureURL" , "")));

            }

            else if (requestCode == GALLERY_REQUEST_CODE) {

                Uri image = data.getData();

                showImage( app.Uri2File(image));

            }


        } else {

            app.t("Something is wrong!" , ToastType.ERROR);
        }
    }



    private void showImage(File image2Act) {

        imageURI = image2Act ;
        imageView.setImageURI(Uri.fromFile(image2Act));
        plus.setVisibility(View.GONE);

    }

    private void uploadToServer(){

        RequestParams params = new RequestParams();
        params.put(ROUTER.ROUTE , ROUTER.ROUTE_POSTS);
        params.put(ROUTER.ACTION , ROUTER.ACTION_ADD);
        params.put(ROUTER.USER_ID , spref.getOurInstance().getInt("USER_ID" , -1));
        params.put(ROUTER.INPUT_CAPTION , caption.getText().toString());
        params.put(ROUTER.SESSION , spref.getOurInstance().getString("SESSION" , ""));

        try {
            params.put(ROUTER.INPUT_IMAGE , imageURI);
        } catch (FileNotFoundException e) {
            app.l(e.toString());
        }


        SocialClient.post(params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {


                String response = new String(responseBody) ;

                try {
                    JSONObject jsonObject = new JSONObject(response);
                    app.l(jsonObject+"");

                    if (jsonObject.has("error")){

                        app.t(jsonObject.getString("error") , ToastType.ERROR);

                    }else {
                        app.t(jsonObject.getString("status") , ToastType.SUCCESS);
                        caption.setText("");
                        imageView.setImageResource(0);
                        plus.setVisibility(View.VISIBLE);

                    }

                }catch (JSONException e){

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

    }


