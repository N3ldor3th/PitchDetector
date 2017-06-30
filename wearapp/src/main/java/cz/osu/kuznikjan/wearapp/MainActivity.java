package cz.osu.kuznikjan.wearapp;

import android.content.Context;
import android.content.IntentFilter;
import android.graphics.Color;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.wearable.activity.WearableActivity;
import android.support.wearable.view.BoxInsetLayout;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import org.adw.library.widgets.discreteseekbar.DiscreteSeekBar;

import java.text.SimpleDateFormat;
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

public class MainActivity extends WearableActivity implements PitchDetectionHandler{

    private static final SimpleDateFormat AMBIENT_DATE_FORMAT =
            new SimpleDateFormat("HH:mm", Locale.US);

    private BoxInsetLayout mContainerView;
    private TextView noteHz, actualHz, previousNote, nextNote, noteName, cents;
    private DiscreteSeekBar seekBar;
    private TextView mClockView;
    private AudioDispatcher dispatcher;
    private double accuracyInCents = 5.0;
    private PitchProcessor.PitchEstimationAlgorithm chosenPDA = PitchProcessor.PitchEstimationAlgorithm.MPM;


    //private MessageReceiver messageReceiver;
    private IntentFilter messageFilter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //mContainerView = (BoxInsetLayout) findViewById(R.id.container);
        noteName = (TextView) findViewById(R.id.noteName);
        previousNote = (TextView) findViewById(R.id.previousNote);
        nextNote = (TextView) findViewById(R.id.nextNote);
        actualHz = (TextView) findViewById(R.id.actualHz);
        noteHz = (TextView) findViewById(R.id.noteHz);
        cents = (TextView) findViewById(R.id.cents);
        seekBar = (DiscreteSeekBar) findViewById(R.id.seekBar);


        //getValidSampleRates();
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
                actualHz.setText(String.format("%.02f", noteResult.getPitch()) + " Hz");
                noteHz.setText(String.format("%.02f", noteResult.getNoteHz()) + " Hz");
                cents.setText(String.format("%.01f", noteResult.getNote().getDifferenceCents()) + " c");
                noteName.setText(noteResult.getNoteFullName());
                previousNote.setText(noteResult.getPreviousFullName());
                nextNote.setText(noteResult.getNextFullName());

                double d = noteResult.getNote().getDifferenceCents();
                int i = (int) d;
                seekBar.setProgress(i);

                if (noteResult.getNote().getDifferenceCents() >= -accuracyInCents && noteResult.getNote().getDifferenceCents() < accuracyInCents) {
                    seekBar.setThumbColor(Color.GREEN, Color.GREEN);
                }else {
                    seekBar.setThumbColor(Color.RED, Color.RED);
                }
            }
            }
        });
    }

    public void getValidSampleRates() {
        for (int rate : new int[] {8000, 11025, 16000, 22050, 44100}) {  // add the rates you wish to check against
            int bufferSize = AudioRecord.getMinBufferSize(rate, AudioFormat.CHANNEL_CONFIGURATION_DEFAULT, AudioFormat.ENCODING_PCM_16BIT);
            if (bufferSize > 0) {
                System.out.println("Value: " + rate);

            }
        }
        // To get preferred buffer size and sampling rate.
        AudioManager audioManager = (AudioManager) this.getSystemService(Context.AUDIO_SERVICE);
        String rate = audioManager.getProperty(AudioManager.PROPERTY_OUTPUT_SAMPLE_RATE);
        String size = audioManager.getProperty(AudioManager.PROPERTY_OUTPUT_FRAMES_PER_BUFFER);
        System.out.println("Size : + " + size + " & Rate: " + rate);
    }



    private void startDispatcher() {
        dispatcher = AudioDispatcherFactory.fromDefaultMicrophone(8000, 1024, 0);
        PitchDetectionHandler pdh = this;
        AudioProcessor p = new PitchProcessor(chosenPDA, 8000, 1024, pdh);
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
            noteName.setText("bla");
        } else {
            noteName.setText("blu");
        }
    }



    public class MessageReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String message = intent.getStringExtra("message");
            noteName.setText(message);
        }
    }*/
}



