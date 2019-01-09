package de.hfu.furti.activities;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.JsonObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import ai.kitt.snowboy.demo.R;
import de.hfu.furti.MainActivity;
import de.hfu.furti.login.ApiUtils;
import de.hfu.furti.login.Login;
import de.hfu.furti.login.ResObj;
import de.hfu.furti.service.UserService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class LoginActivity extends Activity {

    private static String token;

    Retrofit.Builder builder = new Retrofit.Builder()
            .baseUrl("http://192.52.33.31:3000/api/users/")
            .addConverterFactory(GsonConverterFactory.create());

    Retrofit retrofit = builder.build();
    EditText editEmail;
    EditText editPassword;
    Button btnLogin;
    UserService userService;
    SharedPreferences pref;
    private static int userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        ActionBar bar = getActionBar();
        bar.setBackgroundDrawable(new ColorDrawable(Color.BLACK));

        pref = getSharedPreferences("PREF", Context.MODE_PRIVATE);

        editEmail = (EditText) findViewById(R.id.editEmail);
        editPassword = (EditText) findViewById(R.id.editPassword);
        btnLogin = (Button) findViewById(R.id.btnLogin);
        userService = ApiUtils.getUserService();

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = editEmail.getText().toString();
                String password = editPassword.getText().toString();
                if (validateLogin(email, password)) {
                    doLogin(email, password);
                }
            }
        });

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
        Login login = new Login(email, password);
        Call<ResObj> call = userService.login(login);

        call.enqueue(new Callback<ResObj>() {

            @Override
            public void onResponse(Call<ResObj> call, Response<ResObj> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(getApplicationContext(), response.body().getId(), Toast.LENGTH_SHORT).show();


                    userID = response.body().getUserId();
                    token = response.body().getId();

                    String userId = new String("userId");

                    try {
                        response.message().getBytes(userId);
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }


                    SharedPreferences.Editor editor = pref.edit();
                    editor.putString("TOKEN", token);
                    editor.commit();
                    pref = getSharedPreferences("PREF", Context.MODE_PRIVATE);
                    String storedToken = pref.getString("TOKEN", "");
                    Log.e("Token: ", storedToken);

                    //start next Activity

                    //Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    Intent intent = new Intent(LoginActivity.this, PersonalProfile.class);
                    intent.putExtra("KEY_AUTH_TOKEN", token);
                    getApplicationContext().startActivity(intent);
                } else {
                    Toast.makeText(getApplicationContext(), "Email oder Passwort falsch!", Toast.LENGTH_SHORT).show();
                    try {
                        Log.e("Error:", response.errorBody().string());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<ResObj> call, Throwable t) {
                Toast.makeText(getApplicationContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("ERROR!", t.getMessage());
            }
        });

    }

    public static int getUserID(){
        return userID;
    }

}