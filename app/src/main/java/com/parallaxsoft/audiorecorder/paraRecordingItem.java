package com.parallaxsoft.audiorecorder;

import android.os.Parcel;
import android.os.Parcelable;

public class paraRecordingItem implements Parcelable {
    private String paraName;
    private String paraFilePath;
    private int mId;
    private int paraLength;
    private long paraTime;

    public paraRecordingItem()
    {
    }

    public paraRecordingItem(Parcel in) {
        paraName = in.readString();
        paraFilePath = in.readString();
        mId = in.readInt();
        paraLength = in.readInt();
        paraTime = in.readLong();
    }

    public String paraGetFilePath() {
        return paraFilePath;
    }

    public void setFilePath(String filePath) {
        paraFilePath = filePath;
    }

    public int paraGetLength() {
        return paraLength;
    }

    public void setLength(int length) {
        paraLength = length;
    }

    public int paraGetId() {
        return mId;
    }

    public void setId(int id) {
        mId = id;
    }

    public String paraGetName() {
        return paraName;
    }

    public void setName(String name) {
        paraName = name;
    }

    public long paraGetTime() {
        return paraTime;
    }

    public void setTime(long time) {
        paraTime = time;
    }

    public static final Parcelable.Creator<paraRecordingItem> CREATOR = new Parcelable.Creator<paraRecordingItem>() {
        public paraRecordingItem createFromParcel(Parcel in) {
            return new paraRecordingItem(in);
        }

        public paraRecordingItem[] newArray(int size) {
            return new paraRecordingItem[size];
        }
    };

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(mId);
        dest.writeInt(paraLength);
        dest.writeLong(paraTime);
        dest.writeString(paraFilePath);
        dest.writeString(paraName);
    }

    @Override
    public int describeContents() {
        return 0;
    }
}