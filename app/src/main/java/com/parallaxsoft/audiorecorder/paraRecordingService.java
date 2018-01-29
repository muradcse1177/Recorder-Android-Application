package com.parallaxsoft.audiorecorder;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaRecorder;
import android.os.Environment;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.parallaxsoft.audiorecorder.activities.MainActivity;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

public class paraRecordingService extends Service {

    private static final String paraLog = "paraRecordingService";

    private String paraFileName = null;
    private String paraFilePath = null;

    private MediaRecorder paraRecorder = null;

    private paraAudioDBHelper paraDatabase;

    private long paraStartingTimeMillis = 0;
    private long paraElapsedMillis = 0;
    private int paraElapsedSeconds = 0;
    private OnTimerChangedListener paraTimerChangedListener = null;
    private static final SimpleDateFormat paraTimerFormat = new SimpleDateFormat("mm:ss", Locale.getDefault());

    private Timer paraTimer = null;
    private TimerTask paraIncrementTimerTask = null;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public interface OnTimerChangedListener {
        void onTimerChanged(int seconds);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        paraDatabase = new paraAudioDBHelper(getApplicationContext());
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        paraStartRecording();
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        if (paraRecorder != null) {
            paraStopRecording();
        }

        super.onDestroy();
    }

    public void paraStartRecording() {
        parasSetFileNameAndPath();

        paraRecorder = new MediaRecorder();
        paraRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        paraRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        paraRecorder.setOutputFile(paraFilePath);
        paraRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
        paraRecorder.setAudioChannels(1);
        if (paraMySharedPreferences.paraGetPrefHighQuality(this)) {
            paraRecorder.setAudioSamplingRate(44100);
            paraRecorder.setAudioEncodingBitRate(192000);
        }

        try {
            paraRecorder.prepare();
            paraRecorder.start();
            paraStartingTimeMillis = System.currentTimeMillis();

        } catch (IOException e) {
            Log.e(paraLog, "prepare() failed");
        }
    }

    public void parasSetFileNameAndPath(){
        int count = 0;
        File f;

        do{
            count++;

            paraFileName = getString(R.string.DFLNA)
                    + "_" + (paraDatabase.getCount() + count) + ".mp4";
            paraFilePath = Environment.getExternalStorageDirectory().getAbsolutePath();
            paraFilePath += "/SoundRecorder/" + paraFileName;

            f = new File(paraFilePath);
        }while (f.exists() && !f.isDirectory());
    }

    public void paraStopRecording() {
        paraRecorder.stop();
        paraElapsedMillis = (System.currentTimeMillis() - paraStartingTimeMillis);
        paraRecorder.release();
        Toast.makeText(this, getString(R.string.toast_recording_finish) + " " + paraFilePath, Toast.LENGTH_LONG).show();
        if (paraIncrementTimerTask != null) {
            paraIncrementTimerTask.cancel();
            paraIncrementTimerTask = null;
        }
        paraRecorder = null;
        try {
            paraDatabase.paraAddRecording(paraFileName, paraFilePath, paraElapsedMillis);

        } catch (Exception e){
            Log.e(paraLog, "exception", e);
        }
    }
    private void paraStartTimer() {
        paraTimer = new Timer();
        paraIncrementTimerTask = new TimerTask() {
            @Override
            public void run() {
                paraElapsedSeconds++;
                if (paraTimerChangedListener != null)
                    paraTimerChangedListener.onTimerChanged(paraElapsedSeconds);
                NotificationManager mgr = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                mgr.notify(1, paraCreateNotification());
            }
        };
        paraTimer.scheduleAtFixedRate(paraIncrementTimerTask, 1000, 1000);
    }
    private Notification paraCreateNotification() {
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(getApplicationContext())
                        .setSmallIcon(R.drawable.ic_mic_white_36dp)
                        .setContentTitle(getString(R.string.ytrefghgjhertgdsfds))
                        .setContentText(paraTimerFormat.format(paraElapsedSeconds * 1000))
                        .setOngoing(true);

        mBuilder.setContentIntent(PendingIntent.getActivities(getApplicationContext(), 0,
                new Intent[]{new Intent(getApplicationContext(), MainActivity.class)}, 0));

        return mBuilder.build();
    }
}
