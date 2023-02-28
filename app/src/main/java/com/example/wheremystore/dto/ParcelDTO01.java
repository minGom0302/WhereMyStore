package com.example.wheremystore.dto;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

public class ParcelDTO01 implements Parcelable {
    List<Uri> uriList;

    public ParcelDTO01(List<Uri> uriList) {
        this.uriList = uriList;
    }

    protected ParcelDTO01(Parcel in) {
        in.readList(uriList, ClassLoader.getSystemClassLoader());
    }

    public static final Creator<ParcelDTO01> CREATOR = new Creator<ParcelDTO01>() {
        @Override
        public ParcelDTO01 createFromParcel(Parcel in) {
            return new ParcelDTO01(in);
        }

        @Override
        public ParcelDTO01[] newArray(int size) {
            return new ParcelDTO01[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeList(uriList);
    }
}
