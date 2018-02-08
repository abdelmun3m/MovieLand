package com.abdelmun3m.movie_land;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;


public class Movie implements Parcelable {


    private static final String TAG =
            GeneralData.TAG+" : "+Movie.class.getSimpleName();
    public long Movie_DB_ID =-1;
    public String Movie_Id;
    public String OriginallTitle = "Movie Title";
    public String PosterImage;
    public MovieImages images;
    public String[] trailersKeys;
    public Review[] reviews;
    public String Overview;
    public String RelaseDate;
    public long Vote_Average;
    public int favorite  = 0; //not favorite movie


    public Movie() {
        images = new MovieImages();
    }

    public String getPosterUrl(){
       //posterURL = GeneralData.IMAGE_URL+this.PosterImage;
       return GeneralData.IMAGE_URL+this.images.imagePosterUrl;
   }
    public String getBackdropsUrl(){
        //posterURL = GeneralData.IMAGE_URL+this.PosterImage;
        return GeneralData.IMAGE_BACKDROP_URL+this.images.imageBackdropsUrl;
    }


    protected Movie(Parcel in) {
        Movie_Id = in.readString();
        Movie_DB_ID = in.readLong();
        OriginallTitle = in.readString();
        Overview = in.readString();
        RelaseDate = in.readString();
        Vote_Average=in.readLong();
        favorite = in.readInt();
        images = in.readParcelable(MovieImages.class.getClassLoader());
        //in.readStringArray(trailersKeys);
        trailersKeys = in.createStringArray();
       // in.readStringArray(trailersKeys);
        reviews = (Review[]) in.readParcelableArray(Review.class.getClassLoader());
    }

    public static final Creator<Movie> CREATOR = new Creator<Movie>() {
        @Override
        public Movie createFromParcel(Parcel in) {
            return new Movie(in);
        }

        @Override
        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(Movie_Id);
        dest.writeLong(Movie_DB_ID);
        dest.writeString(OriginallTitle);
        dest.writeString(Overview);
        dest.writeString(RelaseDate);
        dest.writeLong(Vote_Average);
        dest.writeInt(favorite);
        dest.writeParcelable(images,1);
        dest.writeStringArray(trailersKeys);
        dest.writeParcelableArray(reviews,1);
    }
}
