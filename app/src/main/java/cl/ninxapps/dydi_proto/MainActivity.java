package cl.ninxapps.dydi_proto;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.kittinunf.fuel.Fuel;
import com.github.kittinunf.fuel.core.FuelError;
import com.github.kittinunf.fuel.core.Handler;
import com.github.kittinunf.fuel.core.Manager;
import com.github.kittinunf.fuel.core.Request;
import com.github.kittinunf.fuel.core.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import kotlin.Pair;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private Toolbar toolbar;
    private RecyclerView recList;
    private Networking net;
    private Map<String, String> headers;
    private LoginManager loginManager = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_side_bar);

        /*
        toolbar = (Toolbar) findViewById(R.id.tool_bar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
        }
        */

        recList = (RecyclerView) findViewById(R.id.cardList);
        recList.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recList.setLayoutManager(llm);

        /*ItemClickSupport.addTo(recList).setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
            @Override
            public void onItemClicked(RecyclerView recyclerView, int position, View v) {
                // do it

                FrameLayout options = (FrameLayout)v.findViewById(R.id.options);
                FrameLayout results = (FrameLayout)v.findViewById(R.id.results);
                ImageView answer = (ImageView)v.findViewById(R.id.answer);

                ImageView gaugeView = (ImageView)v.findViewById(R.id.circle);
                TextView percentView = (TextView)v.findViewById(R.id.percent);

                Random ran = new Random();
                int votes = ran.nextInt(100);

                percentView.setText(votes + "%");

                Integer color, answerColor;


                if (votes%2 == 0) {
                    answerColor = Color.rgb(255, 0, 0);
                } else {
                    answerColor = Color.rgb(0, 255, 0);
                }

                if (votes < 50) {
                    color = Color.rgb(255, 255/50*(votes), 0);
                } else {
                    color = Color.rgb(255-(255/50*(votes-50)), 255, 0);
                }

                answer.setBackgroundColor(answerColor);

                gaugeView.setColorFilter(color);
                gaugeView.setRotation(0);


                options.setVisibility(View.GONE);
                results.setVisibility(View.VISIBLE);
                answer.setVisibility(View.VISIBLE);

                gaugeView.animate().rotationBy(votes*-180/100).start();

                Log.d("CLICK", "position: " + position);

            }
        });*/

//        QuestionAdapter qa = new QuestionAdapter(getList(30), this, recList);
//        recList.setAdapter(qa);

        //Login with the previous information.
        SharedPreferences settings = getApplicationContext().getSharedPreferences(GlobalConstants.PREFS_NAME, 0);
        String email = settings.getString("email", null);
        String password = settings.getString("password", null);
        if(email == null || password == null){
            onLoginFailed();
        }
        else
        {
            doLogin(email, password, getApplicationContext());
        }


        //Initialize headers
        headers = new HashMap<String, String>();
        getQuestions(this);

        //Sidebar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private List<Question> getList(int size) {

        List<Question> result = new ArrayList<Question>();
        String[] qs = {
                "Do you piss in the shower?",
                "Have you ever picked your nose?",
                "Have you ever blamed someone else for your own fart and made fun of them?",
                "Have you ever been caught by your parents while doing it?",
        };
        Random rng = new Random();
        for (int i=1; i <= size; i++) {
            int random = rng.nextInt(4);
            Question ci = new Question();
            ci.text = qs[random];
            ci.comments = i;
            ci.yesCount = i;
            ci.noCount = i;
            ci.answered = false;
            ci.nsfw = random == 3;
            result.add(ci);
        }

        return result;
    }

    public static String generateString(Random rng, String characters, int length)
    {
        char[] text = new char[length];
        for (int i = 0; i < length; i++)
        {
            text[i] = characters.charAt(rng.nextInt(characters.length()));
        }
        return new String(text);
    }

    public void onLoginFailed(){
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }

    public void onLoginSuccess(){
        SharedPreferences settings = getApplicationContext().getSharedPreferences(GlobalConstants.PREFS_NAME, 0);
        TextView logInStatus = (TextView)findViewById(R.id.logInStatus);


        logInStatus.setText(settings.getString("name", "Anonymous"));
    }

    public void doLogin(final String email, final String password, final Context context){
        List<Pair<String, String>> params = new ArrayList<Pair<String, String>>() {{
            add(new Pair<>("email", email));
            add(new Pair<>("password", password));
        }};

        boolean success = false;
        Fuel.post(GlobalConstants.API+"/auth/sign_in", params).responseString(new Handler<String>() {
            @Override
            public void failure(Request request, Response response, FuelError error) {
                //do something when it is failure
                Log.e("FUEL", error.toString());
                onLoginFailed();
            }

            @Override
            public void success(Request request, Response response, String data) {
                Log.e("FUEL", data.toString());
                //do something when it is successful
                SharedPreferences settings = context.getSharedPreferences(GlobalConstants.PREFS_NAME, 0);

                Map<String, List<String>> allHeaders = response.getHttpResponseHeaders();

                //Create JSON
                try {
                    JSONObject jObject = new JSONObject(data);

                    //Store information in shared preferences
                    SharedPreferences.Editor editor = settings.edit();
                    editor.putString("email", email);
                    editor.putString("password", password);

                    jObject = jObject.getJSONObject("data");
                    editor.putString("name", jObject.getString("name"));
                    editor.commit();
                }
                catch(JSONException e){
                    Log.e("FUEL", e.toString());
                    onLoginFailed();
                    return;
                }



                //Make the headers for future http requests
                Map<String, String> headers = new HashMap<String, String>();
                headers.put("uid", allHeaders.get("uid").get(0));
                headers.put("client",allHeaders.get("client").get(0) );
                headers.put("access-token", allHeaders.get("access-token").get(0));
                Manager.Companion.getInstance().setBaseHeaders(headers);
                onLoginSuccess();
            }
        });
    }

    private void getQuestions(Context context){
        Fuel.get(GlobalConstants.API+"/questions.json").responseString(new Handler<String>() {
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

                    List<Question> result = new ArrayList<Question>();

                    JSONArray questions = new JSONArray(data);

                    for (int i = 0; i < questions.length(); i++) {
                        Question ci = new Question();
                        ci.id = questions.getJSONObject(i).getInt("id");
                        ci.text = questions.getJSONObject(i).getString("text");
                        ci.nsfw = questions.getJSONObject(i).getBoolean("nsfw");
                        ci.answered = questions.getJSONObject(i).getBoolean("answered");
                        ci.yesCount = questions.getJSONObject(i).getInt("yes_count");
                        ci.noCount = questions.getJSONObject(i).getInt("no_count");
                        ci.category = questions.getJSONObject(i).getString("category");

                        Log.i("JSON id", questions.getJSONObject(i).getInt("id")+"");
                        Log.i("JSON ci.id", ci.id+"");
                        result.add(ci);
                    }

                    QuestionAdapter qa = new QuestionAdapter(result, context, recList);
                    recList.setAdapter(qa);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_ask) {
            Intent intent = new Intent(this, AskQuestionActivity.class);
            startActivity(intent);

        } else if (id == R.id.nav_asked_questions) {

        } else if (id == R.id.nav_rated_questions) {

        } else if (id == R.id.nav_settings) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
