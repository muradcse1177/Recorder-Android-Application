package com.parallaxsoft.audiorecorder;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import com.parallaxsoft.audiorecorder.listeners.OnDatabaseChangedListener;
import java.util.Comparator;


public class paraAudioDBHelper extends SQLiteOpenHelper {
    private Context paraContext;
    private static final String paraLog = "paraAudioDBHelper";
    private static OnDatabaseChangedListener mOnDatabaseChangedListener;
    public static final String paraDBName = "saved_recordings.db";
    private static final int DATABASE_VERSION = 1;
    public static abstract class paraAudioDBHelperItem implements BaseColumns {
        public static final String paraTBName = "saved_recordings";
        public static final String paraColName = "recording_name";
        public static final String paraColNamePath = "file_path";
        public static final String paraColNameLength = "length";
        public static final String paraColNameTime = "time_added";
    }

    private static final String paraTextType = " TEXT";
    private static final String paraComma = ",";
    private static final String paraSqlCrEn =
            "CREATE TABLE " + paraAudioDBHelperItem.paraTBName + " (" +
                    paraAudioDBHelperItem._ID + " INTEGER PRIMARY KEY" + paraComma +
                    paraAudioDBHelperItem.paraColName + paraTextType + paraComma +
                    paraAudioDBHelperItem.paraColNamePath + paraTextType + paraComma +
                    paraAudioDBHelperItem.paraColNameLength + " INTEGER " + paraComma +
                    paraAudioDBHelperItem.paraColNameTime + " INTEGER " + ")";

    @SuppressWarnings("unused")
    private static final String SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS " + paraAudioDBHelperItem.paraTBName;

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(paraSqlCrEn);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public paraAudioDBHelper(Context context) {
        super(context, paraDBName, null, DATABASE_VERSION);
        paraContext = context;
    }

    public static void setOnDatabaseChangedListener(OnDatabaseChangedListener listener) {
        mOnDatabaseChangedListener = listener;
    }

    public paraRecordingItem paraGetFragmentManagerAt(int position) {
        SQLiteDatabase db = getReadableDatabase();
        String[] projection = {
                paraAudioDBHelperItem._ID,
                paraAudioDBHelperItem.paraColName,
                paraAudioDBHelperItem.paraColNamePath,
                paraAudioDBHelperItem.paraColNameLength,
                paraAudioDBHelperItem.paraColNameTime
        };
        Cursor c = db.query(paraAudioDBHelperItem.paraTBName, projection, null, null, null, null, null);
        if (c.moveToPosition(position)) {
            paraRecordingItem item = new paraRecordingItem();
            item.setId(c.getInt(c.getColumnIndex(paraAudioDBHelperItem._ID)));
            item.setName(c.getString(c.getColumnIndex(paraAudioDBHelperItem.paraColName)));
            item.setFilePath(c.getString(c.getColumnIndex(paraAudioDBHelperItem.paraColNamePath)));
            item.setLength(c.getInt(c.getColumnIndex(paraAudioDBHelperItem.paraColNameLength)));
            item.setTime(c.getLong(c.getColumnIndex(paraAudioDBHelperItem.paraColNameTime)));
            c.close();
            return item;
        }
        return null;
    }

    public void paraRemoveItemWithId(int id) {
        SQLiteDatabase db = getWritableDatabase();
        String[] whereArgs = { String.valueOf(id) };
        db.delete(paraAudioDBHelperItem.paraTBName, "_ID=?", whereArgs);
    }

    public int getCount() {
        SQLiteDatabase db = getReadableDatabase();
        String[] projection = { paraAudioDBHelperItem._ID };
        Cursor c = db.query(paraAudioDBHelperItem.paraTBName, projection, null, null, null, null, null);
        int count = c.getCount();
        c.close();
        return count;
    }

    public Context getContext() {
        return paraContext;
    }

    public class RecordingComparator implements Comparator<paraRecordingItem> {
        public int compare(paraRecordingItem item1, paraRecordingItem item2) {
            Long o1 = item1.paraGetTime();
            Long o2 = item2.paraGetTime();
            return o2.compareTo(o1);
        }
    }

    public long paraAddRecording(String recordingName, String filePath, long length) {

        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(paraAudioDBHelperItem.paraColName, recordingName);
        cv.put(paraAudioDBHelperItem.paraColNamePath, filePath);
        cv.put(paraAudioDBHelperItem.paraColNameLength, length);
        cv.put(paraAudioDBHelperItem.paraColNameTime, System.currentTimeMillis());
        long rowId = db.insert(paraAudioDBHelperItem.paraTBName, null, cv);

        if (mOnDatabaseChangedListener != null) {
            mOnDatabaseChangedListener.onNewDatabaseEntryAdded();
        }

        return rowId;
    }

    public void paraRenameItem(paraRecordingItem item, String recordingName, String filePath) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(paraAudioDBHelperItem.paraColName, recordingName);
        cv.put(paraAudioDBHelperItem.paraColNamePath, filePath);
        db.update(paraAudioDBHelperItem.paraTBName, cv,
                paraAudioDBHelperItem._ID + "=" + item.paraGetId(), null);

        if (mOnDatabaseChangedListener != null) {
            mOnDatabaseChangedListener.onDatabaseEntryRenamed();
        }
    }

    public long paraRestoreRecording(paraRecordingItem item) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(paraAudioDBHelperItem.paraColName, item.paraGetName());
        cv.put(paraAudioDBHelperItem.paraColNamePath, item.paraGetFilePath());
        cv.put(paraAudioDBHelperItem.paraColNameLength, item.paraGetLength());
        cv.put(paraAudioDBHelperItem.paraColNameTime, item.paraGetTime());
        cv.put(paraAudioDBHelperItem._ID, item.paraGetId());
        long rowId = db.insert(paraAudioDBHelperItem.paraTBName, null, cv);
        if (mOnDatabaseChangedListener != null) {
        }
        return rowId;
    }
}
