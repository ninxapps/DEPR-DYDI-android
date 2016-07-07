package cl.ninxapps.dydi_proto;

/**
 * Created by Felipe on 06-07-2016.
 */
public class UserModel {

    private String provider;
    private String uid;
    private String name;
    private String email;
    private String access_token;
    private String client;

    public UserModel(String provider, String uid, String name, String email){
        this.provider = provider;
        this.uid = uid;
        this.name = name;
        this.email = email;
    }

    //access_token
    public void setAccessToken(String access_token){
        this.access_token = access_token;
    }

    public String getAccessToken(){
        return this.access_token;
    }

    //client
    public void setClient(String client){
        this.client = client;
    }

    public String getClient(){
        return this.client;
    }

    public String getUID(){
        return this.uid;
    }
}
