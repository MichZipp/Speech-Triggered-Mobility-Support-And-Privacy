package de.hfu.furti.activities;

import android.app.Activity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import ai.kitt.snowboy.demo.R;
import de.hfu.furti.login.Login;

public class ShowProfilesActivity extends Activity {

    Button btnGetRepos;
    TextView tvRepoList;
    RequestQueue requestQueue;

    String token;

    String baseUrl = "http://192.52.33.31:3000/api/";
    String url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        token = getIntent().getStringExtra("KEY_AUTH_TOKEN");

        this.btnGetRepos = (Button) findViewById(R.id.btn_get_repos);
        this.tvRepoList = (TextView) findViewById(R.id.tv_repo_list);
        this.tvRepoList.setMovementMethod(new ScrollingMovementMethod());

        requestQueue = Volley.newRequestQueue(this);

    }

    private void clearRepoList() {
        // This will clear the repo list (set it as a blank string).
        this.tvRepoList.setText("");
    }

    private void addToRepoList(String vorname, String name, String geburtsdatum, String strasse, String hausnummer, String stadt, String postleitzahl, String land, String profileName, String profilType, String id, String UserId) {
        String strRow = vorname + " / " + name + " / " + geburtsdatum + " / " + strasse + " / " + hausnummer + " / " + stadt + " / " + postleitzahl + " / " + land + " / " + profileName + " / " + profilType + " / " + id + " / " + UserId;
        String currentText = tvRepoList.getText().toString();
        Log.e("tvRepoList ", tvRepoList.getText().toString());
        this.tvRepoList.setText(currentText + "\n\n" + strRow);
    }

    private void setRepoListText(String str) {
        this.tvRepoList.setText(str);
    }

    private void getRepoList(String username) {
        this.url = this.baseUrl + "users/"+ LoginActivity.getUserID() +"/profiles/";

        JsonArrayRequest arrReq = new JsonArrayRequest(Request.Method.GET, url,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        if (response.length() > 0) {
                            for (int i = 0; i < response.length(); i++) {
                                try {
                                    JSONObject jsonObj = response.getJSONObject(i);
                                    String vorname = jsonObj.get("vorname").toString();
                                    String name = jsonObj.get("name").toString();
                                    String geburtsdatum = jsonObj.get("geburtsdatum").toString();
                                    String strasse = jsonObj.get("strasse").toString();
                                    String hausnummer = jsonObj.get("hausnummer").toString();
                                    String stadt = jsonObj.get("land").toString();
                                    String postleitzahl = jsonObj.get("postleitzahl").toString();
                                    String land = jsonObj.get("land").toString();
                                    String profilename = jsonObj.get("profilename").toString();
                                    String profiletype = jsonObj.get("profiletype").toString();
                                    String id = jsonObj.get("id").toString();
                                    String userid = jsonObj.get("userId").toString();
                                    addToRepoList(vorname, name, geburtsdatum, strasse, hausnummer, stadt, postleitzahl, land, profilename, profiletype, id, userid);
                                    Log.e("", response.toString());
                                } catch (JSONException e) {
                                    Log.e("Volley", "Invalid JSON Object.");
                                }

                            }
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // If there a HTTP error
                setRepoListText("Error while calling REST API");
                Log.e("Volley", error.toString());
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<String, String>();
                headers.put("Authorization", token);
                return headers;
            }
        };
        requestQueue.add(arrReq);
        }

        public void getReposClicked (View v){
            clearRepoList();
            getRepoList(tvRepoList.getText().toString());
        }

    }
