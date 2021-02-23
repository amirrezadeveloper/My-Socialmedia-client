package objects;

import java.io.Serializable;

public class PostObject implements Serializable {

    private int id ;
    private int user_id ;
    private String image ;
    private String caption ;
    private String date ;
    private int post_id;
    private UserObject UserObject ;
    public PostObject (){

    }

    public PostObject(int id, int user_id, String image, String caption, String date , int post_id , int like) {
        this.id = id;
        this.user_id = user_id;
        this.image = image;
        this.caption = caption;
        this.date = date;
        this.post_id = post_id ;
    }

    public int getId() {
        return id;
    }

    public int getUser_id() {
        return user_id;
    }

    public String getImage() {
        return image;
    }

    public String getCaption() {
        return caption;
    }

    public String getDate() {
        return date;
    }

    public int getPost_id() {
        return post_id;
    }

    public void setPost_id(int post_id) {
        this.post_id = post_id;
    }


    public UserObject getUserObject() {
        return UserObject;
    }
}
