package de.hfu.butler.activities;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CalendarContract.Calendars;
import android.provider.CalendarContract.Instances;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import ai.kitt.snowboy.demo.R;

public class CalendarActivity extends Activity {

    //TextView serverResp;
    String token;
    RequestQueue requestQueue;
    static final String REQ_TAG = "VACTIVITY";
    String baseUrl = "http://192.52.33.31:3000/api/";
    String url;

    String title2;
    String date, date1, date2;

    public static final int PERMS_REQUEST_CODE = 1;

// Rauslesen von Account Name usw
//    public static final String[] EVENT_PROJECTION = new String[]{
//            Calendars._ID,                           // 0
//            Calendars.ACCOUNT_NAME,                  // 1
//            Calendars.CALENDAR_DISPLAY_NAME,         // 2
//            Calendars.OWNER_ACCOUNT                  // 3
//    };

    //Rauslesen von Events
    public static final String[] INSTANCE_PROJECTION = new String[]{
            Instances.EVENT_ID,                      // 0
            Instances.BEGIN,                         // 1
            Instances.END,                           // 2
            Instances.TITLE,                         // 3
    };

    private static final int PROJECTION_ID_INDEX = 0;
    // Rauslesen von Account Name usw
//    private static final int PROJECTION_ACCOUNT_NAME_INDEX = 1;
//    private static final int PROJECTION_DISPLAY_NAME_INDEX = 2;
//    private static final int PROJECTION_OWNER_ACCOUNT_INDEX = 3
//Rauslesen von Events
//    private static final int PROJECTION_BEGIN_INDEX = 1;
//    private static final int PROJECTION_TITLE_INDEX = 2;
    private static final int EXTRA_EVENT_BEGIN_TIME = 1;
    private static final int EXTRA_EVENT_END_TIME = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.calendar);

        TextView tv = (TextView) findViewById(R.id.textView4);

        token = getIntent().getStringExtra("KEY_AUTH_TOKEN");

        requestQueue = Volley.newRequestQueue(this);

        //---------------PERMISSION CHECK-------------------------------------

        if (ContextCompat.checkSelfPermission(CalendarActivity.this,
                Manifest.permission.READ_CALENDAR)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(CalendarActivity.this,
                    Manifest.permission.READ_CALENDAR)) {
            } else {
                ActivityCompat.requestPermissions(CalendarActivity.this,
                        new String[]{Manifest.permission.READ_CALENDAR},
                        PERMS_REQUEST_CODE);
            }
        } else {

// ----------Permission has already been granted -> Main--------------------------------------------

            // Specify the date range you want to search for recurring event instances
            Calendar beginTime2 = Calendar.getInstance();
            beginTime2.set(2019, Calendar.JANUARY, 02, 7, 0);
            long startMillis2 = beginTime2.getTimeInMillis();
            Calendar endTime2 = Calendar.getInstance();
            endTime2.set(2019, Calendar.JANUARY, 02, 13, 0);
            long endMillis2 = endTime2.getTimeInMillis();

//-------------------Rauslesen von Events-----------------------------------------------------------

            //Run Query
            Cursor cur, cur2;
            ContentResolver cr = getContentResolver();
            Uri uri = Calendars.CONTENT_URI;

            // The ID of the recurring event whose instances you are searching for in the Instances table
            String selection2 = Instances.EVENT_ID + " = ?";
            String[] selectionArgs2 = new String[]{"207"};

            // Construct the query with the desired date range.
            Uri.Builder builder = Instances.CONTENT_URI.buildUpon();
            ContentUris.appendId(builder, startMillis2);
            ContentUris.appendId(builder, endMillis2);

            // Submit the query
            cur2 = cr.query(builder.build(), INSTANCE_PROJECTION, null, null, null);

            while (cur2.moveToNext()) {
                long eventID;
                long beginVal;
                long eventStart;
                long eventEnd;

                // Get the field values
//                eventID = cur2.getLong(PROJECTION_ID_INDEX);
//                beginVal = cur2.getLong(PROJECTION_BEGIN_INDEX);
//                title2 = cur2.getString(PROJECTION_TITLE_INDEX);
                eventStart = cur2.getLong(EXTRA_EVENT_BEGIN_TIME);
                eventEnd = cur2.getLong(EXTRA_EVENT_END_TIME);

                //Do something with the values.
                //Log.e("Hallo", "Event:  " + title2);
                //Calendar calendar = Calendar.getInstance();
                Calendar calBegin = Calendar.getInstance();
                Calendar calEnd = Calendar.getInstance();
                //calendar.setTimeInMillis(beginVal);
                calBegin.setTimeInMillis(eventStart);
                calEnd.setTimeInMillis(eventEnd);
                DateFormat formatter = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
                //date = formatter.format(calendar.getTime());
                date1 = formatter.format(calBegin.getTime());
                date2 = formatter.format(calEnd.getTime());
                //Log.e("Hallo", "Date: " + date);
                Log.e("Hallo", "Startdate: " + date1);
                Log.e("Hallo", "Enddate: " + date2);
                sendEvents();
            }

//-------------------Rauslesen von Account Name usw-------------------------------------------------

//            String selection = "((" + Calendars.ACCOUNT_NAME + " = ?) AND ("
//                    + Calendars.ACCOUNT_TYPE + " = ?) AND ("
//                    + Calendars.OWNER_ACCOUNT + " = ?))";
//            String[] selectionArgs = new String[]{"hera@example.com", "com.example",
//                    "hera@example.com"};
//
//            cur = cr.query(uri, EVENT_PROJECTION, null, null, null);
//
//            while (cur.moveToNext()) {
//                long calID = 0;
//                String title = null;
//                String displayName = null;
//                String accountName = null;
//                String ownerName = null;
//
//                // Get the field values
//                calID = cur.getLong(PROJECTION_ID_INDEX);
//                displayName = cur.getString(PROJECTION_DISPLAY_NAME_INDEX);
//                accountName = cur.getString(PROJECTION_ACCOUNT_NAME_INDEX);
//                ownerName = cur.getString(PROJECTION_OWNER_ACCOUNT_INDEX);
//
//                 Do something with the values...
//                Log.e("DISPLAYNAME ", displayName);
//                Log.e("ACCOUNTNAME ", accountName);
//                Log.e("OWNERNAME ", ownerName);
//                displayLabel = (TextView) findViewById(R.id.textView4);
//                displayLabel.setText(displayName);
//
//            }
        }
    }

    private void sendEvents() {
        this.url = this.baseUrl + "events";
        String name = null;
        String summary = null;
        String description = null;
        String startTime = date1;
        String endTime = date2;
        String creationTime = null;

        JSONObject json = new JSONObject();
        try {
            json.put("name", name);
            json.put("summary", summary);
            json.put("description", description);
            json.put("startTime", startTime);
            json.put("endTime", endTime);
            json.put("creationTime", creationTime);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, json,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.e("String Response ", response.toString());
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Error getting response", requestQueue.toString());
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
}
