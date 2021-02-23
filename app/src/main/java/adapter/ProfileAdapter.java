package adapter;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.noxsocial.EditPost;
import com.example.noxsocial.R;
import java.util.List;
import app.app;
import de.hdodenhof.circleimageview.CircleImageView;
import objects.PostObject;
import app.* ;


public class ProfileAdapter extends RecyclerView.Adapter<ProfileAdapter.SocialViewHolder> {

    Activity activity ;
    List<PostObject>   list ;

    public ProfileAdapter(Activity activity , List<PostObject> list){
        this.activity = activity ;
        this.list = list ;
    }




    @NonNull
    @Override
    public SocialViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(activity).inflate(R.layout.layout_profile_row , parent , false) ;
        return new SocialViewHolder(view) ;
    }

    @Override
    public void onBindViewHolder(@NonNull SocialViewHolder holder, int position) {


        Glide.with(Application.getContext()).load(app.main.URL + list.get(position).getImage()).into(holder.image);


    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class SocialViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        AppCompatImageView image ;
        ConstraintLayout parent ;

        public SocialViewHolder(@NonNull View itemView) {
            super(itemView);


            image = itemView.findViewById(R.id.image) ;
            parent = itemView.findViewById(R.id.parent);
            parent.setOnClickListener(this);


        }

        @Override
        public void onClick(View v) {
            Intent intent = new Intent(activity , EditPost.class);
            intent.putExtra("object" , list.get(getAdapterPosition()));
            activity.startActivity(intent);
            notifyDataSetChanged();
        }
    }
}
