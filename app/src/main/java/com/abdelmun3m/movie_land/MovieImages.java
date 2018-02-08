package com.abdelmun3m.movie_land;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by abdelmun3m on 27/01/18.
 */

public class MovieImages implements Parcelable{
    public String imagePosterUrl;
    public float imagePosterRatio = 1.778f;
    public String imageBackdropsUrl;
    public float imageBackdropsRatio;


    public MovieImages(){

    };
    protected MovieImages(Parcel in) {
        imagePosterUrl = in.readString();
        imagePosterRatio = in.readFloat();
        imageBackdropsUrl = in.readString();
        imageBackdropsRatio = in.readFloat();
    }

    public static final Creator<MovieImages> CREATOR = new Creator<MovieImages>() {
        @Override
        public MovieImages createFromParcel(Parcel in) {
            return new MovieImages(in);
        }

        @Override
        public MovieImages[] newArray(int size) {
            return new MovieImages[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(imagePosterUrl);
        dest.writeFloat(imagePosterRatio);
        dest.writeString(imageBackdropsUrl);
        dest.writeFloat(imageBackdropsRatio);
    }
}
