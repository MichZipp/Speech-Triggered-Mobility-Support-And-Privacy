package de.hfu.butler.activities;

import android.app.ActionBar;
import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.widget.TextView;

import ai.kitt.snowboy.demo.R;

public class ImpressumActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_impressum);

        ActionBar bar = getActionBar();
        bar.setBackgroundDrawable(new ColorDrawable(Color.BLACK));

        TextView textView = ( TextView)findViewById(R.id.textView);

        TextView linkView = (TextView)findViewById(R.id.linkView);
        linkView.setMovementMethod(LinkMovementMethod.getInstance());

    }
}
