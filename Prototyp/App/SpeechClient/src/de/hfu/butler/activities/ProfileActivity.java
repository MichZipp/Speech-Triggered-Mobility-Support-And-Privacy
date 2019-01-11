package de.hfu.butler.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import de.hfu.butler.MainActivity;
import de.hfu.butler.R;

public class ProfileActivity extends AppCompatActivity {
    private final String LOG_TAG = "ProfileActivity";
    private final String profileUpdateUrl = "";
    private RequestQueue requestQueue;

    private EditText editFirstname;
    private EditText editLastname;
    private EditText editLocation;
    private Button btnUpdateProfile;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        editFirstname = (EditText) findViewById(R.id.editTextFirstname);
        editLastname = (EditText) findViewById(R.id.editTextLastname);
        editLocation = (EditText) findViewById(R.id.editTextLocation);
        btnUpdateProfile = (Button) findViewById(R.id.buttonUpdateProfile);

        btnUpdateProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doProfileUpdate();
            }
        });

        requestQueue = Volley.newRequestQueue(this);
    }

    private void doProfileUpdate() {
        String firstname = editFirstname.getText().toString();
        String lastname = editLastname.getText().toString();
        String location = editLocation.getText().toString();

        JSONObject profileJson = new JSONObject();
        try {
            profileJson.put("email", firstname);
            profileJson.put("password", lastname);
            profileJson.put("password", location);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest signInRequest = new JsonObjectRequest(Request.Method.POST, profileUpdateUrl, profileJson,
                new com.android.volley.Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.i(LOG_TAG, "RESPONSE: " + response.toString());
                        String userID, token;
                        Log.i(LOG_TAG, "RESPONSE: " + response.toString());
                        Intent i = new Intent(getApplicationContext(), MainActivity.class);
                        getApplicationContext().startActivity(i);
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
}

