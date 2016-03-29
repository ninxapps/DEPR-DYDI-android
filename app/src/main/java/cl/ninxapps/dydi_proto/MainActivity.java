package cl.ninxapps.dydi_proto;

import android.content.Context;
import android.graphics.Color;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import kotlin.Pair;

public class MainActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private RecyclerView recList;
    private Networking net;
    private Map<String, String> headers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = (Toolbar) findViewById(R.id.tool_bar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
        }

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

        QuestionAdapter qa = new QuestionAdapter(getList(30));
        recList.setAdapter(qa);

//        headers = new HashMap<String, String>();
//        login();

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
        Random rng = new Random();
        for (int i=1; i <= size; i++) {
            Question ci = new Question();
            ci.text = generateString(rng, "AABCD EEFGH IIJKL MNOOP QRST UUVWX YZ ".toLowerCase(), rng.nextInt((100 - 20) + 1) + 20);
            ci.comments = i;
            ci.yesCount = i;
            ci.noCount = i;
            ci.answered = false;
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

    public void login(){

        List<Pair<String, String>> params = new ArrayList<Pair<String, String>>() {{
            add(new Pair<>("email", "n3ggro@gmail.com"));
            add(new Pair<>("password", "11111111"));
        }};

        Fuel.post("http://192.168.0.2:3000/auth/sign_in", params).responseString(new Handler<String>() {
            @Override
            public void failure(Request request, Response response, FuelError error) {
                //do something when it is failure
                Context context = getApplicationContext();
                CharSequence text = "Login Faaaaaail: " + error.toString();
                Log.e("FUEL", error.toString());
                int duration = Toast.LENGTH_LONG;

                Toast toast = Toast.makeText(context, text, duration);
                toast.show();
            }

            @Override
            public void success(Request request, Response response, String data) {
                //do something when it is successful
                Context context = getApplicationContext();
                int duration = Toast.LENGTH_LONG;

                Toast toast = Toast.makeText(context, data, duration);
                toast.show();

                Map<String, List<String>> allHeaders = response.getHttpResponseHeaders();

                headers.put("uid", allHeaders.get("uid").get(0));
                headers.put("client",allHeaders.get("client").get(0) );
                headers.put("access-token", allHeaders.get("access-token").get(0));

                Manager.Companion.getInstance().setBaseHeaders(headers);

                Toast toast2 = Toast.makeText(context, headers.toString(), duration);
                toast.show();


                Log.i("FUEL", data);
                Log.i("FUEL", headers.toString());

                getQuestions();
            }
        });
    }

    private void getQuestions(){
        Fuel.get("http://192.168.0.2:3000/api/v1/categories").responseString(new Handler<String>() {
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
                int duration = Toast.LENGTH_LONG;

                Toast toast = Toast.makeText(context, text, duration);
                toast.show();


                try {

                    List<Question> result = new ArrayList<Question>();

                    JSONArray questions = new JSONArray(data);

                    for (int i = 0; i < questions.length(); i++) {
                        Question ci = new Question();
                        ci.text = questions.getJSONObject(i).getString("text");
                        result.add(ci);
                    }

                    QuestionAdapter qa = new QuestionAdapter(result);
                    recList.setAdapter(qa);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
}