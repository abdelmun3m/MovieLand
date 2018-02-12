package com.abdelmun3m.movie_land.Controlers;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.ProgressBar;

import com.abdelmun3m.movie_land.FavoriteMovieIntentTask.FavoriteMovieService;
import com.abdelmun3m.movie_land.FavoriteMovieIntentTask.FavoriteMovieTasks;
import com.abdelmun3m.movie_land.Movie;
import com.abdelmun3m.movie_land.R;
import com.abdelmun3m.movie_land.Review;
import com.abdelmun3m.movie_land.Views.ViewDetail;
import com.abdelmun3m.movie_land.utilities.DynamicHeightNetworkImageView;
import com.abdelmun3m.movie_land.utilities.MovieAPI;
import com.abdelmun3m.movie_land.utilities.NetworkSingleton;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;

import java.net.URL;
import java.util.List;

/**
 * Created by abdelmun3m on 03/02/18.
 */

public class ControllerDetail {


    private static final String TAG = ControllerMain.class.getSimpleName();
    private ViewDetail mView;
    private Context context ;
    public ControllerDetail(ViewDetail mView){
        this.mView = mView;
        this.context = mView;
    }


    public void retrieveTrailers(String movieID){
        URL mUrl= MovieAPI.Build_Movie_Trailer_Url(movieID);
        StringRequest getTrailers = new StringRequest(Request.Method.GET, mUrl.toString(),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            String[] trailerKey = MovieAPI.getTrailerKey(response);
                            mView.updateTrailersList(trailerKey);
                        } catch (JSONException e) {

                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        NetworkSingleton.getInstance(context).addToRequestQueue(getTrailers,TAG);
    }


    public void retriveReviews(String movieID){
        URL mUrl= MovieAPI.Build_Movies_Review_Url(movieID);
        StringRequest getTrailers = new StringRequest(Request.Method.GET, mUrl.toString(),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            List<Review> reviews =  MovieAPI.getReviewURL(response);
                            mView.updateReviewsList(null);
                        } catch (JSONException e) {

                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        NetworkSingleton.getInstance(context).addToRequestQueue(getTrailers,TAG);
    }

    public void IntentDeleteFavoriteMovie(String movieId) {
        Intent in = new Intent(context, FavoriteMovieService.class);
        in.setAction(FavoriteMovieTasks.DELETE_FAVORITE_MOVIE_TASK);
        in.putExtra(FavoriteMovieTasks.DELETE_MOVIE_INTENT_KEY,movieId);
        context.startService(in);
    }

    public void IntentInsertNewMovie(Movie movie) {
        Intent in = new Intent(context, FavoriteMovieService.class);
        in.setAction(FavoriteMovieTasks.INSERT_NEW_FAVORITE_MOVIE_TASK);
        in.putExtra(FavoriteMovieTasks.MOVIE_INTENT_KEY,movie);
        context.startService(in);
    }

    public void loadMovieInfo( final Movie m) {
        URL mUrl= MovieAPI.Build_MOVIE_INFO(m.Movie_Id);
        StringRequest getMovies = new StringRequest(Request.Method.GET, mUrl.toString(),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            MovieAPI.getMovies_FUllInfo(m,response);
                            mView.initializeTrailers();
                            mView.initializeReviews();

                        } catch (JSONException e) {
                            //TODO handel Exception
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
               //TODO handel Error
            }
        });
        NetworkSingleton.getInstance(context).addToRequestQueue(getMovies,TAG);
    }
}
