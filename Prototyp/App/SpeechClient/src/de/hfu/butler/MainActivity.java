package de.hfu.butler;

import ai.kitt.snowboy.AppResCopy;
import ai.kitt.snowboy.MsgEnum;
import ai.kitt.snowboy.audio.RecordingThread;
import ai.kitt.snowboy.audio.AudioDataSaver;

import com.amazonaws.auth.CognitoCredentialsProvider;
import com.amazonaws.mobileconnectors.lex.interactionkit.InteractionClient;
import com.amazonaws.mobileconnectors.lex.interactionkit.Response;
import com.amazonaws.mobileconnectors.lex.interactionkit.config.InteractionConfig;
import com.amazonaws.mobileconnectors.lex.interactionkit.continuations.LexServiceContinuation;
import com.amazonaws.mobileconnectors.lex.interactionkit.listeners.AudioPlaybackListener;
import com.amazonaws.mobileconnectors.lex.interactionkit.listeners.InteractionListener;
import com.amazonaws.mobileconnectors.lex.interactionkit.listeners.MicrophoneListener;
import com.amazonaws.mobileconnectors.lex.interactionkit.ui.InteractiveVoiceView;
import com.amazonaws.regions.Regions;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.os.Handler;
import android.os.Message;
import android.media.AudioManager;
import android.content.Context;
import android.util.Log;
import android.graphics.Color;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import android.text.Html;
import android.view.View.OnClickListener;
import android.view.View;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import de.hfu.butler.activities.*;
import de.hfu.butler.service.SessionStorage;

public class MainActivity extends Activity implements InteractionListener, InteractiveVoiceView.InteractiveVoiceListener, MicrophoneListener, AudioPlaybackListener {
    private Button record_button;
    private Button play_button;
    private TextView log;
    private ScrollView logView;
    static String strLog = null;

    private InteractiveVoiceView voiceView = null;
    private static InteractionClient lexClient;

    private static final String TAG = "MainActivity";
    private static final String TEST = "TEST";

    private int preVolume = -1;
    private static long activeTimes = 0;
    private RecordingThread recordingThread = null;

    private CognitoCredentialsProvider credentialsProvider = null;

    private final String DIALOGSTATEFULFILLED = "Fulfilled";
    private final String DIALOGSTATEELICTINTENT = "ElicitIntent";
    private final String DIALOGSTATEELICTSLOT = "ElicitSlot";

    private Context appContext = null;

    private boolean isPlaying = false;
    private boolean isRecording = false;
    private boolean isAskResponse = false;

    private SessionStorage storage = null;

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

        ActionBar bar = getActionBar();
        // bar.setTitle("Men�:");
        bar.setBackgroundDrawable(new ColorDrawable(Color.GRAY));

        appContext = getApplicationContext();

        storage = SessionStorage.getInstance();

        initUI();

        AppResCopy.copyResFromAssetsToSD(this);

        initHotwordDetechtion();
        initLex();
        startHotwordDetection();
    }

    private void initUI() {
        record_button = (Button) findViewById(R.id.btn_test1);
        record_button.setOnClickListener(new OnClickListener() {
            // @Override
            public void onClick(View arg0) {
                if(record_button.getText().equals(getResources().getString(R.string.btn1_start))) {
                    sleep();
                    startHotwordDetection();
                } else {
                    lexClient.cancel();
                    stopHotwordDetection();
                    sleep();
                }
            }
        });

        record_button.setEnabled(true);
        log = (TextView)findViewById(R.id.log);
        logView = (ScrollView)findViewById(R.id.logView);
    }

    private void initHotwordDetechtion(){
        activeTimes = 0;
        recordingThread = new RecordingThread(handle, new AudioDataSaver());
        recordingThread.setSensitivity(0.5);
    }

    private void initLex() {
        credentialsProvider = new CognitoCredentialsProvider(
                appContext.getResources().getString(R.string.identity_id),
                Regions.fromName(appContext.getResources().getString(R.string.aws_region)));

        lexClient = new InteractionClient(appContext,
                credentialsProvider,
                Regions.EU_WEST_1,
                new InteractionConfig(appContext.getString(R.string.bot_name),
                        appContext.getString(R.string.bot_alias)));
        lexClient.setInteractionListener(this);
        lexClient.setAudioPlaybackListener(this);
        lexClient.setMicrophoneListener(this);

        voiceView = (InteractiveVoiceView) findViewById(R.id.voiceInterface);
        voiceView.setInteractiveVoiceListener(this);
        voiceView.getViewAdapter().setCredentialProvider(credentialsProvider);
        voiceView.getViewAdapter().setInteractionConfig(
                new InteractionConfig(appContext.getString(R.string.bot_name),
                        appContext.getString(R.string.bot_alias)));
        voiceView.getViewAdapter().setAwsRegion(appContext.getString(R.string.aws_region));
        voiceView.setBackgroundColor(Color.TRANSPARENT);

        voiceView.setOnClickListener(new OnClickListener() {
            // @Override
            public void onClick(View arg0) {
                Log.i(TAG, "isRecording: " + isRecording);
                if(isRecording == false){
                    stopHotwordDetection();
                    isRecording = true;
                    // Get userID and access_token from storage
                    String user_id = storage.getUserId();
                    String access_token = storage.getSessionToken();

                    // Pass AccessToken as sessionAttribute
                    Map<String, String> sessionAttributes = new HashMap();
                    sessionAttributes.put("accessToken", access_token);
                    sessionAttributes.put("userId", user_id);
                    lexClient.audioInForAudioOut(sessionAttributes);
                }else{
                    lexClient.cancel();
                    isRecording = false;
                    startHotwordDetection();
                }
                Log.i(TAG, "Microphone Click");
            }
        });
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

    public Handler handle = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            MsgEnum message = MsgEnum.getMsgEnum(msg.what);
            switch(message) {
                case MSG_ACTIVE:
                    activeTimes++;
                    Log.i(TAG," ----> Detected " + activeTimes + " times");
                    // Toast.makeText(MainActivity.this, "Active "+activeTimes, Toast.LENGTH_SHORT).show();
                    showToast("Active "+activeTimes);
                    stopHotwordDetection();
                    isRecording = true;
                    sleep(200);
                    //voiceView.callOnClick();

                    // Get userID and access_token from storage
                    String user_id = storage.getUserId();
                    String access_token = storage.getSessionToken();

                    // Pass AccessToken as sessionAttribute
                    Map<String, String> sessionAttributes = new HashMap();
                    sessionAttributes.put("accessToken", access_token);
                    sessionAttributes.put("userId", user_id);
                    lexClient.audioInForAudioOut(sessionAttributes);
                    break;
                case MSG_INFO:
                    Log.i(TAG," ----> "+message);
                    break;
                case MSG_VAD_SPEECH:
                    Log.i(TAG," ----> normal voice");
                    break;
                case MSG_VAD_NOSPEECH:
                    Log.i(TAG," ----> no speech");
                    break;
                case MSG_ERROR:
                    Log.i(TAG," ----> " + msg.toString());
                    break;
                default:
                    super.handleMessage(msg);
                    break;
             }
        }
    };

        private void setMaxVolume() {
        AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        preVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        Log.i(TAG," ----> preVolume = "+preVolume);
        int maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        Log.i(TAG," ----> maxVolume = "+maxVolume);
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, maxVolume, 0);
        int currentVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        Log.i(TAG," ----> currentVolume = "+currentVolume);
    }

    private void setProperVolume() {
        AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        preVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        Log.i(TAG," ----> preVolume = "+preVolume);
        int maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        Log.i(TAG," ----> maxVolume = "+maxVolume);
        int properVolume = (int) ((float) maxVolume * 0.2);
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, properVolume, 0);
        int currentVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        Log.i(TAG," ----> currentVolume = "+currentVolume);
    }

    private void restoreVolume() {
        if(preVolume>=0) {
            AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
            audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, preVolume, 0);
            Log.i(TAG," ----> set preVolume = "+preVolume);
            int currentVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
            Log.i(TAG," ----> currentVolume = "+currentVolume);
        }
    }

    private void startHotwordDetection() {
        isRecording = false;
        recordingThread.startRecording();
        Log.i(TAG," ----> recording started ...");
        record_button.setText(R.string.btn1_stop);
    }

    private void stopHotwordDetection() {
        recordingThread.stopRecording();
        Log.i(TAG," ----> recording stopped ");
        record_button.setText(R.string.btn1_start);
    }

    private void sleep(final int time) {
        try { Thread.sleep(time);
        } catch (Exception e) {}
    }

    private void sleep() {
        sleep(500);
    }

    static int MAX_LOG_LINE_NUM = 200;
    static int currLogLineNum = 0;

    public void updateLog(final String text) {
        final String response = "- " + text;
        log.post(new Runnable() {
             @Override
             public void run() {
                 if (currLogLineNum >= MAX_LOG_LINE_NUM) {
                     int st = strLog.indexOf("<br>");
                     strLog = strLog.substring(st+4);
                 } else {
                     currLogLineNum++;
                 }
                 String str = "<font color='white'>"+response+"</font>"+"<br>";
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

    private void doReadyForFulfillment(final Map<String, String> slots, final String intent){
        Log.d(TAG, String.format(
                Locale.US,
                "Dialog ready for fulfillment:\n\tIntent: %s\n\tSlots: %s",
                intent,
                slots.toString()));
    }

    private void doResponse(final Response response){
        Log.d(TAG, "Bot response: " + response.getTextResponse());
        Log.d(TAG, "Transcript: " + response.getInputTranscript());

        String text = response.getTextResponse();
        Log.d(TAG, "DialogState: " + response.getDialogState());

        // If the Dialog is fulfilled, the app should continue to listen to the user input
        if (response.getDialogState().equals(DIALOGSTATEFULFILLED)) {
            Map<String, String> sessionAttributes = response.getSessionAttributes();
            Log.d(TEST, sessionAttributes.toString());
            if(Boolean.parseBoolean(sessionAttributes.get("isAskResponse"))){
                isAskResponse = true;
            }else{
                isAskResponse = false;
                startedRecording();
            }
        }else if(response.getDialogState().equals(DIALOGSTATEELICTINTENT) || response.getDialogState().equals(DIALOGSTATEELICTSLOT)){
            isAskResponse = true;
        }

        updateLog(text);
    }

    private void doInteractionError(final Exception e){
        Log.e(TAG, "Interaction error", e);
    }

    @Override
    public void onReadyForFulfillment(final Response response) {
        doReadyForFulfillment(response.getSlots(), response.getIntentName());
    }

    @Override
    public void promptUserToRespond(final Response response, final LexServiceContinuation continuation) {
        doResponse(response);
    }

    @Override
    public void onInteractionError(final Response response, final Exception e) {
        doInteractionError(e);
    }

    @Override
    public void dialogReadyForFulfillment(Map<String, String> slots, String intent) {
        doReadyForFulfillment(slots, intent);
    }

    @Override
    public void onResponse(Response response) {
        doResponse(response);
    }

    @Override
    public void onError(String responseText, Exception e) {
        doInteractionError(e);
    }

    @Override
    public void onAudioPlaybackStarted() {
        Log.d(TEST, "onAudioPlaybackStarted");
        voiceView.animateNone();
        isPlaying = true;
    }

    @Override
    public void onAudioPlayBackCompleted() {
        Log.d(TEST, "onAudioPlayBackCompleted");
        isPlaying = false;
        if(isAskResponse){
            // Get userID and access_token from storage
            String user_id = storage.getUserId();
            String access_token = storage.getSessionToken();

            // Pass AccessToken as sessionAttribute
            Map<String, String> sessionAttributes = new HashMap();
            sessionAttributes.put("accessToken", access_token);
            sessionAttributes.put("userId", user_id);
            lexClient.audioInForAudioOut(sessionAttributes);
        }else{
            startHotwordDetection();
        }
    }

    @Override
    public void onAudioPlaybackError(Exception e) {
        Log.d(TEST, "onAudioPlaybackErrors");
    }

    @Override
    public void readyForRecording() {
        Log.d(TEST, "readyForRecording");
    }

    @Override
    public void startedRecording() {
        isRecording = true;
        Log.d(TEST, "startedRecording");
    }

    @Override
    public void onRecordingEnd() {
        isRecording = false;
        Log.d(TEST, "onRecordingEnd");
        voiceView.animateWaitSpinner();
    }

    @Override
    public void onSoundLevelChanged(double soundLevel) {
        Log.d(TEST, "onSoundLevelChanged");
        voiceView.animateSoundLevel((float) soundLevel);
    }

    @Override
    public void onMicrophoneError(Exception e) {
        Log.d(TEST, "onMicrophoneError: " + e.getMessage());
        startHotwordDetection();
    }

    // Menu options to load other activity
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.profile:
                Intent intent = new Intent(getApplicationContext(), ProfileActivity.class);
                getApplicationContext().startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}


