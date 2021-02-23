package fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.noxsocial.EditPost;
import com.example.noxsocial.EditProfileActivity;
import com.example.noxsocial.LoginActivity;
import com.example.noxsocial.R;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import adapter.ProfileAdapter;
import adapter.SocialAdapter;
import app.*;
import cz.msebera.android.httpclient.Header;
import objects.LoginObject;
import objects.PostObject;

public class ProfileFragment extends Fragment {

    CollapsingToolbarLayout title ;
    AppCompatImageView profile ;
    FloatingActionButton floatingActionButton ;

    int start = 0 ;
    RecyclerView recyclerView ;
    List<PostObject> list ;
    ProfileAdapter adapter ;
    LinearLayout parent ;
    AppCompatTextView noPost ;

    public ProfileFragment(){

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view =inflater.inflate(R.layout.fragment_profile , container , false) ;
        return init(view) ;
    }

    private View init(View view){


        profile   = view.findViewById(R.id. profile ) ;
        title     = view.findViewById(R.id. title  ) ;

        parent = view.findViewById(R.id.parent);
        recyclerView = view.findViewById(R.id.recyclerView) ;
        noPost = view.findViewById(R.id.noPost) ;

        recyclerView.setLayoutManager(new GridLayoutManager(getActivity() , 4));
        list = new ArrayList<>();
        adapter = new ProfileAdapter(getActivity() , list) ;
        recyclerView.setAdapter(adapter);

        floatingActionButton = view.findViewById(R.id.floatingActionButton) ;
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity() , EditProfileActivity.class);
                startActivity(intent);
            }
        });


        title.setTitle(spref.getOurInstance().getString(ROUTER.INPUT_USERNAME , ""));
        Glide.with(Application.getContext()).load(app.main.URL + spref.getOurInstance().getString("PROFILE_IMAGE" , "")).into(profile);

        getData();

        return view ;
    }

    private void getData() {

        RequestParams params = app.getRequestParams() ;
        params.put(ROUTER.ROUTE , ROUTER.ROUTE_POSTS);
        params.put(ROUTER.ACTION , ROUTER.ACTION_READ);
        params.put(ROUTER.USER_ID , spref.getOurInstance().getInt("USER_ID" , -1));
        params.put(ROUTER.INPUT_START , start);

        SocialClient.post(params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {

                String response = new String(responseBody) ;
                app.l(response);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.has("error")){
                        app.t(jsonObject.getString("error") , ToastType.ERROR);
                    }
                    else {
                        LoginObject loginObject = new Gson().fromJson(response,LoginObject.class);
                        if (loginObject.getState().equals(ROUTER.SUCCESS)){

                            String name = loginObject.getUserObject().getNAME()  ;
                            app.t( "Welcome " + name, ToastType.SUCCESS);

                            if (jsonObject.has("action") && jsonObject.getString("action").equals("logout")){

                                spref.getOurInstance().edit().clear();
                                getActivity().finish();
                                getActivity().startActivity(new Intent(getActivity() , LoginActivity.class));
                            }
                        }else {

                            app.t("Something Went Wrong" , ToastType.ERROR);
                        }
                    }

                }catch (JSONException e){

                    PostObject [] object = new Gson().fromJson(response , PostObject[].class);
                    list.addAll(Arrays.asList(object));
                    noPost.setVisibility(list.size()==0?View.VISIBLE:View.GONE);
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

            }
        });

    }
}
