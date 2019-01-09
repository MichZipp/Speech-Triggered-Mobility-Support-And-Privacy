package de.hfu.furti.activities;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.app.ActionBar;
import android.app.Activity;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ai.kitt.snowboy.demo.R;
import de.hfu.furti.Fragments.LocationFragment;
import de.hfu.furti.Fragments.TimePickerFragment;

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
    String houseNumber1;
    //int houseNumber;
    String city;
    String postalCode1;
    String postalCode;
    //int postalCode;
    String country;
    String profileName;
    String profileType;
    //int profileType;
    //String id;
    Button btnGetRepos;
    Button btnSendRepos;
    Button location;
    Button calendar;
    TextView tvRepoList;
    TextView serverResp;
    RequestQueue requestQueue;
    static final String REQ_TAG = "VACTIVITY";
    FragmentManager fm = getFragmentManager();

    String token;

    String baseUrl = "http://192.52.33.31:3000/api/";
    String url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.personalprofile);

        ActionBar bar = getActionBar();
        bar.setBackgroundDrawable(new ColorDrawable(Color.BLACK));

        token = getIntent().getStringExtra("KEY_AUTH_TOKEN");
//        Log.e("TOKENNNNN", token);

        List<String> spinnerArray = new ArrayList<String>();
        spinnerArray.add("Persönliches Profil");
        spinnerArray.add("Profil mit Kalenderzugriff");
        spinnerArray.add("Profil mit Standortzugriff");

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.spinner_item, spinnerArray);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        Spinner spinner = (Spinner) findViewById(R.id.profileSpinner);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                Object item = adapterView.getItemAtPosition(position);
                if (item != null) {
                    Toast.makeText(PersonalProfile.this, item.toString(),
                            Toast.LENGTH_SHORT).show();
                }
                if (item.equals("Profil mit Standortzugriff")) {
                    location.setVisibility(View.VISIBLE);
                }
                if (item.equals("Profil mit Kalenderzugriff")) {
                    calendar.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                // TODO Auto-generated method stub
            }
        });

        firstNameView = (EditText) findViewById(R.id.firstName);
        lastNameView = (EditText) findViewById(R.id.lastName);
        birthDateView = (EditText) findViewById(R.id.birthDate);
        streetNameView = (EditText) findViewById(R.id.streetName);
        houseNumberView = (EditText) findViewById(R.id.houseNumber);
        cityView = (EditText) findViewById(R.id.city);
        postalCodeView = (EditText) findViewById(R.id.postalCode);
        countryView = (EditText) findViewById(R.id.country);

        this.btnSendRepos = (Button) findViewById(R.id.btn_send_data);
        btnSendRepos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendRepoList();
            }
        });

        this.location = (Button) findViewById(R.id.location);
        location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showLocationDialog(v);
            }
        });

//        this.calendar = (Button) findViewById(R.id.calendar);
//        calendar.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                showLocationDialog(v);
//            }
//        });

        this.calendar = (Button) findViewById(R.id.calendar);
        calendar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(PersonalProfile.this, CalendarActivity.class);
                intent.putExtra("KEY_AUTH_TOKEN", token);
                getApplicationContext().startActivity(intent);
            }
        });

        serverResp = (TextView) findViewById(R.id.server_resp);
        serverResp.setMovementMethod(new ScrollingMovementMethod());
        requestQueue = Volley.newRequestQueue(this);

    }

    public void showTimePickerDialog(View v) {
        DialogFragment newFragment = new TimePickerFragment();
        newFragment.show(fm, "timePicker");
    }

    public void showLocationDialog(View v) {
        FragmentManager manager = getFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.add(R.id.frameLayout, new LocationFragment());
        transaction.addToBackStack(null);
        transaction.commit();
    }

    private void clearRepoList() {
        this.tvRepoList.setText("");
    }

    private void addToRepoList(String firstName, String lastName, String birthDate, String streetName, String houseNumber, String city, String postalCode, String country, String profileName, String profileType, String id) {
        String strRow = firstName + " / " + lastName + " / " + birthDate + " / " + streetName + " / " + houseNumber + " / " + city + " / " + postalCode + " / " + country + " / " + profileName + " / " + " / " + profileType + " / " + id;
        String currentText = tvRepoList.getText().toString();
        this.tvRepoList.setText(currentText + "\n\n" + strRow);
    }

    private void setRepoListText(String str) {
        this.tvRepoList.setText(str);
    }

    private void sendRepoList() {
        this.url = this.baseUrl + "users/10/profiles";
        firstName = firstNameView.getText().toString();
        lastName = lastNameView.getText().toString();
        birthDate = birthDateView.getText().toString();
        streetName = streetNameView.getText().toString();
        houseNumber = houseNumberView.getText().toString();
        city = cityView.getText().toString();
        postalCode = postalCodeView.getText().toString();
        country = countryView.getText().toString();
        Spinner spinner = (Spinner) findViewById(R.id.profileSpinner);
        profileName = spinner.getSelectedItem().toString();
        profileType = "0";

        JSONObject json = new JSONObject();
        try {
            json.put("vorname", firstName);
            json.put("name", lastName);
            json.put("geburtsdatum", birthDate);
            json.put("strasse", streetName);
            json.put("hausnummer", houseNumber);
            json.put("stadt", city);
            json.put("postleitzahl", postalCode);
            json.put("land", country);
            json.put("profilename", profileName);
            json.put("profiletype", profileType);

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
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<String, String>();
                headers.put("Authorization", token);
                return headers;
            }
        };
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
                                    String profiletype = jsonObj.get("profileType").toString();
                                    String id = jsonObj.get("id").toString();
                                    addToRepoList(firstname, lastname, birthdate, streetname, houseNumber, city, postalcode, country, profilename, profiletype, id);
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
