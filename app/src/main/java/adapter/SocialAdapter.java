package adapter;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;
import androidx.transition.FragmentTransitionSupport;

import com.bumptech.glide.Glide;
import com.example.noxsocial.R;
import com.example.noxsocial.comments;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import app.app;
import cz.msebera.android.httpclient.Header;
import de.hdodenhof.circleimageview.CircleImageView;
import fragments.ProfileFragment;
import interfaces.SocialLikeChangeListener;
import objects.PostObject;

import static android.content.Intent.getIntent;
import static app.Application.config;
import static app.Application.options;
import app.* ;


public class SocialAdapter extends RecyclerView.Adapter<SocialAdapter.SocialViewHolder> {

    Activity activity ;
    List<PostObject>   list ;


    public SocialAdapter(Activity activity , List<PostObject> list){
        this.activity = activity ;
        this.list = list ;
    }




    @NonNull
    @Override
    public SocialViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(activity).inflate(R.layout.layout_post_row , parent , false) ;
        return new SocialViewHolder(view) ;
    }

    @Override
    public void onBindViewHolder(@NonNull SocialViewHolder holder, int position) {

        Glide.with(Application.getContext()).load(app.main.URL + spref.getOurInstance().getString("PROFILE_IMAGE" , "")).into(holder.profile_image);


        holder.user_id.setText(spref.getOurInstance().getString("USERNAME" , ""));
        //holder.like_counter.setText(list.get(position).);
        holder.caption.setText(list.get(position).getCaption());


        Glide.with(Application.getContext()).load(app.main.URL + list.get(position).getImage()).into(holder.image);

        holder.like.setImageResource(
                list.get(position).getPost_id() == 0 ? R.drawable.ic_baseline_favorite_border_24 : R.drawable.ic_baseline_favorite_24
        );


    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class SocialViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        ConstraintLayout parent ;
        LinearLayout container ;
        CircleImageView profile_image ;
        AppCompatTextView user_id , like_counter , caption;
        AppCompatImageView image , like , comment ;



    public SocialViewHolder(@NonNull View itemView) {
        super(itemView);

        parent = itemView.findViewById(R.id.parent);
        container = itemView.findViewById(R.id.container) ;
        profile_image = itemView.findViewById(R.id.profile_image) ;
        user_id = itemView.findViewById(R.id.user_id) ;
        like_counter = itemView.findViewById(R.id.like_counter) ;
        caption = itemView.findViewById(R.id.caption) ;
        image = itemView.findViewById(R.id.image) ;
        like = itemView.findViewById(R.id.like) ;
        comment = itemView.findViewById(R.id.comment) ;


        like.setOnClickListener(this);
        comment.setOnClickListener(this);
        like_counter.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {

        if (v == like){

            likeFunction();

        }else if (v == comment) {

            commentFunction();

        }else if (v == like_counter) {

            likeCounterFunction();

        }

    }

        private void likeCounterFunction() {

        }


        private void likeFunction(){

            app.changeLikeState(0 , list.get(getAdapterPosition()), new SocialLikeChangeListener() {
                @Override
                public void onChange(int position, int postID, int like , Boolean success) {

                    PostObject postObject = list.get(getAdapterPosition());
                    if (success){

                        postObject.setPost_id(like);
                        list.set(getAdapterPosition() , postObject);
                        SocialViewHolder.this.like.setImageResource(
                                postObject.getPost_id()==0?R.drawable.ic_baseline_favorite_border_24 :R.drawable.ic_baseline_favorite_24
                        );

                    }

                }

                @Override
                public void onStart() {
                    //progressBar.setVisibility(View.VISIBLE);
                }

                @Override
                public void onFinish() {
                    // progressBar.setVisibility(View.INVISIBLE);
                }


            });


    }
        private void commentFunction() {

            Intent intent = new Intent(activity , comments.class);
            intent.putExtra("object" , list.get(getAdapterPosition()));
            activity.startActivity(intent);

    }

    }
}
