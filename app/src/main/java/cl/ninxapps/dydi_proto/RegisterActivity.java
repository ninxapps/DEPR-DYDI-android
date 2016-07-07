package cl.ninxapps.dydi_proto;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.github.kittinunf.fuel.Fuel;
import com.github.kittinunf.fuel.core.FuelError;
import com.github.kittinunf.fuel.core.Handler;
import com.github.kittinunf.fuel.core.Manager;
import com.github.kittinunf.fuel.core.Request;
import com.github.kittinunf.fuel.core.Response;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import kotlin.Pair;

public class RegisterActivity extends Activity {
    private static final String TAG = "SignupActivity";

    @BindView(R.id.input_name) EditText _nameText;
    @BindView(R.id.input_email) EditText _emailText;
    @BindView(R.id.input_password) EditText _passwordText;
    @BindView(R.id.btn_signup) Button _signupButton;
    @BindView(R.id.link_login) TextView _loginLink;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        ButterKnife.bind(this);

        _signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signup();
            }
        });

        _loginLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Finish the registration screen and return to the Login activity
                finish();
            }
        });
    }

    public void signup() {
        Log.d(TAG, "Signup");

        if (!validate()) {
            onSignupFailed();
            return;
        }

        _signupButton.setEnabled(false);

        final ProgressDialog progressDialog = new ProgressDialog(RegisterActivity.this,
                R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Creating Account...");
        progressDialog.show();

        final String name = _nameText.getText().toString();
        final String email = _emailText.getText().toString();
        final String password = _passwordText.getText().toString();

        final MainApp app = (MainApp)getApplication();
        List<Pair<String, String>> params = new ArrayList<Pair<String, String>>() {{
            add(new Pair<>("name", name));
            add(new Pair<>("email", email));
            add(new Pair<>("password", password));
        }};

        boolean success = false;
        Fuel.post(GlobalConstants.API+"/auth/sign_in", params).responseString(new Handler<String>() {
            @Override
            public void failure(Request request, Response response, FuelError error) {
                onSignupFailed();
            }

            @Override
            public void success(Request request, Response response, String data) {
                Log.e("FUEL", data.toString());
                Map<String, List<String>> allHeaders = response.getHttpResponseHeaders();


                try {
                    String user_email = email;
                    String access_token = allHeaders.get("access-token").get(0);
                    String client = allHeaders.get("client").get(0);

                    //Parse JSON
                    JSONObject jObject = new JSONObject(data);
                    jObject = jObject.getJSONObject("data");

                    //Get data
                    String name = jObject.getString("name");
                    String uid = jObject.getString("uid");
                    String provider = jObject.getString("provider");

                    app.onUserDataLoaded(provider, uid, name, email, access_token, client);
                }
                catch(JSONException e){
                    Log.e("FUEL", e.toString());
                    onSignupFailed();
                    return;
                }


                //Make the headers for future http requests
                Map<String, String> headers = new HashMap<String, String>();
                headers.put("uid",  app.getUID());
                headers.put("client", app.getClient());
                headers.put("access-token", app.getAccessToken());
                Manager.Companion.getInstance().setBaseHeaders(headers);
                onSignupSuccess();
            }
        });
    }


    public void onSignupSuccess() {
        _signupButton.setEnabled(true);
        setResult(RESULT_OK, null);
        finish();
    }

    public void onSignupFailed() {
        Toast.makeText(getBaseContext(), "Login failed", Toast.LENGTH_LONG).show();

        _signupButton.setEnabled(true);
    }

    public boolean validate() {
        boolean valid = true;

        String name = _nameText.getText().toString();
        String email = _emailText.getText().toString();
        String password = _passwordText.getText().toString();

        if (name.isEmpty() || name.length() < 3) {
            _nameText.setError("at least 3 characters");
            valid = false;
        } else {
            _nameText.setError(null);
        }

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _emailText.setError("enter a valid email address");
            valid = false;
        } else {
            _emailText.setError(null);
        }

        if (password.isEmpty() || password.length() < 4 || password.length() > 10) {
            _passwordText.setError("between 4 and 10 alphanumeric characters");
            valid = false;
        } else {
            _passwordText.setError(null);
        }

        return valid;
    }
}
