package de.hfu.butler.service;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.provider.CalendarContract;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.format.DateUtils;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;

import de.hfu.butler.model.DataCalendar;
import de.hfu.butler.model.DataEvent;

public class ReadCalendar
{
    private final int READ_CALENDAR_PERMISSION = 0 ;

    private final String BASE_URL = "http://192.52.32.250:3000/api/";
    private final String suffixCalendar = "calendars";
    private final String suffixEvents = "events";
    private final String aceessToken = "?access_token=fLxV45XwpP5fcg7m2e1umwcFv4pCdZdyaXsdPI8r7RjMMfQuTUEYFH1GWl1AAZU9";
    private final RequestQueue requestQueue;

    private final Activity activity;
    private final ContentResolver contentResolver;
    private final Context context;
    private  Cursor cursor;
    private SessionStorage session = SessionStorage.getInstance();

    public ReadCalendar(Activity activity){
        this.context = activity;
        this.activity = activity;
        this.contentResolver = context.getContentResolver();
        requestQueue = Volley.newRequestQueue(activity);

    }

    public boolean requestPermission(){
        if (ContextCompat.checkSelfPermission(activity,Manifest.permission.READ_CALENDAR)!= PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(activity,
                    Manifest.permission.READ_CALENDAR)) {

            } else {
                ActivityCompat.requestPermissions(activity,new String[]{Manifest.permission.READ_CALENDAR},READ_CALENDAR_PERMISSION);
            }
        }
        return true;
    }


    public void readCalendar() {

        ArrayList<DataCalendar> calendars ;
        ArrayList<DataEvent> events ;

        calendars = getCalendars(contentResolver);
        JSONObject json = getJSONCalenders(calendars);
        updateCalendarData(json);

        for (DataCalendar calendar : calendars) {

            events = getEventsOf(calendar,contentResolver);
            JSONObject jsonEvents = getJSONEvents(events);
            updateEventData(jsonEvents);

        }
    }

    private void updateEventData(JSONObject jsonEvents) {

        String url = BASE_URL + suffixEvents + "?access_token=" + session.getSessionToken();

        JsonObjectRequest eventRequest = new JsonObjectRequest
                (Request.Method.POST, url, jsonEvents, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Log.i("Events",response.toString());
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("Events",error.getMessage());
                    }
                });

        requestQueue.add(eventRequest);
    }

    private void updateCalendarData(JSONObject json) {

        String url = BASE_URL + suffixCalendar + "?access_token=" + session.getSessionToken();


        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.PUT, url, json, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Log.i("Calendar",response.toString());
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("Calendar",error.getMessage());
                    }
                });

        requestQueue.add(jsonObjectRequest);
    }

    private JSONObject getJSONEvents(ArrayList<DataEvent> events) {

        JSONObject json =null;
        JSONArray jsonArray = new JSONArray();


        try {
            for (DataEvent event: events) {
                json = new JSONObject();

                json.put("title",event.getTitle());
                json.put("description",event.getDescription());
                json.put("start",event.getStart());
                json.put("end",event.getEnd());
                json.put("calendarId",event.getCalendarID());

                jsonArray.put(json);
            }

            json = jsonArray.toJSONObject(jsonArray);


        } catch (JSONException e) {
            e.printStackTrace();
        }


        return json;

    }

    private JSONObject getJSONCalenders(ArrayList<DataCalendar> calendars) {

        JSONObject json = new JSONObject();
        JSONObject calendarJson;

        JSONArray array = new JSONArray();
        try {

            for (DataCalendar calendar: calendars) {
                calendarJson = new JSONObject();
                calendarJson.put("id",calendar.getId());
                calendarJson.put("name",calendar.getName());
                calendarJson.put("profileId",SessionStorage.getInstance().getProfileId());

                array.put(calendarJson);

            }

            json = array.toJSONObject(array);


        } catch (JSONException e) {
            e.printStackTrace();
        };



        return json;
    }

    private ArrayList<DataEvent> getEventsOf(DataCalendar calendar, ContentResolver contentResolver) {

        ArrayList<DataEvent> events = new ArrayList<>();

        Uri.Builder builder = Uri.parse("content://com.android.calendar/instances/when").buildUpon();
        long now = new Date().getTime();

        ContentUris.appendId(builder, now - DateUtils.DAY_IN_MILLIS * 10000);
        ContentUris.appendId(builder, now + DateUtils.DAY_IN_MILLIS * 10000);

        String[] PROJEKTION_COLUMN = new String[]  { "title","description", "begin", "end"};
        String selector = CalendarContract.Instances.CALENDAR_ID + "=" + calendar.getId();
        String orderBy ="startDay ASC, startMinute ASC";

        Cursor eventCursor = contentResolver.query(builder.build(),PROJEKTION_COLUMN,selector,null, orderBy);

        if(eventCursor.getCount()>0 && eventCursor.moveToFirst())
        {

            do
            {

                final String title = eventCursor.getString(0);
                final String description = eventCursor.getString(1);
                final Date begin = new Date(eventCursor.getLong(2));
                final Date end = new Date(eventCursor.getLong(3));
                final int id = calendar.getId();

                events.add(new DataEvent(title,description,begin,end,id));

            }
            while(eventCursor.moveToNext());
        }
        return events;
    }

    private  ArrayList<DataCalendar> getCalendars(ContentResolver contentResolver){

        String[] PROJEKTION_COLUMN =new String[] { "_id", "calendar_displayName"};
        Uri calendar_uri = Uri.parse("content://com.android.calendar/calendars");

        cursor = contentResolver.query(calendar_uri, PROJEKTION_COLUMN, null, null, null);


        ArrayList<DataCalendar> calendars = new ArrayList<>();
        try
        {
            if(cursor.getCount() > 0)
            {
                while (cursor.moveToNext()) {

                    String _id = cursor.getString(0);
                    String displayName = cursor.getString(1);

                    calendars.add(new DataCalendar(Integer.parseInt(_id),displayName));
                }
            }
        }
        catch(AssertionError ex){
            ex.printStackTrace();
        }
        catch(Exception e){
            e.printStackTrace();
        }
        return calendars;
    }



}