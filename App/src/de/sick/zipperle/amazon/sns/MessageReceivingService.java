package de.sick.zipperle.amazon.sns;

import java.io.IOException;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;

import com.amazonaws.auth.CognitoCredentialsProvider;
import com.amazonaws.regions.Regions;
import com.google.android.gms.gcm.GoogleCloudMessaging;

import de.sick.zipperle.amazon.sns.datastructure.GCMMessage;
import de.sick.zipperle.android.R;

/*
 * This service is designed to run in the background and receive messages from gcm. If the app is in the foreground
 * when a message is received, it will immediately be posted. If the app is not in the foreground, the message will be saved
 * and a notification is posted to the NotificationManager.
 */
public class MessageReceivingService extends Service{
    private GoogleCloudMessaging gcm;
    public static SharedPreferences savedValues;

    private static final String userID = "selfcreateduser-1";

    private static final String TAG = "MessageReceiver";

    private static Context appContext;

    protected static MessageListener listener = null;

    @Override
    public void onCreate(){
        super.onCreate();
        appContext = this.getApplicationContext();
        final String preferences = getString(R.string.preferences);
        savedValues = getSharedPreferences(preferences, Context.MODE_PRIVATE);
        // In later versions multi_process is no longer the default
        if(VERSION.SDK_INT >  9){
            savedValues = getSharedPreferences(preferences, Context.MODE_MULTI_PROCESS);
        }
        gcm = GoogleCloudMessaging.getInstance(getBaseContext());
        SharedPreferences savedValues = PreferenceManager.getDefaultSharedPreferences(this);
        if(savedValues.getBoolean(getString(R.string.first_launch), true)){
            register();
            SharedPreferences.Editor editor = savedValues.edit();
            editor.putBoolean(getString(R.string.first_launch), false);
            editor.commit();
        }else{
            String token = savedValues.getString(getString(R.string.gcm_registration_id), "1234");
            Log.i("registrationId", token);
        }
    }

    protected static GCMMessage parseNotification(Bundle extras, Context context){
        GCMMessage message = new GCMMessage();

        for(String key : extras.keySet()){
            if(key.equals("google.sent_time")){
                message.setSendTime(extras.getLong(key));
            }else if (key.equals("from")){
                message.setSender(extras.getString(key));
            }else if (key.equals("google.message_id")){
                message.setMessageId(extras.getString(key));
            }else if (key.equals("default")){
                message.setMessage(extras.getString(key));
            }
        }

        Log.i(TAG, message.toString());

        return message;
    }

    protected static void notifyUser(final GCMMessage message){
        // Check if the receiver of the message is the user of this application
        if(userID.equals(message.getMessage())){
            callMessageListener(message);
        }
    }

    private static void callMessageListener(final GCMMessage message){
        if(listener != null){
            listener.newMessage(message);
        }else{
            Log.e(TAG, "MessageListener is not set!");
        }
    }

    public static void setMessageListener(final MessageListener list){
        listener = list;
    }

    private void register() {
        new AsyncTask(){
            protected Object doInBackground(final Object... params) {
                String token;
                try {
                    token = gcm.register(getString(R.string.project_number));
                    SharedPreferences.Editor editor = savedValues.edit();
                    editor.putString(getString(R.string.gcm_registration_id), token);
                    editor.commit();
                    Log.i("registrationId", token);
                } 
                catch (IOException e) {
                    Log.i("Registration Error", e.getMessage());
                }
                return true;
            }
        }.execute(null, null, null);
    }

    public IBinder onBind(Intent arg0) {
        return null;
    }

}