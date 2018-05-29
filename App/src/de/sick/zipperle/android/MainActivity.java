package de.sick.zipperle.android;

import ai.kitt.snowboy.AppResCopy;
import ai.kitt.snowboy.MsgEnum;
import ai.kitt.snowboy.audio.RecordingThread;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import android.content.Context;

import ai.kitt.snowboy.audio.AudioDataSaver;

public class MainActivity extends Activity {

    private Button record_button;
    private Button play_button;
    private TextView log;
    private ScrollView logView;
    static String strLog = null;

    private int preVolume = -1;
    private static long activeTimes = 0;

    private RecordingThread recordingThread = null;
    //private PollyPlaybackThread pollyPlaybackThread = null;

    private Context appContext = null;

    private static final String TAG = "VoiceActivity";
    private static final String TEST = "TEST";

    private final String DIALOGSTATEFULFILLED = "Fulfilled";
    private final String DIALOGSTATEELICTINTENT = "ElicitIntent";
    private final String DIALOGSTATEELICTSLOT = "ElicitSlot";

    private SharedPreferences savedValues;
    private String numOfMissedMessages;

    // Since this activity is SingleTop, there can only ever be one instance. This variable corresponds to this instance.
    public static Boolean inBackground = true;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.main);
        setUI();

        appContext = getApplicationContext();

        AppResCopy.copyResFromAssetsToSD(this);

        activeTimes = 0;
        recordingThread = new RecordingThread(handle, new AudioDataSaver());

        //MessageReceivingService.setMessageListener(this);

        startRecording();
    }

    @Override
    public void onNewIntent(Intent intent){
        super.onNewIntent(intent);
        setIntent(intent);
    }

    @Override
    public void onDestroy() {
        restoreVolume();
        recordingThread.stopRecording();
        super.onDestroy();
    }

    void showToast(CharSequence msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }
    
    private void setUI() {
        record_button = (Button) findViewById(R.id.btn_test1);
        record_button.setOnClickListener(record_button_handle);
        record_button.setEnabled(true);

        log = (TextView)findViewById(R.id.log);
        logView = (ScrollView)findViewById(R.id.logView);
    }

    private void setMaxVolume() {
        AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        preVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        updateLog(" ----> preVolume = "+preVolume, "green");
        int maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        updateLog(" ----> maxVolume = "+maxVolume, "green");
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, maxVolume, 0);
        int currentVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        updateLog(" ----> currentVolume = "+currentVolume, "green");
    }

    private void setProperVolume() {
        AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        preVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        updateLog(" ----> preVolume = "+preVolume, "green");
        int maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        updateLog(" ----> maxVolume = "+maxVolume, "green");
        int properVolume = (int) ((float) maxVolume * 0.2);
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, properVolume, 0);
        int currentVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        updateLog(" ----> currentVolume = "+currentVolume, "green");
    }

    private void restoreVolume() {
        if(preVolume>=0) {
            AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
            audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, preVolume, 0);
            updateLog(" ----> set preVolume = "+preVolume, "green");
            int currentVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
            updateLog(" ----> currentVolume = "+currentVolume, "green");
        }
    }

    private void startRecording() {
        recordingThread.startRecording();
        updateLog(" ----> recording started ...", "green");
        record_button.setText(R.string.btn1_stop);
    }

    private void stopRecording() {
        recordingThread.stopRecording();
        updateLog(" ----> recording stopped ", "green");
        record_button.setText(R.string.btn1_start);
    }

    private void sleep(final int time) {
        try { Thread.sleep(time);
        } catch (Exception e) {}
    }

    private void sleep() {
        sleep(500);
    }
    
    private OnClickListener record_button_handle = new OnClickListener() {
        // @Override
        public void onClick(View arg0) {
            if(record_button.getText().equals(getResources().getString(R.string.btn1_start))) {
                sleep();
                startRecording();
            } else {
                stopRecording();
                sleep();
            }
        }
    };
     
    public Handler handle = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            MsgEnum message = MsgEnum.getMsgEnum(msg.what);
            switch(message) {
                case MSG_ACTIVE:
                    activeTimes++;
                    updateLog(" ----> Detected " + activeTimes + " times", "green");
                    // Toast.makeText(MainActivity.this, "Active "+activeTimes, Toast.LENGTH_SHORT).show();
                    showToast("Active "+activeTimes);
                    sleep(200);
                    break;
                case MSG_INFO:
                    updateLog(" ----> "+message);
                    break;
                case MSG_VAD_SPEECH:
                    updateLog(" ----> normal voice", "blue");
                    break;
                case MSG_VAD_NOSPEECH:
                    updateLog(" ----> no speech", "blue");
                    break;
                case MSG_ERROR:
                    updateLog(" ----> " + msg.toString(), "red");
                    break;
                default:
                    super.handleMessage(msg);
                    break;
             }
        }
    };

     public void updateLog(final String text) {

         log.post(new Runnable() {
             @Override
             public void run() {
                 if (currLogLineNum >= MAX_LOG_LINE_NUM) {
                     int st = strLog.indexOf("<br>");
                     strLog = strLog.substring(st+4);
                 } else {
                     currLogLineNum++;
                 }
                 String str = "<font color='white'>"+text+"</font>"+"<br>";
                 strLog = (strLog == null || strLog.length() == 0) ? str : strLog + str;
                 log.setText(Html.fromHtml(strLog));
             }
        });
        logView.post(new Runnable() {
            @Override
            public void run() {
                logView.fullScroll(ScrollView.FOCUS_DOWN);
            }
        });
    }

    static int MAX_LOG_LINE_NUM = 200;
    static int currLogLineNum = 0;

    public void updateLog(final String text, final String color) {
        log.post(new Runnable() {
            @Override
            public void run() {
                if(currLogLineNum>=MAX_LOG_LINE_NUM) {
                    int st = strLog.indexOf("<br>");
                    strLog = strLog.substring(st+4);
                } else {
                    currLogLineNum++;
                }
                String str = "<font color='"+color+"'>"+text+"</font>"+"<br>";
                strLog = (strLog == null || strLog.length() == 0) ? str : strLog + str;
                log.setText(Html.fromHtml(strLog));
            }
        });
        logView.post(new Runnable() {
            @Override
            public void run() {
                logView.fullScroll(ScrollView.FOCUS_DOWN);
            }
        });
    }

    private void emptyLog() {
        strLog = null;
        log.setText("");
    }


    private void doInteractionError(final Exception e){
        Log.e(TAG, "Interaction error", e);
    }
}
