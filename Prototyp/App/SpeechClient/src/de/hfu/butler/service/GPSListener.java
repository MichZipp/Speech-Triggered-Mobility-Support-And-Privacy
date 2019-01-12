package de.hfu.butler.service;

import android.app.Activity;
import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.widget.EditText;
import java.io.IOException;
import java.util.List;
import java.util.Locale;

import de.hfu.butler.R;


public class GPSListener implements LocationListener {

    private final Activity activity;
    private Location mLastLocation;
    private Context context;
    private EditText locationText;

    public GPSListener(Activity context){

        mLastLocation = new Location("Test");
        this.context = context;
        this.activity = context;

    }

    @Override
    public void onLocationChanged(Location location) {
        mLastLocation.set(location);

        double lat = mLastLocation.getLatitude();
        double lng = mLastLocation.getLongitude();

        locationText = activity.findViewById(R.id.editTextLocation);

        String cityName = getCityName(lat,lng);
        locationText.setText(cityName);

    }

    private String getCityName(double lat, double lng) {

        String cityName ="Kein genauen Ort gefunden";

        Geocoder gcd = new Geocoder(context, Locale.getDefault());
        List<Address> addresses;
        try {
            addresses = gcd.getFromLocation(lat, lng, 1);
            if (addresses.size() > 0) {
                System.out.println(addresses.get(0).getLocality());
                cityName = addresses.get(0).getLocality();
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        return cityName;
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderDisabled(String provider) {}

    @Override
    public void onProviderEnabled(String provider) {}



}
