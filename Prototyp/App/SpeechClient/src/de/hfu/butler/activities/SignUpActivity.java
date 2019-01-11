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
import android.widget.RadioGroup;
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

public class SignUpActivity extends Activity {
    private final String LOG_TAG = "SignUpActivity";
    private final String userUrl = "http://192.52.32.250:3000/api/customers";

    private EditText editEmail;
    private EditText editPassword;
    private EditText confirmPassword;
    private Button btnSignIn;
    private Button btnSignUp;
    private RadioGroup radioGroupProfileType;
    private RequestQueue requestQueue;
    private SessionStorage storage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        ActionBar bar = getActionBar();
        bar.setBackgroundDrawable(new ColorDrawable(Color.BLACK));

        editEmail = (EditText) findViewById(R.id.editEmail);
        editPassword = (EditText) findViewById(R.id.editPassword);
        confirmPassword = (EditText) findViewById(R.id.editConfirmPassword);
        btnSignUp = (Button) findViewById(R.id.btnSignUp);
        btnSignIn = (Button) findViewById(R.id.btnSignIn);
        radioGroupProfileType = (RadioGroup) findViewById(R.id.radioGroupProfileType);

        storage = SessionStorage.getInstance();

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = editEmail.getText().toString();
                String password = editPassword.getText().toString();
                String password2 = confirmPassword.getText().toString();
                if (validateRegister(email, password, password2)) {
                    doRegister(email, password);
                }
            }
        });

        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Open SignInActivity
                Intent intent = new Intent(SignUpActivity.this, SignInActivity.class);
                getApplicationContext().startActivity(intent);
            }
        });

        radioGroupProfileType.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                Log.i(LOG_TAG, String.valueOf(checkedId));
                switch(checkedId) {
                    case R.id.radioButtonPrivat:
                            storage.setProfileType(0);
                            break;
                    case R.id.radioButtonDoctor:
                            storage.setProfileType(1);
                            break;
                    case R.id.radioButtonCarRental:
                            storage.setProfileType(2);
                            break;
                }
            }
        });

        requestQueue = Volley.newRequestQueue(this);
    }

    private boolean validateRegister(String email, String password, String password2) {
        if (email == null || email.trim().length() == 0) {
            Toast.makeText(getApplicationContext(), "Email ist erforderlich!", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (password == null || password.trim().length() == 0) {
            Toast.makeText(getApplicationContext(), "Passwort ist erforderlich!", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (password2 == null || password2.trim().length() == 0) {
            Toast.makeText(getApplicationContext(), "Passwort bestätigen!", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (password.trim().length() != password2.trim().length() ) {
            Toast.makeText(getApplicationContext(), "Passwörter stimmen nicht überein!", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void doRegister(String email, String password) {
        JSONObject signUpJson = new JSONObject();
        try {
            //signUpJson.put("Usertype", storage.getProfileType());
            signUpJson.put("email", email);
            signUpJson.put("password", password);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest signUpRequest = new JsonObjectRequest(Request.Method.POST, userUrl, signUpJson,
            new com.android.volley.Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    Log.i(LOG_TAG, "RESPONSE: " + response.toString());
                    Intent i = new Intent(getApplicationContext(), SignInActivity.class);
                    getApplicationContext().startActivity(i);
                    finish();
                }
            },
            new com.android.volley.Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e(LOG_TAG,"ERROR: " + error.getMessage());
                }
        });

        requestQueue.add(signUpRequest);
    }
}