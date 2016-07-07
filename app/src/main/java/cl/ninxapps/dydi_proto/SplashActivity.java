package cl.ninxapps.dydi_proto;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebView;
import android.widget.Toast;

import com.github.kittinunf.fuel.Fuel;
import com.github.kittinunf.fuel.core.FuelError;
import com.github.kittinunf.fuel.core.Handler;
import com.github.kittinunf.fuel.core.Manager;
import com.github.kittinunf.fuel.core.Request;
import com.github.kittinunf.fuel.core.Response;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SplashActivity extends AppCompatActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        WebView wv = (WebView) findViewById(R.id.loadingWebView);

        //Loading gif!
        wv.loadUrl("file:///android_asset/loading.gif");

        //Open shared preferences
        SharedPreferences settings = getApplicationContext().getSharedPreferences(GlobalConstants.PREFS_NAME, 0);

        //Lets find stored information
        String access_token = settings.getString("access_token", null);
        String provider = settings.getString("provider", null);
        String uid = settings.getString("uid", null);
        String email = settings.getString("email", null);
        String name = settings.getString("name", null);
        String client = settings.getString("client", null);

        //I dont have a token
        if(access_token == null)
        {
            onUserNotLoggedIn();
        }

        //I do, but I have to test it
        else {
            ((MainApp) getApplication()).onUserDataLoaded(provider, uid, name, email, access_token, client);
            testAccessToken();
        }
    }

    public void onUserLoggedIn(){
        Intent i = new Intent(SplashActivity.this, MainActivity.class);
        startActivity(i);
        finish();
    }

    public void onUserNotLoggedIn(){
        Intent i = new Intent(SplashActivity.this, LoginActivity.class);
        startActivity(i);
        finish();
    }

    public void testAccessToken(){
        final MainApp app = (MainApp)getApplication();
        Map<String, String> headers = new HashMap<String, String>();
        headers.put("uid",  app.getUID());
        headers.put("client", app.getClient());
        headers.put("access-token", app.getAccessToken());
        Manager.Companion.getInstance().setBaseHeaders(headers);
        Fuel.get(GlobalConstants.API+"/auth/validate_token").responseString(new Handler<String>() {
            @Override
            public void failure(Request request, Response response, FuelError error) {
                Log.e("server", error.toString());
                onUserNotLoggedIn();
            }

            @Override
            public void success(Request request, Response response, String data) {
                onUserLoggedIn();

                Map<String, String> headers = new HashMap<String, String>();
                headers.put("uid",  app.getUID());
                headers.put("client", app.getClient());
                headers.put("access-token", app.getAccessToken());
                Manager.Companion.getInstance().setBaseHeaders(headers);
            }
        });
    }
}
