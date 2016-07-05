package cl.ninxapps.dydi_proto;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.github.kittinunf.fuel.Fuel;
import com.github.kittinunf.fuel.core.FuelError;
import com.github.kittinunf.fuel.core.Handler;
import com.github.kittinunf.fuel.core.Request;
import com.github.kittinunf.fuel.core.Response;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;

public class AskQuestionActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ask_question);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getCategories(this);

    }

    private void getCategories(Context context){
        Fuel.get(GlobalConstants.API+"/categories.json").responseString(new Handler<String>() {
            @Override
            public void failure(Request request, Response response, FuelError error) {
                //do something when it is failure
                Context context = getApplicationContext();
                CharSequence text = "Faaaaaail: " + error.toString();
                Log.e("FUEL", error.toString());
                int duration = Toast.LENGTH_LONG;

                Toast toast = Toast.makeText(context, text, duration);
                toast.show();
            }

            @Override
            public void success(Request request, Response response, String data) {
                //do something when it is successful
                Context context = getApplicationContext();
                CharSequence text = data;

                try {

                    List<String> spinnerArray =  new ArrayList<String>();

                    JSONArray categories = new JSONArray(data);

                    for (int i = 0; i < categories.length(); i++) {
                        spinnerArray.add(categories.getJSONObject(i).getString("text"));
                    }

                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                            context, android.R.layout.simple_spinner_item, spinnerArray);

                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    Spinner sItems = (Spinner) findViewById(R.id.category_spinner);
                    sItems.setAdapter(adapter);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

}
