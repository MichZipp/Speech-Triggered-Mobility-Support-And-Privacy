package de.hfu.butler.activities;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import de.hfu.butler.MainActivity;
import de.hfu.butler.R;
import de.hfu.butler.service.GPSListener;
import de.hfu.butler.service.ReadCalendar;
import de.hfu.butler.service.SessionStorage;

public class ProfileActivity extends Activity {
    private final String LOG_TAG = "ProfileActivity";
    private final String baseUrl = "http://192.52.32.250:3000/api/customers/";
    private RequestQueue requestQueue;

    private LocationListener locationListener = new GPSListener(this);

    private EditText editFirstname;
    private EditText editLastname;
    private EditText editLocation;
    private Button btnUpdateProfile;
    private Button btnUpdateLocation;
    private Button btnUpdateCalendar;
    private CheckBox checkBoxLocationUpdate;
    private CheckBox checkBoxCalendarUpdate;
    private CheckBox checkBoxLocationEvent;
    private CheckBox checkBoxScalaMobile;
    private CheckBox checkBoxPickUpService;

    private SessionStorage storage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        storage = SessionStorage.getInstance();

        editFirstname = (EditText) findViewById(R.id.editTextFirstname);
        editLastname = (EditText) findViewById(R.id.editTextLastname);
        editLocation = (EditText) findViewById(R.id.editTextLocation);
        btnUpdateProfile = (Button) findViewById(R.id.buttonUpdateProfile);
        btnUpdateLocation = (Button) findViewById(R.id.buttonUpdateLocation);
        btnUpdateCalendar = (Button) findViewById(R.id.buttonUpdateCalendar);
        checkBoxLocationUpdate = (CheckBox) findViewById(R.id.checkBoxLocation);
        checkBoxCalendarUpdate = (CheckBox) findViewById(R.id.checkBoxCalendarUpdate);
        checkBoxLocationEvent = (CheckBox) findViewById(R.id.checkBoxCalendarEvent);
        checkBoxScalaMobile = (CheckBox) findViewById(R.id.checkBoxScalaMobile);
        checkBoxPickUpService = (CheckBox) findViewById(R.id.checkBoxPickUpService);

        btnUpdateProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateProfile();
            }
        });

        btnUpdateLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // set editLocation text based on current position!
                updateLocation();
            }
        });

        btnUpdateCalendar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // send calendar to API
                updateCalendar();
            }
        });

        requestQueue = Volley.newRequestQueue(this);

        getProfile();
    }

    private void getProfile(){
        String url = baseUrl + storage.getUserId() + "/profiles?access_token=" + storage.getSessionToken();

        JsonArrayRequest getProfileRequest = new JsonArrayRequest(Request.Method.GET, url,
            new com.android.volley.Response.Listener<JSONArray>() {
                @Override
                public void onResponse(JSONArray response) {
                    Log.i(LOG_TAG, "RESPONSE: " + response.toString());
                    String firstname, lastname, location, id;
                    int pickUp, scalaMobile;
                    try {
                    JSONObject profile = (JSONObject) response.get(0);
                    firstname = profile.getString("vorname");
                    lastname = profile.getString("name");
                    location = profile.getString("location");
                    pickUp = profile.getInt("pickup");
                    scalaMobile = profile.getInt("scalamobile");
                    id = profile.getString("id");
                    storage.setProfileId(id);

                    editFirstname.setText(firstname);
                    editLastname.setText(lastname);
                    editLocation.setText(location);

                    if(pickUp == 0){
                        checkBoxPickUpService.setChecked(false);
                    } else {
                        checkBoxPickUpService.setChecked(true);
                    }

                    if(scalaMobile == 0){
                        checkBoxScalaMobile.setChecked(false);
                    } else {
                        checkBoxScalaMobile.setChecked(true);
                    }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            },
            new com.android.volley.Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e(LOG_TAG,"ERROR: " + error.toString());
                }
            });

        requestQueue.add(getProfileRequest);
    }

    private void updateProfile() {
        String url = baseUrl + storage.getUserId() + "/profiles/" + storage.getProfileId() + "?access_token=" + storage.getSessionToken();

        String firstname = editFirstname.getText().toString();
        String lastname = editLastname.getText().toString();
        String location = editLocation.getText().toString();
        int scalaMobile = 0, pickUp = 0;
        if(checkBoxScalaMobile.isChecked()){
            scalaMobile = 1;
        }
        if(checkBoxPickUpService.isChecked()){
            pickUp = 1;
        }


        JSONObject profileJson = new JSONObject();
        try {
            profileJson.put("vorname", firstname);
            profileJson.put("name", lastname);
            profileJson.put("location", location);
            profileJson.put("scalamobile", scalaMobile);
            profileJson.put("pickup", pickUp);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest updateProfileRequest = new JsonObjectRequest(Request.Method.PUT, url, profileJson,
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
                });

        requestQueue.add(updateProfileRequest);
    }

    private void updateLocation() {
        int requestPermissionsCode = 50;
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, requestPermissionsCode);
            return;
        }

        //Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        locationManager.requestSingleUpdate(LocationManager.GPS_PROVIDER, locationListener, null);
    }

    private void updateCalendar(){

        ReadCalendar reader = new ReadCalendar(this);

        reader.requestPermission();
        reader.readCalendar();
    }
}

