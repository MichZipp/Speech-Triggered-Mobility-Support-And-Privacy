package de.hfu.butler.activities;

import android.app.ActionBar;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.android.volley.RequestQueue;

import ai.kitt.snowboy.demo.R;

public class SingleProfileActivity extends Activity {
    //TO-DO: Werte anzeigen lassen

    TextView vornameV;
    TextView nachnameV;
    TextView geburtsdatumV;
    TextView strasseV;
    TextView hausnummerV;
    TextView stadtV;
    TextView landV;
    RequestQueue requestQueue;
    String token, vorname, nachname, geburtsdatum, strasse, hausnummer, stadt, land;

    String baseUrl = "http://192.52.33.31:3000/api/";
    String url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.show_single_profile);

        ActionBar bar = getActionBar();
        getActionBar().hide();

        this.vornameV = (TextView) findViewById(R.id.Vorname);
        this.nachnameV = (TextView) findViewById(R.id.Nachname);
        this.geburtsdatumV = (TextView) findViewById(R.id.Geburtsdatum);
        this.strasseV = (TextView) findViewById(R.id.Strasse);
        this.hausnummerV = (TextView) findViewById(R.id.Hausnummer);
        this.stadtV = (TextView) findViewById(R.id.Stadt);
        this.landV = (TextView) findViewById(R.id.Land);
        //this.tvRepoList.setMovementMethod(new ScrollingMovementMethod());

        Bundle extras = getIntent().getExtras();
        token = getIntent().getStringExtra("KEY_AUTH_TOKEN");
        Log.e("TOKEN", token);
        vorname = getIntent().getStringExtra("VORNAME");
        nachname = getIntent().getStringExtra("NACHNAME");
        geburtsdatum = getIntent().getStringExtra("GEBURTSDATUM");
        strasse = getIntent().getStringExtra("STRASSE");
        hausnummer = getIntent().getStringExtra("HAUSNUMMER");
        stadt = getIntent().getStringExtra("STADT");
        land = extras.getString("LAND");


        vornameV.setText("vorname");
        nachnameV.setText(nachname);
        geburtsdatumV.setText(geburtsdatum);
        strasseV.setText(strasse);
        hausnummerV.setText(hausnummer);
        stadtV.setText(stadt);
        landV.setText(land);

    }

}
