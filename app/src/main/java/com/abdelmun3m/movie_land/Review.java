package com.abdelmun3m.movie_land;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by abdelmun3m on 12/10/17.
 */

public class Review implements Parcelable{
    public String url;
    public String author;
    public String content;

    protected Review(Parcel in) {
        url = in.readString();
        author = in.readString();
        content = in.readString();
    }


    public Review(){}

    public static final Creator<Review> CREATOR = new Creator<Review>() {
        @Override
        public Review createFromParcel(Parcel in) {
            return new Review(in);
        }

        @Override
        public Review[] newArray(int size) {
            return new Review[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(url);
        dest.writeString(author);
        dest.writeString(content);
    }
}
