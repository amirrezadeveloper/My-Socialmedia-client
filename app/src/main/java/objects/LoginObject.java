package objects;

public class LoginObject {

    private String state ;
    private int id ;
    private String session ;
    private UserObject UserObject ;
    private ProfileObject ProfileObject ;


    public String getState() {
        return state;
    }

    public int getId() {
        return id;
    }

    public String getSession() {
        return session;
    }

    public UserObject getUserObject() {
        return UserObject;
    }

    public ProfileObject getProfileObject() {
        return ProfileObject;
    }
}
