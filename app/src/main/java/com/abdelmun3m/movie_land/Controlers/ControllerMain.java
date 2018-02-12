package com.abdelmun3m.movie_land.Controlers;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import com.abdelmun3m.movie_land.GeneralData;
import com.abdelmun3m.movie_land.Movie;
import com.abdelmun3m.movie_land.MoviesProvider.MoviesContract;
import com.abdelmun3m.movie_land.MoviewRecyclerView.MoviesAdapter;
import com.abdelmun3m.movie_land.Views.ViewDetail;
import com.abdelmun3m.movie_land.R;
import com.abdelmun3m.movie_land.Views.ViewMain;
import com.abdelmun3m.movie_land.utilities.DynamicHeightNetworkImageView;
import com.abdelmun3m.movie_land.utilities.NetworkSingleton;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
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

    public static final String TAG = ControllerMain.class.getSimpleName();
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
                            mView.setDataloaded(true);
                            mView.updateAdapterData(listOfMovies);
                        } catch (JSONException e) {
                            mView.showErrorMsg(context.getString(R.string.json_error)+" "+e.getMessage());
                            mView.setDataloaded(false);
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                mView.showErrorMsg(context.getString(R.string.network_error));
                mView.setDataloaded(false);
            }
        });
        NetworkSingleton.getInstance(context).addToRequestQueue(getMovies,TAG);
    }

    public void setMovieImages(final Movie movie, final DynamicHeightNetworkImageView moviePoster, final ProgressBar loader, final boolean dualMode){
        String imagesUrl = MovieAPI.Build_Movies_Images_Url(movie.Movie_Id).toString();
        StringRequest getImages = new StringRequest(Request.Method.GET, imagesUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                                movie.images = MovieAPI.getMovieImages(response);
                                loadImage(loader,movie,moviePoster,dualMode);
/*
                                moviePoster.setImageUrl(movie.getPosterUrl(),
                                        NetworkSingleton.getInstance(moviePoster.getContext())
                                                .getImageLoader());
                                moviePoster.setAspectRatio(movie.images.imagePosterRatio);
                                loader.setVisibility(View.GONE);
*/

                        } catch (JSONException e) {
                            //TODO Loading Indicator
                            moviePoster.setDefaultImageResId(R.drawable.placeholder);
                            moviePoster.setAspectRatio(1f);
                           loader.setVisibility(View.GONE);
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //TODO SET PHOTOS PLACEHOLDER.
                moviePoster.setDefaultImageResId(R.drawable.placeholder);
                moviePoster.setAspectRatio(1f);
                loader.setVisibility(View.GONE);
            }
        });
        NetworkSingleton.getInstance(context).addToRequestQueue(getImages,TAG);
    }

    public  void loadImage(final ProgressBar pb , final Movie movie, final DynamicHeightNetworkImageView moviePoster, final boolean dualMode){

        String url = dualMode? movie.getBackdropsUrl():movie.getPosterUrl();
        moviePoster.setImageUrl(url,
                NetworkSingleton.getInstance(moviePoster.getContext())
                        .getImageLoader());
        if(dualMode){
            moviePoster.setAspectRatio(movie.images.imageBackdropsRatio);
        }
    }


    public void movieClicked(Movie m){
        //start details activity ;
        if(m != null){
            Intent in = new Intent(context,ViewDetail.class);
            in.putExtra(GeneralData.INTENT_TAG,m);
            context.startActivity(in);
        }
    }

    public void getMovieRecommendation(String movieId){
        URL mUrl= MovieAPI.Build_Movie_Recommendation(movieId);
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



    public void retrieveFavoriteMovies(Context context){
        Cursor query = context.getContentResolver()
                .query(MoviesContract.FavoriteMoviesEntity.FAVORITE_MOVIES_URI,null,null,null,null,null);
        if(query.getCount() > 0){
            mView.updateAdapterData(query);
        }else {
           mView.showErrorMsg(context.getString(R.string.favorit_error));
        }

    }
}
