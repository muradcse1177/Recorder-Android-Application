package com.parallaxsoft.audiorecorder.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.SystemClock;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.TextView;
import android.widget.Toast;

import com.parallaxsoft.audiorecorder.R;
import com.parallaxsoft.audiorecorder.paraRecordingService;
import com.melnykov.fab.FloatingActionButton;

import java.io.File;

public class paraRecordFragment extends Fragment {
    private static final String paraPositionAg = "position";
    private static final String paraLog = paraRecordFragment.class.getSimpleName();
    private int position;
    private FloatingActionButton paraRecordButton = null;
    private Button paraPauseButton = null;
    private TextView paraRecordingPrompt;
    private int paraRecordPromptCount = 0;
    private boolean paraStartRecording = true;
    private boolean paraPauseRecording = true;
    private Chronometer paraChronometer = null;
    long timeWhenPaused = 0;
    public static paraRecordFragment newInstance(int position) {
        paraRecordFragment f = new paraRecordFragment();
        Bundle b = new Bundle();
        b.putInt(paraPositionAg, position);
        f.setArguments(b);

        return f;
    }

    public paraRecordFragment() {
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        position = getArguments().getInt(paraPositionAg);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View recordView = inflater.inflate(R.layout.fragment_record, container, false);

        paraChronometer = (Chronometer) recordView.findViewById(R.id.chronometer);
        paraRecordingPrompt = (TextView) recordView.findViewById(R.id.recording_status_text);
        paraRecordButton = (FloatingActionButton) recordView.findViewById(R.id.btnRecord);
        paraRecordButton.setColorNormal(getResources().getColor(R.color.primary));
        paraRecordButton.setColorPressed(getResources().getColor(R.color.primary_dark));
        paraRecordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                paraRecord(paraStartRecording);
                paraStartRecording = !paraStartRecording;
            }
        });

        paraPauseButton = (Button) recordView.findViewById(R.id.btnPause);
        paraPauseButton.setVisibility(View.GONE); //hide pause button before recording starts
        paraPauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                paraPauseRecord(paraPauseRecording);
                paraPauseRecording = !paraPauseRecording;
            }
        });

        return recordView;
    }
    private void paraRecord(boolean start){

        Intent intent = new Intent(getActivity(), paraRecordingService.class);

        if (start) {
            paraRecordButton.setImageResource(R.drawable.ic_media_stop);
            Toast.makeText(getActivity(),R.string.tyjtrherrtertery,Toast.LENGTH_SHORT).show();
            File folder = new File(Environment.getExternalStorageDirectory() + "/SoundRecorder");
            if (!folder.exists()) {
                folder.mkdir();
            }
            paraChronometer.setBase(SystemClock.elapsedRealtime());
            paraChronometer.start();
            paraChronometer.setOnChronometerTickListener(new Chronometer.OnChronometerTickListener() {
                @Override
                public void onChronometerTick(Chronometer chronometer) {
                    if (paraRecordPromptCount == 0) {
                        paraRecordingPrompt.setText(getString(R.string.jghfdxcvqetpoimg) + ".");
                    } else if (paraRecordPromptCount == 1) {
                        paraRecordingPrompt.setText(getString(R.string.jghfdxcvqetpoimg) + "..");
                    } else if (paraRecordPromptCount == 2) {
                        paraRecordingPrompt.setText(getString(R.string.jghfdxcvqetpoimg) + "...");
                        paraRecordPromptCount = -1;
                    }

                    paraRecordPromptCount++;
                }
            });
            getActivity().startService(intent);
            getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
            paraRecordingPrompt.setText(getString(R.string.jghfdxcvqetpoimg) + ".");
            paraRecordPromptCount++;

        } else {
            paraRecordButton.setImageResource(R.drawable.ic_mic_white_36dp);
            paraChronometer.stop();
            paraChronometer.setBase(SystemClock.elapsedRealtime());
            timeWhenPaused = 0;
            paraRecordingPrompt.setText(getString(R.string.record_prompt));

            getActivity().stopService(intent);
            getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        }
    }

    private void paraPauseRecord(boolean pause) {
        if (pause) {
            paraPauseButton.setCompoundDrawablesWithIntrinsicBounds
                    (R.drawable.ic_media_play ,0 ,0 ,0);
            paraRecordingPrompt.setText((String)getString(R.string.RRBUTTON).toUpperCase());
            timeWhenPaused = paraChronometer.getBase() - SystemClock.elapsedRealtime();
            paraChronometer.stop();
        } else {
            paraPauseButton.setCompoundDrawablesWithIntrinsicBounds
                    (R.drawable.ic_media_pause ,0 ,0 ,0);
            paraRecordingPrompt.setText((String)getString(R.string.PRBMMM).toUpperCase());
            paraChronometer.setBase(SystemClock.elapsedRealtime() + timeWhenPaused);
            paraChronometer.start();
        }
    }
}