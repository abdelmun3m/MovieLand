package com.abdelmun3m.movie_land.utilities;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.util.Log;

import com.abdelmun3m.movie_land.GeneralData;
import com.abdelmun3m.movie_land.Movie;
import com.abdelmun3m.movie_land.MovieImages;
import com.abdelmun3m.movie_land.Review;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class MovieAPI {

    private static final String TAG = GeneralData.TAG+":"+ MovieAPI.class.getSimpleName();

    private static final String VIDEOS_PATH = "videos";
    private static final String REVIEWS_PATH = "reviews";
    private static final String MOVIE_PATH = "movie";
    private static final String IMAGES_PATH = "images";
    public static final String RECOMMENDATION_PATH = "recommendations";



    public static URL Build_Movies_Category_URL(int category){
        String sort_URI;
        if(category == 1){sort_URI = GeneralData.TOP_RATED_MOVIES_URL;}
        else if(category == 2){sort_URI = GeneralData.POPULAR_MOVIES_URL;}
        else{sort_URI = null;}
        Uri uri = Uri.parse(sort_URI).buildUpon()
                .appendQueryParameter(GeneralData.QUERY_API_KEY, GeneralData.API_KEY)
                .appendQueryParameter(GeneralData.QUERY_LANGUAGE, GeneralData.DEFAULT_LANG)
                .appendQueryParameter(GeneralData.QUERY_PAGE,GeneralData.PAGE_NUMBER)
                .build();

        URL url = null;
        try {
             url = new URL(uri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        Log.i(TAG,"Builed URL : "+ (url != null ? url.toString() : null));
        return url;
    }

    public static URL Build_Movie_Trailer_Url(String movieID){
        Uri uri = Uri.parse(GeneralData.MOVIE_API_URL).buildUpon()
                .appendPath(MOVIE_PATH)
                .appendPath(movieID).appendPath(VIDEOS_PATH)
                .appendQueryParameter(GeneralData.QUERY_API_KEY,GeneralData.API_KEY)
                .appendQueryParameter(GeneralData.QUERY_LANGUAGE, GeneralData.DEFAULT_LANG)
                .build();
        URL url = null;
        try {
            url = new URL(uri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return url;
    }

    public static URL Build_Movies_Review_Url(String movieId){
        Uri uri = Uri.parse(GeneralData.MOVIE_API_URL).buildUpon()
                .appendPath(MOVIE_PATH)
                .appendPath(movieId).appendPath(REVIEWS_PATH)
                .appendQueryParameter(GeneralData.QUERY_API_KEY,GeneralData.API_KEY)
                .appendQueryParameter(GeneralData.QUERY_LANGUAGE, GeneralData.DEFAULT_LANG)
                .build();
        URL url = null;
        try {
            url = new URL(uri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        Log.i(TAG,"Trailer URL : "+ (url != null ? url.toString() : null));
        return url;
    }

    public static URL Build_Movies_Images_Url(String movieId){
        Uri uri = Uri.parse(GeneralData.MOVIE_API_URL).buildUpon()
                .appendPath(MOVIE_PATH)
                .appendPath(movieId)
                .appendPath(IMAGES_PATH)
                .appendQueryParameter(GeneralData.QUERY_API_KEY,GeneralData.API_KEY)
                .build();
        URL url = null;
        try {
            url = new URL(uri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        Log.i(TAG,"Images URL : "+ (url != null ? url.toString() : null));
        return url;
    }

    public static URL Build_Movie_Recommendation(String movieId){
        Uri uri = Uri.parse(GeneralData.MOVIE_API_URL).buildUpon()
                .appendPath(MOVIE_PATH)
                .appendPath(movieId)
                .appendPath(RECOMMENDATION_PATH)
                .appendQueryParameter(GeneralData.QUERY_API_KEY,GeneralData.API_KEY)
                .build();
        URL url = null;
        try {
            url = new URL(uri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        Log.i(TAG,"Recommendation URL : "+ (url != null ? url.toString() : null));
        return url;
    }

    public static URL Build_MOVIE_INFO(String movieId){
        Uri uri = Uri.parse(GeneralData.MOVIE_API_URL).buildUpon()
                .appendPath(MOVIE_PATH)
                .appendPath(movieId)
                .appendQueryParameter(GeneralData.QUERY_API_KEY,GeneralData.API_KEY)
                .appendQueryParameter(GeneralData.Json_APPEND_REQUEST_KEY_OPTION,
                        VIDEOS_PATH+","+
                        IMAGES_PATH+","+
                        REVIEWS_PATH
                )
                .build();
        URL url = null;
        try {
            url = new URL(uri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        Log.i(TAG,"Movie Info URL : "+ (url != null ? url.toString() : null));
        return url;
    }

    static  String res ="";
/*
    public static String get_Response(URL url,Context context){
        HttpURLConnection connection = null;
        try {
            connection = (HttpURLConnection) url.openConnection();
            InputStream response = connection.getInputStream();
            Scanner scan= new Scanner(response);
            scan.useDelimiter("\\A");
            if(scan.hasNext()){
                String temp = scan.next();

                return temp;
            }

        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            assert connection != null;
            connection.disconnect();
        }
        return null;
    }
*/

    public static void getMovies_FUllInfo(Movie m,String response) throws JSONException{
        JSONObject json_result = new JSONObject(response);
        JSONObject json_images = json_result.getJSONObject(IMAGES_PATH);
        JSONObject json_videos = json_result.getJSONObject(VIDEOS_PATH);
        JSONObject json_reviews = json_result.getJSONObject(REVIEWS_PATH);
        MovieImages image = getMovieImages(json_images);
        String[] trailers = getTrailerKey(json_videos);
        Review[] reviews = getReviewURL(json_reviews);
        m.images = image;
        m.trailersKeys = trailers;
        m.reviews = reviews;
    }
    public static List<Movie> getListOfMovies(String Result) throws JSONException{

        List<Movie> ListOfMovies = new ArrayList<>();
        JSONObject jsonResult= new JSONObject(Result);
        JSONArray results = jsonResult.getJSONArray(GeneralData.Json_Result);
        for (int i = 0; i <results.length() ; i++) {
            JSONObject JsonMovie = results.getJSONObject(i);
            Movie temp = new Movie();
            temp.Movie_Id = JsonMovie.getString(GeneralData.Json_MOVIE_id);
            temp.OriginallTitle = JsonMovie.getString(GeneralData.Json_MOVIE_ORIGINAL_TITLE);
            temp.Overview = JsonMovie.getString(GeneralData.Json_MOVIE_OVERVIEW);
            temp.RelaseDate = JsonMovie.getString(GeneralData.Json_MOVIE_RELEASE_DATE);
            temp.Vote_Average = JsonMovie.getLong(GeneralData.Json_MOVIE_VOTE_AVERAGE);
            temp.images.imagePosterUrl = JsonMovie.getString(GeneralData.Json_MOVIE_POSTER_IMAGE);

            ListOfMovies.add(temp);
        }
        return ListOfMovies;
    }

    public static MovieImages getMovieImages(String result) throws JSONException {
        MovieImages movieImg = new MovieImages();
        JSONObject jsonResult = new JSONObject(result);
        JSONArray posters = jsonResult.getJSONArray(GeneralData.Json_Posters);
        JSONArray backdrops = jsonResult.getJSONArray(GeneralData.Json_backdrops);
        JSONObject poster = posters.getJSONObject(0);
        JSONObject backdrop = backdrops.getJSONObject(0);
        movieImg.imagePosterRatio = (float) poster.getDouble(GeneralData.Json_Image_Ratio);
        movieImg.imagePosterUrl = poster.getString(GeneralData.Json_Image_Path);
        movieImg.imageBackdropsRatio = (float) backdrop.getDouble(GeneralData.Json_Image_Ratio);
        movieImg.imageBackdropsUrl = backdrop.getString(GeneralData.Json_Image_Path);
        return  movieImg;
    }
    private static MovieImages getMovieImages(JSONObject result) throws JSONException {
        MovieImages movieImg = new MovieImages();
        JSONObject jsonResult = result;
        JSONArray posters = jsonResult.getJSONArray(GeneralData.Json_Posters);
        JSONArray backdrops = jsonResult.getJSONArray(GeneralData.Json_backdrops);
        JSONObject poster = posters.getJSONObject(0);
        JSONObject backdrop = backdrops.getJSONObject(0);
        movieImg.imagePosterRatio = (float) poster.getDouble(GeneralData.Json_Image_Ratio);
        movieImg.imagePosterUrl = poster.getString(GeneralData.Json_Image_Path);
        movieImg.imageBackdropsRatio = (float) backdrop.getDouble(GeneralData.Json_Image_Ratio);
        movieImg.imageBackdropsUrl = backdrop.getString(GeneralData.Json_Image_Path);
        return  movieImg;
    }

    public static String[] getTrailerKey(String result) throws  JSONException{
        JSONObject obj = new JSONObject(result);
        JSONArray jsonResult =obj.getJSONArray("results");
        String[] kes = new String[jsonResult.length()];
        for(int i = 0 ; i < jsonResult.length();i++){
            JSONObject trailer = jsonResult.getJSONObject(i);
            if(trailer.has(GeneralData.Json_MOVIE_TRAILER_KEY)){
                if(trailer.has(GeneralData.Json_MOVIE_TRAILER_SITE)
                        && trailer.getString(GeneralData.Json_MOVIE_TRAILER_SITE)
                        == GeneralData.Json_MOVIE_TRAILER_SITE_VALUE
                        ){
                    kes[i] = trailer.getString(GeneralData.Json_MOVIE_TRAILER_KEY);
                }

            }
        }
        return kes;
    }
    public static String[] getTrailerKey(JSONObject result) throws  JSONException{
        JSONObject obj = result;
        JSONArray jsonResult =obj.getJSONArray("results");
        String[] kes = new String[jsonResult.length()];
        for(int i = 0 ; i < jsonResult.length();i++){
            JSONObject trailer = jsonResult.getJSONObject(i);
            if(trailer.has(GeneralData.Json_MOVIE_TRAILER_KEY)){
                if(trailer.has(GeneralData.Json_MOVIE_TRAILER_SITE)
                        && trailer.getString(GeneralData.Json_MOVIE_TRAILER_SITE)
                        .equals(GeneralData.Json_MOVIE_TRAILER_SITE_VALUE)
                        ){
                    kes[i] = trailer.getString(GeneralData.Json_MOVIE_TRAILER_KEY);
                }
            }
        }
        return kes;
    }



    public static final String REVIEW_URL = "url";
    public static final String REVIEW_AUTHOR = "author";
    public static final String REVIEW_CONTENT = "content";

    public static List<Review> getReviewURL(String result) throws  JSONException{
        List<Review> reviews = new ArrayList<>();
        JSONObject obj = new JSONObject(result);
        JSONArray results = obj.getJSONArray("results");
        for (int i = 0 ; i <results.length(); i++){
           Review review = new Review();
            JSONObject Jsonreview = results.getJSONObject(i);
            if(Jsonreview.has(REVIEW_URL)){
                review.url = Jsonreview.getString(REVIEW_URL);
                GeneralData.mylog(review.url);
            }if(Jsonreview.has(REVIEW_AUTHOR)){
                review.author = Jsonreview.getString(REVIEW_AUTHOR);
            }if(Jsonreview.has(REVIEW_CONTENT)){
                review.content=Jsonreview.getString(REVIEW_CONTENT);
            }

            reviews.add(review);
        }
        return reviews;
    }
    public static Review[] getReviewURL(JSONObject result) throws  JSONException{

        JSONObject obj = result;
        JSONArray results = obj.getJSONArray("results");
        Review[] reviews = new Review[results.length()];
        for (int i = 0 ; i <results.length(); i++){
           Review review = new Review();
            JSONObject Jsonreview = results.getJSONObject(i);
            if(Jsonreview.has(REVIEW_URL)){
                review.url = Jsonreview.getString(REVIEW_URL);
                GeneralData.mylog(review.url);
            }if(Jsonreview.has(REVIEW_AUTHOR)){
                review.author = Jsonreview.getString(REVIEW_AUTHOR);
            }if(Jsonreview.has(REVIEW_CONTENT)){
                review.content=Jsonreview.getString(REVIEW_CONTENT);
            }
            reviews[i]= review;
        }
        return reviews;
    }

    public static boolean NetworkConnectivityAvilable(Context context){
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info =  cm.getActiveNetworkInfo();
        return info != null && info.isConnectedOrConnecting();
    }

}
