package cl.ninxapps.dydi_proto;

import android.content.Context;
import android.util.Log;
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
import java.util.concurrent.Callable;

/**
 * Created by jose on 26/3/16.
 */
public class Networking {
    private String api;
    private String uid;
    private String client;
    private String token;

    public Networking(){
        api = "http://192.168.0.2:3000/api/v1";
    }

    public void get(final Context context, String resource, final Callable failure, final Callable success) {
        Fuel.get(api+resource).responseString(new Handler<String>() {
            @Override
            public void failure(Request request, Response response, FuelError error) {
                try {
                    failure.call();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void success(Request request, Response response, String data) {
                try {
                    success.call();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void setHeaders(String uid, String client, String token) {
        this.client = client;
        this.token  = token;
        this.uid    = uid;

        Map<String, String> headers = new HashMap<String, String>();
        headers.put("uid",            uid);
        headers.put("client",      client);
        headers.put("access-token", token);

        Manager.Companion.getInstance().setBaseHeaders(headers);
    }
}
