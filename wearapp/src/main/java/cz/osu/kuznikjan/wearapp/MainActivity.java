package cz.osu.kuznikjan.wearapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.wearable.activity.WearableActivity;
import android.support.wearable.view.BoxInsetLayout;
import android.support.wearable.view.WatchViewStub;
import android.view.View;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import be.tarsos.dsp.AudioDispatcher;
import be.tarsos.dsp.AudioEvent;
import be.tarsos.dsp.AudioProcessor;
import be.tarsos.dsp.io.android.AudioDispatcherFactory;
import be.tarsos.dsp.pitch.PitchDetectionHandler;
import be.tarsos.dsp.pitch.PitchDetectionResult;
import be.tarsos.dsp.pitch.PitchProcessor;
import cz.osu.kuznikjan.pitchlibrary.NotePitchHandler;
import cz.osu.kuznikjan.pitchlibrary.NoteResult;

public class MainActivity extends WearableActivity implements PitchDetectionHandler {

    private static final SimpleDateFormat AMBIENT_DATE_FORMAT =
            new SimpleDateFormat("HH:mm", Locale.US);

    private BoxInsetLayout mContainerView;
    private TextView mTextView;
    private TextView mClockView;
    private AudioDispatcher dispatcher;

    //private MessageReceiver messageReceiver;
    private IntentFilter messageFilter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        //mContainerView = (BoxInsetLayout) findViewById(R.id.container);
        mTextView = (TextView) findViewById(R.id.text);


        startDispatcher();

        //setAmbientEnabled();

        //messageFilter = new IntentFilter(Intent.ACTION_SEND);
        //messageReceiver = new MessageReceiver();
        //LocalBroadcastManager.getInstance(this).registerReceiver(messageReceiver, messageFilter);

    }

    @Override
    public void handlePitch(PitchDetectionResult result, AudioEvent audioEvent) {
        final NoteResult noteResult = NotePitchHandler.mapPitchToNoteResult(result);

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
            if(noteResult.getPitch()!=-1){
                mTextView.setText(noteResult.getNoteFullName());
            }
            }
        });


    }

    private void startDispatcher() {
        dispatcher = AudioDispatcherFactory.fromDefaultMicrophone(11025, 1024, 512);
        PitchDetectionHandler pdh = this;
        AudioProcessor p = new PitchProcessor(PitchProcessor.PitchEstimationAlgorithm.MPM, 11025, 1024, pdh);
        dispatcher.addAudioProcessor(p);
        new Thread(dispatcher,"Audio Dispatcher").start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        dispatcher.stop();
        this.finish();
        System.exit(0);
        //LocalBroadcastManager.getInstance(this).unregisterReceiver(messageReceiver);
    }


 /*   @Override
    public void onEnterAmbient(Bundle ambientDetails) {
        super.onEnterAmbient(ambientDetails);
        updateDisplay();
    }

    @Override
    public void onUpdateAmbient() {
        super.onUpdateAmbient();
        updateDisplay();
    }

    @Override
    public void onExitAmbient() {
        updateDisplay();
        dispatcher.stop();
        super.onExitAmbient();
    }



    private void updateDisplay() {
        if (isAmbient()) {
            mTextView.setText("bla");
        } else {
            mTextView.setText("blu");
        }
    }



    public class MessageReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String message = intent.getStringExtra("message");
            mTextView.setText(message);
        }
    }*/
}



