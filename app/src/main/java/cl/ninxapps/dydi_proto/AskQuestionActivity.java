package cl.ninxapps.dydi_proto;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.github.kittinunf.fuel.Fuel;
import com.github.kittinunf.fuel.core.FuelError;
import com.github.kittinunf.fuel.core.Handler;
import com.github.kittinunf.fuel.core.Request;
import com.github.kittinunf.fuel.core.Response;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;

import kotlin.Pair;

public class AskQuestionActivity extends AppCompatActivity {

    private EditText editText;
    private Spinner category;
    private CheckBox nsfw;
    private Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ask_question);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        editText = (EditText) findViewById(R.id.editText);
        category = (Spinner) findViewById(R.id.category_spinner);
        nsfw = (CheckBox) findViewById(R.id.checkBox);
        button = (Button) findViewById(R.id.button);

        button.setOnClickListener(new Button.OnClickListener() {

            @Override
            public void onClick(View v) {

                List<Pair<String, String>> params = new ArrayList<Pair<String, String>>() {{
                    add(new Pair<>("question[text]", editText.getText().toString()));
                    add(new Pair<>("question[nsfw]", nsfw.isChecked() ? "true" : "false"));
                    add(new Pair<>("question[category]", category.getSelectedItem().toString()));
                }};

                Fuel.post(GlobalConstants.API+"/questions", params).responseString(new Handler<String>() {
                    @Override
                    public void failure(Request request, Response response, FuelError error) {
                        //do something when it is failure
                        Context context = getApplicationContext();
                        CharSequence text = "Question Faaaaaail: " + error.toString();
                        Log.e("FUEL", error.toString());
                        int duration = Toast.LENGTH_LONG;

                        Toast toast = Toast.makeText(context, text, duration);
                        toast.show();
                    }

                    @Override
                    public void success(Request request, Response response, String data) {
                        //do something when it is successful
                        Context context = getApplicationContext();

                        Intent intent = new Intent(context, MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        finish();
                    }
                });

            }
        });

        getCategories(this);
    }

    private void getCategories(Context context) {
        Fuel.get(GlobalConstants.API + "/categories.json").responseString(new Handler<String>() {
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

                    List<String> spinnerArray = new ArrayList<String>();

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

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
    }
}
