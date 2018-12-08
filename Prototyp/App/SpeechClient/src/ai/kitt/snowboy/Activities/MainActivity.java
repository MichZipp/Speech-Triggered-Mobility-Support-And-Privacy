package ai.kitt.snowboy.Activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import ai.kitt.snowboy.Demo;
import ai.kitt.snowboy.demo.R;



public class MainActivity extends Activity {

    TextView TextView;

        @Override
        public boolean onCreateOptionsMenu(Menu menu) {
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.menu, menu);
            return true;
        }

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_main);

            TextView = (TextView);

        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            // Handle item selection
            switch (item.getItemId()) {
                case R.id.createprofile:
                    Intent intent = new Intent(this, CreateProfileActivity.class);
                    this.startActivity(intent);
                    return true;

                case R.id.editprofile:
                    intent = new Intent(this, CreateProfileActivity.class);
                    this.startActivity(intent);
                    return true;

                case R.id.hotword:
                    intent = new Intent(this, Demo.class);
                    this.startActivity(intent);
                    return true;

                case R.id.help:
                    intent = new Intent(this, ImpressumActivity.class);
                    this.startActivity(intent);
                    return true;

                case R.id.impressum:
                    intent = new Intent(this, ImpressumActivity.class);
                    this.startActivity(intent);
                    return true;

                default:
                    return super.onOptionsItemSelected(item);
            }
        }

    }


