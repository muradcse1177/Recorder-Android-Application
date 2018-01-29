package com.parallaxsoft.audiorecorder.fragments;
import android.app.AlertDialog;
import android.app.Dialog;
import android.graphics.ColorFilter;
import android.graphics.LightingColorFilter;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.SeekBar;
import android.widget.TextView;
import com.parallaxsoft.audiorecorder.R;
import com.parallaxsoft.audiorecorder.paraRecordingItem;
import com.melnykov.fab.FloatingActionButton;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class paraPlBackFrag extends DialogFragment{

    private static final String paraLog = "paraPlBackFrag";
    private static final String paraItemAg = "recording_item";
    private paraRecordingItem item;
    private Handler paraHandler = new Handler();
    private MediaPlayer paraMediaPlayer = null;
    private SeekBar paraSeekBar = null;
    private FloatingActionButton paraPlayButton = null;
    private TextView paraCurrentProgressTextView = null;
    private TextView paraFileNameTextView = null;
    private TextView paraFileLengthTextView = null;
    private boolean paraIsPlaying = false;
    long minutes = 0;
    long seconds = 0;

    public paraPlBackFrag newInstance(paraRecordingItem item) {
        paraPlBackFrag f = new paraPlBackFrag();
        Bundle b = new Bundle();
        b.putParcelable(paraItemAg, item);
        f.setArguments(b);

        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        item = getArguments().getParcelable(paraItemAg);

        long paraViewitemDuration = item.paraGetLength();
        minutes = TimeUnit.MILLISECONDS.toMinutes(paraViewitemDuration);
        seconds = TimeUnit.MILLISECONDS.toSeconds(paraViewitemDuration)
                - TimeUnit.MINUTES.toSeconds(minutes);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View view = getActivity().getLayoutInflater().inflate(R.layout.fragment_media_playback, null);
        paraFileNameTextView = (TextView) view.findViewById(R.id.file_name_text_view);
        paraFileLengthTextView = (TextView) view.findViewById(R.id.file_length_text_view);
        paraCurrentProgressTextView = (TextView) view.findViewById(R.id.current_progress_text_view);
        paraSeekBar = (SeekBar) view.findViewById(R.id.seekbar);
        ColorFilter filter = new LightingColorFilter
                (getResources().getColor(R.color.primary), getResources().getColor(R.color.primary));
        paraSeekBar.getProgressDrawable().setColorFilter(filter);
        paraSeekBar.getThumb().setColorFilter(filter);
        paraSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(paraMediaPlayer != null && fromUser) {
                    paraMediaPlayer.seekTo(progress);
                    paraHandler.removeCallbacks(paraStartMRunnable);

                    long minutes = TimeUnit.MILLISECONDS.toMinutes(paraMediaPlayer.getCurrentPosition());
                    long seconds = TimeUnit.MILLISECONDS.toSeconds(paraMediaPlayer.getCurrentPosition())
                            - TimeUnit.MINUTES.toSeconds(minutes);
                    paraCurrentProgressTextView.setText(String.format("%02d:%02d", minutes,seconds));

                    paraStartCurrentPosition();

                } else if (paraMediaPlayer == null && fromUser) {
                    paraStartPlayerFromPoint(progress);
                    paraStartCurrentPosition();
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                if(paraMediaPlayer != null) {
                    paraHandler.removeCallbacks(paraStartMRunnable);
                }
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if (paraMediaPlayer != null) {
                    paraHandler.removeCallbacks(paraStartMRunnable);
                    paraMediaPlayer.seekTo(seekBar.getProgress());

                    long minutes = TimeUnit.MILLISECONDS.toMinutes(paraMediaPlayer.getCurrentPosition());
                    long seconds = TimeUnit.MILLISECONDS.toSeconds(paraMediaPlayer.getCurrentPosition())
                            - TimeUnit.MINUTES.toSeconds(minutes);
                    paraCurrentProgressTextView.setText(String.format("%02d:%02d", minutes,seconds));
                    paraStartCurrentPosition();
                }
            }
        });

        paraPlayButton = (FloatingActionButton) view.findViewById(R.id.fab_play);
        paraPlayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onPlay(paraIsPlaying);
                paraIsPlaying = !paraIsPlaying;
            }
        });

        paraFileNameTextView.setText(item.paraGetName());
        paraFileLengthTextView.setText(String.format("%02d:%02d", minutes,seconds));
        builder.setView(view);
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        return builder.create();
}

    @Override
    public void onStart() {
        super.onStart();
        Window window = getDialog().getWindow();
        window.setBackgroundDrawableResource(android.R.color.transparent);
        AlertDialog alertDialog = (AlertDialog) getDialog();
        alertDialog.getButton(Dialog.BUTTON_POSITIVE).setEnabled(false);
        alertDialog.getButton(Dialog.BUTTON_NEGATIVE).setEnabled(false);
        alertDialog.getButton(Dialog.BUTTON_NEUTRAL).setEnabled(false);
    }

    @Override
    public void onPause() {
        super.onPause();

        if (paraMediaPlayer != null) {
            paraStartstopPlaying();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (paraMediaPlayer != null) {
            paraStartstopPlaying();
        }
    }
    private void onPlay(boolean paraIsPlaying){
        if (!paraIsPlaying) {
            if(paraMediaPlayer == null) {
                paraStartPlaying();
            } else {
                paraStartresumePlaying();
            }
        } else {
            paraStartPausePlaying();
        }
    }

    private void paraStartPlaying() {
        paraPlayButton.setImageResource(R.drawable.ic_media_pause);
        paraMediaPlayer = new MediaPlayer();
        try {
            paraMediaPlayer.setDataSource(item.paraGetFilePath());
            paraMediaPlayer.prepare();
            paraSeekBar.setMax(paraMediaPlayer.getDuration());
            paraMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    paraMediaPlayer.start();
                }
            });
        } catch (IOException e) {
            Log.e(paraLog, "prepare() failed");
        }
        paraMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                paraStartstopPlaying();
            }
        });
        paraStartCurrentPosition();
        getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    private void paraStartPlayerFromPoint(int progress) {
        paraMediaPlayer = new MediaPlayer();
        try {
            paraMediaPlayer.setDataSource(item.paraGetFilePath());
            paraMediaPlayer.prepare();
            paraSeekBar.setMax(paraMediaPlayer.getDuration());
            paraMediaPlayer.seekTo(progress);
            paraMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    paraStartstopPlaying();
                }
            });
        } catch (IOException e) {
            Log.e(paraLog, "prepare() failed");
        }
        getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }
    private void paraStartPausePlaying() {
        paraPlayButton.setImageResource(R.drawable.ic_media_play);
        paraHandler.removeCallbacks(paraStartMRunnable);
        paraMediaPlayer.pause();
    }

    private void paraStartresumePlaying() {
        paraPlayButton.setImageResource(R.drawable.ic_media_pause);
        paraHandler.removeCallbacks(paraStartMRunnable);
        paraMediaPlayer.start();
        paraStartCurrentPosition();
    }

    private void paraStartstopPlaying() {
        paraPlayButton.setImageResource(R.drawable.ic_media_play);
        paraHandler.removeCallbacks(paraStartMRunnable);
        paraMediaPlayer.stop();
        paraMediaPlayer.reset();
        paraMediaPlayer.release();
        paraMediaPlayer = null;

        paraSeekBar.setProgress(paraSeekBar.getMax());
        paraIsPlaying = !paraIsPlaying;

        paraCurrentProgressTextView.setText(paraFileLengthTextView.getText());
        paraSeekBar.setProgress(paraSeekBar.getMax());
        getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }
    private Runnable paraStartMRunnable = new Runnable() {
        @Override
        public void run() {
            if(paraMediaPlayer != null){
                int paraStartMCurrentPosition = paraMediaPlayer.getCurrentPosition();
                paraSeekBar.setProgress(paraStartMCurrentPosition);
                long minutes = TimeUnit.MILLISECONDS.toMinutes(paraStartMCurrentPosition);
                long seconds = TimeUnit.MILLISECONDS.toSeconds(paraStartMCurrentPosition)
                        - TimeUnit.MINUTES.toSeconds(minutes);
                paraCurrentProgressTextView.setText(String.format("%02d:%02d", minutes, seconds));
                paraStartCurrentPosition();
            }
        }
    };

    private void paraStartCurrentPosition() {
        paraHandler.postDelayed(paraStartMRunnable, 1000);
    }
}
