package ai.kitt.snowboy.Activities;

import android.app.Activity;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.widget.TextView;

import ai.kitt.snowboy.demo.R;

public class ImpressumActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_impressum);

        TextView textView = ( TextView)findViewById(R.id.textView);

        TextView linkView = (TextView)findViewById(R.id.linkView);
        linkView.setMovementMethod(LinkMovementMethod.getInstance());

    }
}
