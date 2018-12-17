package de.hfu.furti.activities;

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

import java.io.IOException;

import ai.kitt.snowboy.demo.R;
import de.hfu.furti.MainActivity;
import de.hfu.furti.service.Login;
import de.hfu.furti.model.ResObj;
import de.hfu.furti.service.User;
import de.hfu.furti.remote.ApiUtils;
import de.hfu.furti.remote.UserService;
import de.hfu.furti.service.UserClient;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class LoginActivity extends Activity {

    Retrofit.Builder builder = new Retrofit.Builder()
            .baseUrl("http://192.52.33.31:3000/api/users/")
            .addConverterFactory(GsonConverterFactory.create());

    Retrofit retrofit = builder.build();

    UserClient userClient = retrofit.create(UserClient.class);

    EditText editEmail;
    EditText editPassword;
    Button btnLogin;
    Button loginHard;
    Button getToken;
    UserService userService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        ActionBar bar = getActionBar();
        bar.setBackgroundDrawable(new ColorDrawable(Color.BLACK));

        editEmail = (EditText) findViewById(R.id.editEmail);
        editPassword = (EditText) findViewById(R.id.editPassword);
        btnLogin = (Button) findViewById(R.id.btnLogin);
        loginHard = (Button) findViewById(R.id.loginHard);
        getToken = (Button) findViewById(R.id.getToken);
        userService = ApiUtils.getUserService();

        btnLogin.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                String email = editEmail.getText().toString();
                String password = editPassword.getText().toString();
                if(validateLogin(email, password)) {
                    doLogin(email, password);
                }
            }
        });

        loginHard.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                loginHard();
            }
        });

        getToken.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                getToken();
            }
        });
    }

    private static String token;

    private void loginHard() {
        Login login = new Login("michael@zipperle.de", "privacy");
        Call<User> call = userClient.login(login);

        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(getApplicationContext(), response.body().getId(), Toast.LENGTH_SHORT).show();
                    token = response.body().getId();
                } else {
                    Toast.makeText(getApplicationContext(), "Error on Response", Toast.LENGTH_SHORT).show();
                    try {
                        Log.e("Error:", response.errorBody().string());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "Error on Failure", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getToken() {

        Call<ResponseBody> call = userClient.getToken(token);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(getApplicationContext(), response.body().toString(), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(), "Error on Response", Toast.LENGTH_SHORT).show();
                    try {
                        Log.e("Error:", response.errorBody().string());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_SHORT).show();
            }
        });


    }

    private boolean validateLogin(String email, String password) {
        if (email == null || email.trim().length() == 0) {
            Toast.makeText(getApplicationContext(), "Email is required!", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (password == null || password.trim().length() == 0) {
            Toast.makeText(getApplicationContext(),"Password is required!", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void doLogin(String email, String password) {
        Login login = new Login (email, password);
        Call<ResObj> call = userService.login(login);
        call.enqueue(new Callback<ResObj>() {
            @Override
            public void onResponse(Call<ResObj> call, Response<ResObj> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(getApplicationContext(), response.body().getId(), Toast.LENGTH_SHORT).show();
                    token = response.body().getId();
                    Log.e("", response.body().getId());
                    //start MainActivity
                    Intent i = new Intent(getApplicationContext(), MainActivity.class);
                    getApplicationContext().startActivity(i);
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
}
