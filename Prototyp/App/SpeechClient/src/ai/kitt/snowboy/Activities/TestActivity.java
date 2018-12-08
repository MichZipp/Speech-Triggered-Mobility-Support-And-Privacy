package ai.kitt.snowboy.Activities;

import android.app.Activity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import ai.kitt.snowboy.demo.R;

public class TestActivity extends Activity {

    Button btnGetRepos;
    TextView tvRepoList;
    RequestQueue requestQueue;

    String baseUrl = "http://192.52.33.31:3000/api/";
    String url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        this.btnGetRepos = (Button) findViewById(R.id.btn_get_repos);
        this.tvRepoList = (TextView) findViewById(R.id.tv_repo_list);
        this.tvRepoList.setMovementMethod(new ScrollingMovementMethod());

        requestQueue = Volley.newRequestQueue(this);

    }

    private void clearRepoList() {
        // This will clear the repo list (set it as a blank string).
        this.tvRepoList.setText("");
    }

    private void addToRepoList(String firstName, String lastName, String birthDate, String streetName, String houseNumber, String city, String postalCode, String country, String profileName, String id) {
        String strRow = firstName + " / " + lastName + " / " + birthDate + " / " + streetName + " / " + houseNumber + " / " + city + " / " + postalCode + " / " + country + " / " + profileName + " / " + id;
        String currentText = tvRepoList.getText().toString();
        this.tvRepoList.setText(currentText + "\n\n" + strRow);
    }

    private void setRepoListText(String str) {
        // This is used for setting the text of our repo list box to a specific string.
        // We will use this to write a "No repos found" message if the user doens't have any.
        this.tvRepoList.setText(str);
    }

    private void getRepoList(String username) {
        this.url = this.baseUrl + "/profiles";

        JsonArrayRequest arrReq = new JsonArrayRequest(Request.Method.GET, url,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        if (response.length() > 0) {
                            for (int i = 0; i < response.length(); i++) {
                                try {
                                    JSONObject jsonObj = response.getJSONObject(i);
                                    String firstname = jsonObj.get("firstName").toString();
                                    String lastname = jsonObj.get("lastName").toString();
                                    String birthdate = jsonObj.get("birthDate").toString();
                                    String streetname = jsonObj.get("streetName").toString();
                                    String houseNumber = jsonObj.get("houseNumber").toString();
                                    String city = jsonObj.get("city").toString();
                                    String postalcode = jsonObj.get("postalCode").toString();
                                    String country = jsonObj.get("country").toString();
                                    String profilename = jsonObj.get("profileName").toString();
                                    String id = jsonObj.get("id").toString();
                                    addToRepoList(firstname, lastname, birthdate, streetname, houseNumber, city, postalcode, country, profilename, id);
                                } catch (JSONException e) {
                                    Log.e("Volley", "Invalid JSON Object.");
                                }

                            }
                        }
                    }
                },

                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // If there a HTTP error
                        setRepoListText("Error while calling REST API");
                        Log.e("Volley", error.toString());
                    }
                }
        );
        requestQueue.add(arrReq);
    }

    public void getReposClicked(View v) {
        clearRepoList();
        getRepoList(tvRepoList.getText().toString());
    }

}
