package de.hfu.butler.activities;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import de.hfu.butler.MainActivity;
import de.hfu.butler.R;
import de.hfu.butler.service.SessionStorage;

public class SignInActivity extends Activity {
    private final String LOG_TAG = "SignInActivity";
    private final String loginUrl = "http://192.52.32.250:3000/api/customers/login";
    private final String profileUrl = "http://192.52.32.250:3000/api/customers/";
    private RequestQueue requestQueue;
    private SessionStorage storage;

    private EditText editEmail;
    private EditText editPassword;
    private Button btnSignIn;
    private Button btnSignUp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_singin);

        storage = SessionStorage.getInstance();

        ActionBar bar = getActionBar();
        bar.setBackgroundDrawable(new ColorDrawable(Color.BLACK));

        editEmail = (EditText) findViewById(R.id.editEmail);
        editPassword = (EditText) findViewById(R.id.editPassword);
        btnSignIn = (Button) findViewById(R.id.btnSignIn);
        btnSignUp = (Button) findViewById(R.id.btnSignUp);

        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = editEmail.getText().toString();
                String password = editPassword.getText().toString();
                if (validateLogin(email, password)) {
                    doLogin(email, password);
                }
            }
        });

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Open SignUpActivity
                Intent intent = new Intent(SignInActivity.this, SignUpActivity.class);
                getApplicationContext().startActivity(intent);
            }
        });

        requestQueue = Volley.newRequestQueue(this);
    }

    private boolean validateLogin(String email, String password) {
        if (email == null || email.trim().length() == 0) {
            Toast.makeText(getApplicationContext(), "Email is required!", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (password == null || password.trim().length() == 0) {
            Toast.makeText(getApplicationContext(), "Password is required!", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void doLogin(String email, String password) {
        JSONObject signInJson = new JSONObject();
        try {
            signInJson.put("email", email);
            signInJson.put("password", password);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        final JSONObject basicProfileJson = new JSONObject();
        try {
            basicProfileJson.put("vorname", "Max");
            basicProfileJson.put("name", "Mustermann");
            basicProfileJson.put("location", "Furtwangen im Schwarzwald");
            basicProfileJson.put("scalamobile", 0);
            basicProfileJson.put("pickup", 1);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest signInRequest = new JsonObjectRequest(Request.Method.POST, this.loginUrl, signInJson,
            new com.android.volley.Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    Log.i(LOG_TAG, "RESPONSE: " + response.toString());
                    String userID, token;

                    try {
                        userID = response.getString("userId");
                        token = response.getString("id");
                        storage.setUserId(userID);
                        storage.setSessionToken(token);
                        Log.i(LOG_TAG, "Userid: " + storage.getUserId());
                        requestQueue.add(new JsonObjectRequest(Request.Method.GET, getProfileCountUrl(),
                                new com.android.volley.Response.Listener<JSONObject>() {
                                    @Override
                                    public void onResponse(JSONObject response) {
                                        Log.i(LOG_TAG, "RESPONSE: " + response.toString());
                                        int count = 0;

                                        try {
                                            count = response.getInt("count");
                                            if(count == 0){
                                                requestQueue.add(new JsonObjectRequest(Request.Method.POST, getProfilePostUrl(), basicProfileJson,
                                                    new com.android.volley.Response.Listener<JSONObject>() {
                                                        @Override
                                                        public void onResponse(JSONObject response) {
                                                            Log.i(LOG_TAG, "RESPONSE: " + response.toString());
                                                            Intent i = new Intent(getApplicationContext(), MainActivity.class);
                                                            getApplicationContext().startActivity(i);
                                                            finish();
                                                        }
                                                    },
                                                    new com.android.volley.Response.ErrorListener() {
                                                        @Override
                                                        public void onErrorResponse(VolleyError error) {
                                                            Log.e(LOG_TAG,"ERROR: " + error.toString());
                                                        }
                                                    }));
                                            }else{
                                                Intent i = new Intent(getApplicationContext(), MainActivity.class);
                                                getApplicationContext().startActivity(i);
                                                finish();
                                            }
                                        } catch(JSONException e){
                                            Log.e(LOG_TAG, "JSONException: " + e.toString());                            ;
                                        }
                                    }
                                },
                                new com.android.volley.Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        Log.e(LOG_TAG,"ERROR: " + error.toString());
                                    }
                                }));
                    } catch(JSONException e){
                        Log.e(LOG_TAG, "JSONException: " + e.toString());
                    }
                }
            },
            new com.android.volley.Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e(LOG_TAG,"ERROR: " + error.toString());
                }
        });

        requestQueue.add(signInRequest);

    }

    private String getProfileCountUrl(){
        String url = profileUrl + storage.getUserId() + "/profiles/count?access_token=" + storage.getSessionToken();
        return url;
    }

    private String getProfilePostUrl(){
        String url = profileUrl + storage.getUserId() + "/profiles?access_token=" + storage.getSessionToken();
        return url;
    }
}