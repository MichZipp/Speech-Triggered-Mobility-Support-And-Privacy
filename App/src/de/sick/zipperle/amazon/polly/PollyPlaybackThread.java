package de.sick.zipperle.amazon.polly;

import android.media.AudioManager;
import android.media.AudioTrack;
import android.media.MediaPlayer;
import android.util.Log;

import com.amazonaws.auth.CognitoCredentialsProvider;
import com.amazonaws.services.polly.AmazonPollyPresigningClient;
import com.amazonaws.services.polly.model.OutputFormat;
import com.amazonaws.services.polly.model.SynthesizeSpeechPresignRequest;
import com.amazonaws.services.polly.model.VoiceId;

import java.io.IOException;
import java.net.URL;

/**
 * Created by michael on 12.12.17.
 */

public class PollyPlaybackThread {
    private static final String TAG = PollyPlaybackThread.class.getSimpleName();

    private AmazonPollyPresigningClient client = null;
    private MediaPlayer mediaPlayer = null;

    private String outputText = "";

    public PollyPlaybackThread() {
    }

    private Thread thread;
    private boolean shouldContinue;
    protected AudioTrack audioTrack;

    public boolean isPlaying() {
        return thread != null;
    }

    public void startPlayback(final String outputText) {
        this.outputText = outputText;

        if (thread != null || this.outputText.isEmpty())
            return;

        // Start streaming in a thread
        shouldContinue = true;
        thread = new Thread(new Runnable() {
            @Override
            public void run() {
                play();
            }
        });
        thread.start();
    }

    public void stopPlayback() {
        if (thread == null)
            return;

        shouldContinue = false;
        mediaPlayer.stop();
        mediaPlayer.release();
        thread = null;
    }

    public void initPolly(final CognitoCredentialsProvider credentialsProvider) {
        // Create a client that supports generation of presigned URLs.
        client = new AmazonPollyPresigningClient(credentialsProvider);

        setupNewMediaPlayer();
    }

    private void setupNewMediaPlayer(){
        // Create Media Player
        mediaPlayer = new MediaPlayer();
    }


    private void play() {
        // Create speech synthesis request.
        SynthesizeSpeechPresignRequest synthesizeSpeechPresignRequest =
                new SynthesizeSpeechPresignRequest()
                        // Set text to synthesize.
                        .withText(outputText)
                        // Set voice selected by the user.
                        .withVoiceId(VoiceId.Carmen)
                        // Set format to MP3.
                        .withOutputFormat(OutputFormat.Mp3);

        // Get the presigned URL for synthesized speech audio stream.
        URL presignedSynthesizeSpeechUrl =
                client.getPresignedSynthesizeSpeechUrl(synthesizeSpeechPresignRequest);

        Log.i(TAG, "Playing speech from presigned URL: " + presignedSynthesizeSpeechUrl);

        // Create a media player to play the synthesized audio stream.
        if (mediaPlayer.isPlaying()) {
            setupNewMediaPlayer();
        }
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

        try {
            // Set media player's data source to previously obtained URL.
            mediaPlayer.setDataSource(presignedSynthesizeSpeechUrl.toString());
        } catch (IOException e) {
            Log.e(TAG, "Unable to set data source for the media player! " + e.getMessage());
        }

        // Start the playback asynchronously (since the data source is a network stream).
        mediaPlayer.prepareAsync();
    }
}
