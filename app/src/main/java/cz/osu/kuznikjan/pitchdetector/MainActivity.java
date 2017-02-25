package cz.osu.kuznikjan.pitchdetector;

import android.content.pm.PackageManager;
import android.graphics.Color;
import android.media.AudioFormat;
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
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.Wearable;

import org.adw.library.widgets.discreteseekbar.DiscreteSeekBar;

import be.tarsos.dsp.AudioDispatcher;
import be.tarsos.dsp.AudioEvent;
import be.tarsos.dsp.AudioProcessor;
import be.tarsos.dsp.io.android.AudioDispatcherFactory;
import be.tarsos.dsp.pitch.PitchDetectionHandler;
import be.tarsos.dsp.pitch.PitchDetectionResult;
import be.tarsos.dsp.pitch.PitchProcessor;

public class MainActivity extends AppCompatActivity implements PitchDetectionHandler, View.OnClickListener,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    private static final int MY_PERMISSIONS_REQUEST_RECORD_AUDIO = 1;
    private AudioDispatcher dispatcher;
    private TextView textHertz, textProb, textTone, textClosenessPercent, textClosenessHz, textClosenessCents;
    private ImageButton fabRecord,fabStop;
    private DiscreteSeekBar seekBar;
    private GoogleApiClient mGoogleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        textHertz = (TextView) findViewById(R.id.hz);
        textProb = (TextView) findViewById(R.id.prob);
        textTone = (TextView) findViewById(R.id.note);
        textClosenessPercent = (TextView) findViewById(R.id.deviation);
        textClosenessHz = (TextView) findViewById(R.id.closenessHz);
        textClosenessCents = (TextView) findViewById(R.id.closenessCents);
        fabRecord = (ImageButton) findViewById(R.id.fab_record);
        fabStop = (ImageButton) findViewById(R.id.fab_stop);
        fabRecord.setOnClickListener(this);
        fabStop.setOnClickListener(this);
        seekBar = (DiscreteSeekBar) findViewById(R.id.seekBar);

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.RECORD_AUDIO)) {
                // Show an expanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            } else {

                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.RECORD_AUDIO},
                        MY_PERMISSIONS_REQUEST_RECORD_AUDIO);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
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
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_RECORD_AUDIO: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    startDispatcher();
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

        }
    }

    public void getValidSampleRates() {
        for (int rate : new int[] {8000, 11025, 16000, 22050, 44100}) {  // add the rates you wish to check against
            int bufferSize = AudioRecord.getMinBufferSize(rate, AudioFormat.CHANNEL_CONFIGURATION_DEFAULT, AudioFormat.ENCODING_PCM_16BIT);
            if (bufferSize > 0) {
                System.out.println("Hodnota: " + rate);

            }
        }
    }

    private void startDispatcher() {
        dispatcher = AudioDispatcherFactory.fromDefaultMicrophone(44100, 4096, 1024);
        PitchDetectionHandler pdh = this;
        AudioProcessor p = new PitchProcessor(PitchProcessor.PitchEstimationAlgorithm.FFT_YIN, 44100, 4096, pdh);
        dispatcher.addAudioProcessor(p);
        new Thread(dispatcher,"Audio Dispatcher").start();
    }

    @Override
    public void handlePitch(PitchDetectionResult result, AudioEvent audioEvent) {
        final NoteResult noteResult = NotePitchHandler.mapPitchDetectionResultToNoteResult(result);
        //new DataLayerThread(mGoogleApiClient,"/message_path", Float.toString(pitchInHz)).start();
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (noteResult.getPitch() != -1) {
                    textHertz.setText("" + String.format("%.02f", noteResult.getPitch()) + " pitch found in Hz");
                    textProb.setText("" + String.format("%.02f", noteResult.getProbability()) + " probability in %");
                    textClosenessPercent.setText("" + String.format("%.02f", noteResult.getClosenessPercent()) + " closeness in %");
                    textClosenessHz.setText("" + String.format("%.02f", noteResult.getClosenessHz()) + " closeness in Hz");
                    textClosenessCents.setText("" + String.format("%.02f", noteResult.getClosenessCents()) + " closeness in cents");
                    textTone.setText("Closest note is :" + noteResult.getNote());

                    double d = noteResult.getClosenessCents();
                    int i = (int) d;
                    seekBar.setProgress(i);

                    if (noteResult.getClosenessCents() >= -5.0F && noteResult.getClosenessCents() < 5.0F) {
                        seekBar.setThumbColor(Color.GREEN, Color.GREEN);
                    }else {
                        seekBar.setThumbColor(Color.RED, Color.RED);
                    }

                } else {
                    textHertz.setText("Pitch wasn't found");
                    textProb.setText("");
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
                Snackbar.make(view, "Recording stopped...", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                dispatcher.stop();
                view.setVisibility(View.GONE);
                fabRecord.setVisibility(View.VISIBLE);
                new DataLayerThread(mGoogleApiClient,"/message_path", stopped).start();
                break;
            case R.id.fab_record:
                Snackbar.make(view, "Recording started...", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                startDispatcher();
                view.setVisibility(View.GONE);
                fabStop.setVisibility(View.VISIBLE);
                new DataLayerThread(mGoogleApiClient,"/message_path", started).start();
                break;
            default:
                break;

        }
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
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
