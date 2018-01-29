package com.parallaxsoft.audiorecorder.adapters;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.text.format.DateUtils;
import com.parallaxsoft.audiorecorder.paraAudioDBHelper;
import com.parallaxsoft.audiorecorder.R;
import com.parallaxsoft.audiorecorder.paraRecordingItem;
import com.parallaxsoft.audiorecorder.fragments.paraPlBackFrag;
import com.parallaxsoft.audiorecorder.listeners.OnDatabaseChangedListener;
import java.io.File;
import java.util.Locale;
import java.util.concurrent.TimeUnit;
import java.util.ArrayList;

public class paraViewFFileViewerAdapter extends RecyclerView.Adapter<paraViewFFileViewerAdapter.paraRecordingsViewHolder>
    implements OnDatabaseChangedListener{
    private static final String paraLog = "paraViewFFileViewerAdapter";
    private paraAudioDBHelper paraDatabase;
    paraRecordingItem item;
    Context paraContext;
    LinearLayoutManager llm;

    public paraViewFFileViewerAdapter(Context context, LinearLayoutManager linearLayoutManager) {
        super();
        paraContext = context;
        paraDatabase = new paraAudioDBHelper(paraContext);
        paraDatabase.setOnDatabaseChangedListener(this);
        llm = linearLayoutManager;
    }

    @Override
    public void onBindViewHolder(final paraRecordingsViewHolder holder, int position) {
        item = getItem(position);
        long paraViewitemDuration = item.paraGetLength();
        long minutes = TimeUnit.MILLISECONDS.toMinutes(paraViewitemDuration);
        long seconds = TimeUnit.MILLISECONDS.toSeconds(paraViewitemDuration)
                - TimeUnit.MINUTES.toSeconds(minutes);
        holder.vName.setText(item.paraGetName());
        holder.vLength.setText(String.format("%02d:%02d", minutes, seconds));
        holder.vDateAdded.setText(
            DateUtils.formatDateTime(
                paraContext,
                item.paraGetTime(),
                DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_NUMERIC_DATE | DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_YEAR
            )
        );

        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    paraPlBackFrag playbackFragment =
                            new paraPlBackFrag().newInstance(getItem(holder.getPosition()));

                    FragmentTransaction transaction = ((FragmentActivity) paraContext)
                            .getSupportFragmentManager()
                            .beginTransaction();

                    playbackFragment.show(transaction, "dialog_playback");

                } catch (Exception e) {
                    Log.e(paraLog, "exception", e);
                }
            }
        });

        holder.cardView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                ArrayList<String> entrys = new ArrayList<String>();
                entrys.add(paraContext.getString(R.string.ytrefghgjhert));
                entrys.add(paraContext.getString(R.string.ytrgdvcxbxasfasc));
                entrys.add(paraContext.getString(R.string.iyfgsdvcxbvc));
                final CharSequence[] items = entrys.toArray(new CharSequence[entrys.size()]);
                AlertDialog.Builder builder = new AlertDialog.Builder(paraContext);
                builder.setTitle(paraContext.getString(R.string.poiuytfvbdfghgjhert));
                builder.setItems(items, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int item) {
                        if (item == 0) {
                            shareFileDialog(holder.getPosition());
                        } if (item == 1) {
                            renameFileDialog(holder.getPosition());
                        } else if (item == 2) {
                            deleteFileDialog(holder.getPosition());
                        }
                    }
                });
                builder.setCancelable(true);
                builder.setNegativeButton(paraContext.getString(R.string.iyfgsdvcxbvckjhkjghjghjgf),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

                AlertDialog alert = builder.create();
                alert.show();

                return false;
            }
        });
    }

    @Override
    public paraRecordingsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.
                from(parent.getContext()).
                inflate(R.layout.card_view, parent, false);

        paraContext = parent.getContext();

        return new paraRecordingsViewHolder(itemView);
    }

    public static class paraRecordingsViewHolder extends RecyclerView.ViewHolder {
        protected TextView vName;
        protected TextView vLength;
        protected TextView vDateAdded;
        protected View cardView;
        public paraRecordingsViewHolder(View v) {
            super(v);
            vName = (TextView) v.findViewById(R.id.file_name_text);
            vLength = (TextView) v.findViewById(R.id.file_length_text);
            vDateAdded = (TextView) v.findViewById(R.id.file_date_added_text);
            cardView = v.findViewById(R.id.card_view);
        }
    }
    @Override
    public int getItemCount() {
        return paraDatabase.getCount();
    }

    public paraRecordingItem getItem(int position) {

       return paraDatabase.paraGetFragmentManagerAt(position);

   }

    @Override
    public void onNewDatabaseEntryAdded() {
        notifyItemInserted(getItemCount() - 1);
        llm.scrollToPosition(getItemCount() - 1);
    }

    @Override
    public void onDatabaseEntryRenamed() {
    }

    public void remove(int position) {
        File file = new File(getItem(position).paraGetFilePath());
        file.delete();

        Toast.makeText(
            paraContext,
            String.format(
                paraContext.getString(R.string.toast_file_delete),
                getItem(position).paraGetName()
            ),
            Toast.LENGTH_SHORT
        ).show();

        paraDatabase.paraRemoveItemWithId(getItem(position).paraGetId());
        notifyItemRemoved(position);
    }

    public void removeOutOfApp(String filePath) {
    }

    public void rename(int position, String name) {

        String paraFilePath = Environment.getExternalStorageDirectory().getAbsolutePath();
        paraFilePath += "/SoundRecorder/" + name;
        File f = new File(paraFilePath);

        if (f.exists() && !f.isDirectory()) {
            Toast.makeText(paraContext,
                    String.format(paraContext.getString(R.string.toast_file_exists), name),
                    Toast.LENGTH_SHORT).show();

        } else {
            File oldFilePath = new File(getItem(position).paraGetFilePath());
            oldFilePath.renameTo(f);
            paraDatabase.paraRenameItem(getItem(position), name, paraFilePath);
            notifyItemChanged(position);
        }
    }

    public void shareFileDialog(int position) {
        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(new File(getItem(position).paraGetFilePath())));
        shareIntent.setType("audio/mp4");
        paraContext.startActivity(Intent.createChooser(shareIntent, paraContext.getText(R.string.sendTo)));
    }

    public void renameFileDialog (final int position) {
        AlertDialog.Builder renameFileBuilder = new AlertDialog.Builder(paraContext);

        LayoutInflater inflater = LayoutInflater.from(paraContext);
        View view = inflater.inflate(R.layout.dialog_rename_file, null);

        final EditText input = (EditText) view.findViewById(R.id.new_name);

        renameFileBuilder.setTitle(paraContext.getString(R.string.poiuytfvb));
        renameFileBuilder.setCancelable(true);
        renameFileBuilder.setPositiveButton(paraContext.getString(R.string.thtfgnbngfhqw),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        try {
                            String value = input.getText().toString().trim() + ".mp4";
                            rename(position, value);

                        } catch (Exception e) {
                            Log.e(paraLog, "exception", e);
                        }

                        dialog.cancel();
                    }
                });
        renameFileBuilder.setNegativeButton(paraContext.getString(R.string.iyfgsdvcxbvckjhkjghjghjgf),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        renameFileBuilder.setView(view);
        AlertDialog alert = renameFileBuilder.create();
        alert.show();
    }

    public void deleteFileDialog (final int position) {
        AlertDialog.Builder confirmDelete = new AlertDialog.Builder(paraContext);
        confirmDelete.setTitle(paraContext.getString(R.string.DTDELETE));
        confirmDelete.setMessage(paraContext.getString(R.string.DTEXTDE));
        confirmDelete.setCancelable(true);
        confirmDelete.setPositiveButton(paraContext.getString(R.string.jtyrtdfgfqwert),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        try {
                            remove(position);

                        } catch (Exception e) {
                            Log.e(paraLog, "exception", e);
                        }

                        dialog.cancel();
                    }
                });
        confirmDelete.setNegativeButton(paraContext.getString(R.string.hjfghtgfvdfgbg),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        AlertDialog alert = confirmDelete.create();
        alert.show();
    }
}
