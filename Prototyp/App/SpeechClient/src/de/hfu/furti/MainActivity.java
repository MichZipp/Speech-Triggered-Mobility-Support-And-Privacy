package de.hfu.furti;

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

import android.app.Activity;
import android.content.Intent;
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

import ai.kitt.snowboy.demo.R;

import java.util.Locale;
import java.util.Map;

import de.hfu.furti.activities.*;



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
    private boolean isAskResponse = false;

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

        appContext = getApplicationContext();

        initUI();
        initHotwordDetechtion();
        initLex();

        AppResCopy.copyResFromAssetsToSD(this);

        startHotwordDetection();
    }

    private void initUI() {
        record_button = (Button) findViewById(R.id.btn_test1);
        record_button.setOnClickListener(record_button_handle);
        record_button.setEnabled(true);

        log = (TextView)findViewById(R.id.log);
        logView = (ScrollView)findViewById(R.id.logView);
    }

    private OnClickListener record_button_handle = new OnClickListener() {
        // @Override
        public void onClick(View arg0) {
            if(record_button.getText().equals(getResources().getString(R.string.btn1_start))) {
                sleep();
                startHotwordDetection();
            } else {
                stopHotwordDetection();
                sleep();
            }
        }
    };

    private void initHotwordDetechtion(){
        activeTimes = 0;
        recordingThread = new RecordingThread(handle, new AudioDataSaver());
        recordingThread.setSensitivity(0.8);
    }

    private void initLex() {
        credentialsProvider = new CognitoCredentialsProvider(
                appContext.getResources().getString(R.string.identity_id),
                Regions.fromName(appContext.getResources().getString(R.string.aws_region)));
        voiceView = (InteractiveVoiceView) findViewById(R.id.voiceInterface);
        voiceView.setInteractiveVoiceListener(this);
        voiceView.getViewAdapter().setCredentialProvider(credentialsProvider);
        voiceView.getViewAdapter().setInteractionConfig(
                new InteractionConfig(appContext.getString(R.string.bot_name),
                        appContext.getString(R.string.bot_alias)));
        voiceView.getViewAdapter().setAwsRegion(appContext.getString(R.string.aws_region));
        voiceView.setBackgroundColor(Color.TRANSPARENT);

        lexClient = new InteractionClient(appContext,
                credentialsProvider,
                Regions.EU_WEST_1,
                new InteractionConfig(appContext.getString(R.string.bot_name),
                        appContext.getString(R.string.bot_alias)));
        lexClient.setInteractionListener(this);
        lexClient.setAudioPlaybackListener(this);
        lexClient.setMicrophoneListener(this);
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
                    updateLog(" ----> Detected " + activeTimes + " times", "green");
                    // Toast.makeText(MainActivity.this, "Active "+activeTimes, Toast.LENGTH_SHORT).show();
                    showToast("Active "+activeTimes);
                    stopHotwordDetection();
                    sleep(200);
                    //voiceView.callOnClick();
                    lexClient.audioInForAudioOut(null);
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

    private void startHotwordDetection() {
        recordingThread.startRecording();
        updateLog(" ----> recording started ...", "green");
        record_button.setText(R.string.btn1_stop);
    }

    private void stopHotwordDetection() {
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

    static int MAX_LOG_LINE_NUM = 200;
    static int currLogLineNum = 0;

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

        showToast(text);
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
            lexClient.audioInForAudioOut(null);
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
        Log.d(TEST, "startedRecording");
    }

    @Override
    public void onRecordingEnd() {
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
            case R.id.createprofile:
                Intent intent = new Intent(this, CreateProfileActivity.class);
                this.startActivity(intent);
                return true;

            case R.id.editprofile:
                intent = new Intent(this, CreateProfileActivity.class);
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


