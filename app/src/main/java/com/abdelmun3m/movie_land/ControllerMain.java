package com.abdelmun3m.movie_land;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.abdelmun3m.movie_land.utilities.DynamicHeightNetworkImageView;
import com.abdelmun3m.movie_land.utilities.NetworkSingleton;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.abdelmun3m.movie_land.utilities.MovieAPI;
import org.json.JSONException;

import java.net.URL;
import java.util.List;

/**
 * Created by abdelmun3m on 22/01/18.
 *
 * this class is a P from the MVP Design pattern for the ViewMain
 * that controls the main activity.
 */

public class ControllerMain {

    public static final String TAG = "Main";
    private ViewMain mView;
    private Context context;
    public ControllerMain(){
    }

    public ControllerMain(ViewMain mView){
        this.mView = mView;
        this.context = mView;
    }


    public void retrieveMovies(int category){
        URL mUrl= MovieAPI.Build_Movies_Category_URL(category);
        StringRequest getMovies = new StringRequest(Request.Method.GET, mUrl.toString(),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            List<Movie> listOfMovies = MovieAPI.getListOfMovies(response);
                            mView.updateAdapterData(listOfMovies);
                        } catch (JSONException e) {
                            mView.showErrorMsg(context.getString(R.string.json_error));
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                mView.showErrorMsg(context.getString(R.string.network_error));
            }
        });
        NetworkSingleton.getInstance(context).addToRequestQueue(getMovies,TAG);
    }

    public void setMovieImages(final Movie movie, final DynamicHeightNetworkImageView moviePoster){
        String imagesUrl = MovieAPI.Build_Movies_Images_Url(movie.Movie_Id).toString();
        StringRequest getImages = new StringRequest(Request.Method.GET, imagesUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                               movie.images = MovieAPI.getMovieImages(response);

                                moviePoster.setImageUrl(movie.getPosterUrl(),
                                        NetworkSingleton.getInstance(moviePoster.getContext()).getImageLoader());
                                Log.d("A7A",""+movie.getBackdropsUrl());
                                moviePoster.setAspectRatio(movie.images.imagePosterRatio);

                        } catch (JSONException e) {
                            //TODO Loading Indicator
                            moviePoster.setDefaultImageResId(R.drawable.placeholder);
                            moviePoster.setAspectRatio(1f);
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //TODO SET PHOTOS PLACEHOLDER.
                moviePoster.setDefaultImageResId(R.drawable.placeholder);
                moviePoster.setAspectRatio(1f);
            }
        });
        NetworkSingleton.getInstance(context).addToRequestQueue(getImages,TAG);
    }


    public void movieClicked(Movie m){
        //start details activity ;
        if(m != null){
            Intent in = new Intent(context,MovieDetails.class);
            in.putExtra(GeneralData.INTENT_TAG,m);
            if(m.images != null){
                Log.d("A7A"," brfore Not Null");
            }else{

                Log.d("A7A","before Null");
            }
            context.startActivity(in);
        }
    }
}
