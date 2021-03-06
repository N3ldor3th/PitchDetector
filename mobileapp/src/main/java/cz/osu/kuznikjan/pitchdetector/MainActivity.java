package cz.osu.kuznikjan.pitchdetector;

import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.Wearable;

import org.adw.library.widgets.discreteseekbar.DiscreteSeekBar;

import java.util.List;

import be.tarsos.dsp.AudioDispatcher;
import be.tarsos.dsp.AudioEvent;
import be.tarsos.dsp.AudioProcessor;
import be.tarsos.dsp.io.android.AudioDispatcherFactory;
import be.tarsos.dsp.pitch.PitchDetectionHandler;
import be.tarsos.dsp.pitch.PitchDetectionResult;
import be.tarsos.dsp.pitch.PitchProcessor;
import cz.osu.kuznikjan.pitchlibrary.NotePitchHandler;
import cz.osu.kuznikjan.pitchlibrary.NoteResult;
import cz.osu.kuznikjan.pitchlibrary.db.NoteResultDB;
import cz.osu.kuznikjan.pitchlibrary.db.NoteResultMapper;

public class MainActivity extends AppCompatActivity implements PitchDetectionHandler, View.OnClickListener, AdapterView.OnItemSelectedListener,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    private static final int MY_PERMISSIONS_REQUEST_RECORD_AUDIO = 1;
    private double accuracyInCents = 5.0;
    private PitchProcessor.PitchEstimationAlgorithm chosenPDA = PitchProcessor.PitchEstimationAlgorithm.MPM;
    private AudioDispatcher dispatcher;
    private Spinner spinner;
    private TextView textHertz, previousNote, nextNote, textTone, noteHz, textClosenessHz, textClosenessCents;
    private ImageButton fabRecord,fabStop;
    private DiscreteSeekBar seekBar;
    private GoogleApiClient mGoogleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        NoteResultDB.deleteAll(NoteResultDB.class);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        spinner = (Spinner) findViewById(R.id.pdas);
        textHertz = (TextView) findViewById(R.id.hz);
        previousNote = (TextView) findViewById(R.id.previousNote);
        nextNote = (TextView) findViewById(R.id.nextNote);
        textTone = (TextView) findViewById(R.id.note);
        noteHz = (TextView) findViewById(R.id.noteHz);
        textClosenessHz = (TextView) findViewById(R.id.closenessHz);
        textClosenessCents = (TextView) findViewById(R.id.closenessCents);
        fabRecord = (ImageButton) findViewById(R.id.fab_record);
        fabStop = (ImageButton) findViewById(R.id.fab_stop);
        fabRecord.setOnClickListener(this);
        fabStop.setOnClickListener(this);
        seekBar = (DiscreteSeekBar) findViewById(R.id.seekBar);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.pdas, android.R.layout.simple_list_item_1);
        adapter.setDropDownViewResource(android.R.layout.simple_list_item_1);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.RECORD_AUDIO)) {
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.RECORD_AUDIO},
                        MY_PERMISSIONS_REQUEST_RECORD_AUDIO);
            }
        }

        startDispatcher();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Wearable.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

        Snackbar.make(view, "Pitch Detection algorithm chosen: " + spinner.getSelectedItem().toString(), Snackbar.LENGTH_SHORT).setAction("Action", null).show();

        switch(position){
            case 0:
                dispatcher.stop();
                chosenPDA = PitchProcessor.PitchEstimationAlgorithm.MPM;
                startDispatcher();
                break;
            case 1:
                dispatcher.stop();
                chosenPDA = PitchProcessor.PitchEstimationAlgorithm.DYNAMIC_WAVELET;
                startDispatcher();
                break;
            case 2:
                dispatcher.stop();
                chosenPDA = PitchProcessor.PitchEstimationAlgorithm.YIN;
                startDispatcher();
                break;
            case 3:
                dispatcher.stop();
                chosenPDA = PitchProcessor.PitchEstimationAlgorithm.FFT_YIN;
                startDispatcher();
                break;
            case 4:
                dispatcher.stop();
                chosenPDA = PitchProcessor.PitchEstimationAlgorithm.AMDF;
                startDispatcher();
                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    private void startDispatcher() {
        dispatcher = AudioDispatcherFactory.fromDefaultMicrophone(44100, 4096, 2048);
        PitchDetectionHandler pdh = this;
        AudioProcessor p = new PitchProcessor(chosenPDA, 44100, 4096, pdh);
        dispatcher.addAudioProcessor(p);
        new Thread(dispatcher,"Audio Dispatcher").start();
    }

    @Override
    public void handlePitch(PitchDetectionResult result, AudioEvent audioEvent) {
        final NoteResult noteResult = NotePitchHandler.mapPitchToNoteResult(result);
        //new DataLayerThread(mGoogleApiClient,"/message_path", Float.toString(pitchInHz)).start();
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                NoteResultDB noteResultDB = NoteResultMapper.mapNoteResult(noteResult);
                noteResultDB.save();

                if ((noteResult.getPitch() != -1)) {
                    textHertz.setText(String.format("%.02f", noteResult.getPitch()) + " Hz");
                    textClosenessHz.setText(String.format("%.02f", noteResult.getNote().getDifferenceHz()) + " Hz");
                    noteHz.setText(String.format("%.02f", noteResult.getNoteHz()) + " Hz");
                    textClosenessCents.setText(String.format("%.01f", noteResult.getNote().getDifferenceCents()) + " c");
                    textTone.setText(noteResult.getNoteFullName());
                    previousNote.setText(noteResult.getPreviousFullName());
                    nextNote.setText(noteResult.getNextFullName());

                    setCentsToProgressBar(noteResult.getNote().getDifferenceCents());

                    if (noteResult.getNote().getDifferenceCents() >= -accuracyInCents && noteResult.getNote().getDifferenceCents() < accuracyInCents) {
                        seekBar.setThumbColor(Color.GREEN, Color.GREEN);
                    }else {
                        seekBar.setThumbColor(Color.RED, Color.RED);
                    }
                }
            }
        });
    }

    @Override
    public void onClick(View view) {
        final String stopped = "Recording stopped...";
        final String started = "Recording started...";

        switch(view.getId()){
            case R.id.fab_stop:
                Snackbar.make(view, stopped, Snackbar.LENGTH_LONG).setAction("Action", null).show();
                dispatcher.stop();
                printAllRecords();
                view.setVisibility(View.GONE);
                fabRecord.setVisibility(View.VISIBLE);
                //new DataLayerThread(mGoogleApiClient,"/message_path", stopped).start();
                break;
            case R.id.fab_record:
                Snackbar.make(view, started, Snackbar.LENGTH_LONG).setAction("Action", null).show();
                startDispatcher();
                view.setVisibility(View.GONE);
                fabStop.setVisibility(View.VISIBLE);
                //new DataLayerThread(mGoogleApiClient,"/message_path", started).start();
                break;
            default:
                break;
        }
    }

    public void printAllRecords(){
        try {
            List<NoteResultDB> notes = NoteResultDB.listAll(NoteResultDB.class);
            for (NoteResultDB note: notes) {
                System.out.println(note.getPitch());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setCentsToProgressBar(double differenceCents) {
        double d = differenceCents;
        int i = (int) d;
        seekBar.setProgress(i);
    }

    @Override
    public void onConnected(Bundle connectionHint) {
        String message = "Hello wearable\n Devices are paired";
        new DataLayerThread(mGoogleApiClient,"/message_path", message).start();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (null != mGoogleApiClient && mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (null != mGoogleApiClient && mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    public void getValidSampleRates() {
        for (int rate : new int[] {8000, 11025, 16000, 22050, 44100}) {  // add the rates you wish to check against
            int bufferSize = AudioRecord.getMinBufferSize(rate, AudioFormat.CHANNEL_CONFIGURATION_DEFAULT, AudioFormat.ENCODING_PCM_16BIT);
            if (bufferSize > 0) {
                System.out.println("Value: " + rate);

            }
        }
        AudioManager audioManager = (AudioManager) this.getSystemService(Context.AUDIO_SERVICE);
        String rate = audioManager.getProperty(AudioManager.PROPERTY_OUTPUT_SAMPLE_RATE);
        String size = audioManager.getProperty(AudioManager.PROPERTY_OUTPUT_FRAMES_PER_BUFFER);
        System.out.println("Size : + " + size + " & Rate: " + rate);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }
}
