package de.hfu.furti.activities;


//FUNKTIONIERT, NICHT ANFASSEN!!!!!!!!!!!!!!!!!!!!!!!!!!!!

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
import android.widget.TextView;
import android.widget.Toast;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONException;
import org.json.JSONObject;

import ai.kitt.snowboy.demo.R;

public class RegisterActivity extends Activity {

    EditText editEmail;
    EditText editPassword;
    EditText confirmPassword;
    Button btnRegister;
    String email;
    String password;
    String baseUrl = "http://192.52.33.31:3000/api/";
    String url;
    TextView serverResp;
    RequestQueue requestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        ActionBar bar = getActionBar();
        bar.setBackgroundDrawable(new ColorDrawable(Color.BLACK));

        editEmail = (EditText) findViewById(R.id.editEmail);
        editPassword = (EditText) findViewById(R.id.editPassword);
        confirmPassword = (EditText) findViewById(R.id.confirmPassword);
        btnRegister = (Button) findViewById(R.id.btnReg);
        serverResp = (TextView) findViewById(R.id.server_resp);

        btnRegister.setOnClickListener(new View.OnClickListener() {
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

        sendRegData();
    }

    private void sendRegData() {
        this.url = this.baseUrl + "users";
        email = editEmail.getText().toString();
        password = editPassword.getText().toString();

        JSONObject json = new JSONObject();
        try {
            json.put("email", email);
            json.put("password", password);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, json,
                new com.android.volley.Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        serverResp.setText("String Response : " + response.toString());
                        Log.e("RESPONSE: ", response.toString());
                        Intent i = new Intent(getApplicationContext(), LoginActivity.class);
                        getApplicationContext().startActivity(i);
                    }
                }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                serverResp.setText("Error getting response");
            }
        });
        requestQueue.add(jsonObjectRequest);

    }

}