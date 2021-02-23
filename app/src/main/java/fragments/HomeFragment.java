package fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.noxsocial.LoginActivity;
import com.example.noxsocial.R;
import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import adapter.SocialAdapter;
import cz.msebera.android.httpclient.Header;
import objects.LoginObject;
import objects.PostObject;
import app.*;

public class HomeFragment extends Fragment {

    int start = 0 ;
    RecyclerView recyclerView ;
    List<PostObject> list ;
    SocialAdapter adapter ;
    ConstraintLayout parent ;
    AppCompatTextView noPost ;
    PostObject postObject;

    public HomeFragment(){

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_home , container , false );
        return init(view) ;

    }

    private View init (View view){

        parent = view.findViewById(R.id.parent);
        recyclerView = view.findViewById(R.id.recyclerView) ;
        noPost = view.findViewById(R.id.noPost) ;

        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        list = new ArrayList<>();
        adapter = new SocialAdapter(getActivity() , list) ;
        recyclerView.setAdapter(adapter);

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

                                spref.getOurInstance().edit().clear().apply();
                                Objects.requireNonNull(getActivity()).finish();
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
