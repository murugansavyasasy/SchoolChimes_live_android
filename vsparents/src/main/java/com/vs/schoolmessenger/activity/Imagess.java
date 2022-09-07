package com.vs.schoolmessenger.activity;

import android.media.Image;
import android.os.Parcel;
import android.os.Parcelable;

public class Imagess implements Parcelable {
    public long id;
    public String name;
    public String path;
    public boolean isSelected;

    public Imagess(long id, String name, String path, boolean isSelected) {
        this.id = id;
        this.name = name;
        this.path = path;
        this.isSelected = isSelected;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeString(name);
        dest.writeString(path);
    }

    public static final Parcelable.Creator<Imagess> CREATOR = new Parcelable.Creator<Imagess>() {
        @Override
        public Imagess createFromParcel(Parcel source) {
            return new Imagess(source);
        }

        @Override
        public Imagess[] newArray(int size) {
            return new Imagess[size];
        }
    };

    private Imagess(Parcel in) {
        id = in.readLong();
        name = in.readString();
        path = in.readString();
    }
}