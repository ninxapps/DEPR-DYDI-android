package cl.ninxapps.dydi_proto;

import android.app.Application;
import android.content.SharedPreferences;

import com.github.kittinunf.fuel.core.Manager;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Felipe on 06-07-2016.
 * Wrapper for non-constants variables
 */
public class MainApp extends Application {

    //The user!
    private UserModel user;


    public MainApp(){
        user = null;
    }

    //When a user successfully logs in
    public void onUserDataLoaded(String provider, String uid, String name, String email, String access_token, String client){
        user = new UserModel(provider, uid, name, email);
        user.setAccessToken(access_token);
        user.setClient(client);

        //Set headers forever!
        Map<String, String> headers = new HashMap<String, String>();
        headers.put("uid", uid);
        headers.put("client", client);
        headers.put("access-token", access_token);
        Manager.Companion.getInstance().setBaseHeaders(headers);

        //Save all this information
        SharedPreferences settings = getApplicationContext().getSharedPreferences(GlobalConstants.PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString("uid", uid);
        editor.putString("provider", provider);
        editor.putString("email", email);
        editor.putString("name", name);
        editor.putString("access_token", access_token);
        editor.putString("client", client);
        editor.commit();

    }

    public String getAccessToken(){
        if(user == null)
            return null;
        return user.getAccessToken();
    }

    public String getUID(){
        if(user == null)
            return null;
        return user.getUID();
    }

    public String getClient(){
        if(user == null)
            return null;
        return user.getClient();
    }


}
