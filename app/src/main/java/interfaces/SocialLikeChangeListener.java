package interfaces;

public interface SocialLikeChangeListener {

    void onChange(int position , int postID , int like , Boolean success);
    void onStart();
    void onFinish();
}
