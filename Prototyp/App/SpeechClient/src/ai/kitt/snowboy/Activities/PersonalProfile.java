package ai.kitt.snowboy.Activities;

import android.app.Activity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import ai.kitt.snowboy.demo.R;

public class PersonalProfile extends Activity {

    EditText etGitHubUser;

    EditText firstNameView;
    EditText lastNameView;
    EditText birthDateView;
    EditText streetNameView;
    EditText houseNumberView;
    EditText cityView;
    EditText postalCodeView;
    EditText countryView;
    EditText profileNameView;
    //EditText idView;
    String firstName;
    String lastName;
    String birthDate;
    String streetName;
    String houseNumber;
    String city;
    String postalCode;
    String country;
    String profileName;
    //String id;
    Button btnGetRepos;
    Button btnSendRepos;
    TextView tvRepoList;
    TextView serverResp;
    RequestQueue requestQueue;
    static final String REQ_TAG = "VACTIVITY";

    String baseUrl = "http://192.52.33.31:3000/api/";
    String url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.personalprofile);

        firstNameView = (EditText) findViewById(R.id.firstName);
        lastNameView= (EditText) findViewById(R.id.lastName);
        birthDateView = (EditText) findViewById(R.id.birthDate);
        streetNameView = (EditText) findViewById(R.id.streetName);
        houseNumberView = (EditText) findViewById(R.id.houseNumber);
        cityView = (EditText) findViewById(R.id.city);
        postalCodeView = (EditText) findViewById(R.id.postalCode);
        countryView = (EditText) findViewById(R.id.country);
        profileNameView = (EditText) findViewById(R.id.profileName);
        //idView = (EditText) findViewById(R.id.id);
        this.btnGetRepos = (Button) findViewById(R.id.btn_get_repos);
        btnGetRepos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getReposClicked(v);
            }
        });
        this.btnSendRepos = (Button) findViewById(R.id.btn_send_data);
        btnSendRepos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendRepoList();
            }
        });

    serverResp =(TextView)

    findViewById(R.id.server_resp);
        serverResp.setMovementMethod(new

                ScrollingMovementMethod());
    requestQueue =Volley.newRequestQueue(this);

}

    private void clearRepoList() {
        this.tvRepoList.setText("");
    }

    private void addToRepoList(String firstName, String lastName, String birthDate, String streetName, String houseNumber, String city, String postalCode, String country, String profileName, String id) {
        String strRow = firstName + " / " + lastName + " / " + birthDate + " / " + streetName + " / " + houseNumber + " / " + city + " / " + postalCode + " / " + country + " / " + profileName + " / " + id;
        String currentText = tvRepoList.getText().toString();
        this.tvRepoList.setText(currentText + "\n\n" + strRow);
    }

    private void setRepoListText(String str) {
        this.tvRepoList.setText(str);
    }

    private void sendRepoList() {
        this.url = this.baseUrl + "/profiles";
        firstName = firstNameView.getText().toString();
        lastName = lastNameView.getText().toString();
        birthDate = birthDateView.getText().toString();
        streetName = streetNameView.getText().toString();
        houseNumber = houseNumberView.getText().toString();
        city = cityView.getText().toString();
        postalCode = postalCodeView.getText().toString();
        country = countryView.getText().toString();
        profileName = profileNameView.getText().toString();
        //id = idView.getText().toString();

        JSONObject json = new JSONObject();
        try {
            json.put("firstname", firstName);
            json.put("lastname", lastName);
            json.put("birthdate", birthDate);
            json.put("streetname", streetName);
            json.put("housenumber", houseNumber);
            json.put("city", city);
            json.put("postalcode", postalCode);
            json.put("country", country);
            json.put("profilename", profileName);
            //json.put("id", id);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, json,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        serverResp.setText("String Response : " + response.toString());
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                serverResp.setText("Error getting response");
            }
        });
        jsonObjectRequest.setTag(REQ_TAG);
        requestQueue.add(jsonObjectRequest);

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
