package com.abdelmun3m.movie_land.Controlers;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.abdelmun3m.movie_land.FavoriteMovieIntentTask.FavoriteMoviesLoader;
import com.abdelmun3m.movie_land.GeneralData;
import com.abdelmun3m.movie_land.Movie;
import com.abdelmun3m.movie_land.MoviesProvider.MoviesContract;
import com.abdelmun3m.movie_land.Presenters.ViewMainPresenter;
import com.abdelmun3m.movie_land.Views.ViewDetail;
import com.abdelmun3m.movie_land.R;
import com.abdelmun3m.movie_land.Views.ViewMain;

import com.abdelmun3m.movie_land.utilities.DynamicHeightNetworkImageView;
import com.abdelmun3m.movie_land.utilities.NetworkSingleton;
import com.abdelmun3m.movie_land.utilities.Utils;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.abdelmun3m.movie_land.utilities.MovieAPI;
import org.json.JSONException;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by abdelmun3m on 22/01/18.
 *
 * this class is a P from the MVP Design pattern for the ViewMain
 * that controls the main activity.
 */

public class ControllerMain {

    public static final String TAG = ControllerMain.class.getSimpleName();
    private ViewMainPresenter mView;
    private Context context;


    public ControllerMain(){
    }

    public ControllerMain(ViewMainPresenter mView){
        this.mView = mView;
        this.context = mView.getContext();
    }

    public void retrieveMovies(int category, int page, final Boolean append){
        URL mUrl= MovieAPI.Build_Movies_Category_URL(category,page);
        StringRequest getMovies = new StringRequest(Request.Method.GET, mUrl.toString(),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            List<Movie> listOfMovies = MovieAPI.getListOfMovies(response);
                            mView.setDataloaded(true);
                            mView.updateAdapterData(listOfMovies,append);
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

    public void movieClicked(Movie m,View moviePoster){
        //start details activity ;
        if(m != null){
            Intent in = new Intent(context,ViewDetail.class);
            in.putExtra(GeneralData.INTENT_TAG,m);
            ActivityOptionsCompat options = ActivityOptionsCompat
                    .makeSceneTransitionAnimation(mView.getActivity(),moviePoster,"poster");
            context.startActivity(in,options.toBundle());
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
                            mView.appendMovieRecommendation(listOfMovies);
                        } catch (JSONException e) {
                            //mView.showErrorMsg(context.getString(R.string.json_error));
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //mView.showErrorMsg(context.getString(R.string.network_error));
            }
        });
        NetworkSingleton.getInstance(context).addToRequestQueue(getMovies,TAG);
    }

    public void getRecommendation(){
      mView.getActivity().getSupportLoaderManager().restartLoader(FavoriteMoviesLoader.loaderID, null,
              new FavoriteMoviesLoader(context) {
                  @Override
          public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
              if(data != null  && data.getCount() > 0){
                  int count = 0;
                  while (data.moveToNext()){
                      String id =
                              data.getString(MoviesContract.FavoriteMoviesEntity.INDEX_COLUMN_MOVIE_ID);
                      getMovieRecommendation(id);
                      count++;
                      if(count > 5) break;
                  }
              }else{
                  //retrieve rated movies in case no favorite movies;
                  Utils.showAlert(context,context.getString(R.string.no_favorite_title),
                          context.getString(R.string.no_favorite_message));
                  Toast.makeText(mView.getContext(), "show top", Toast.LENGTH_SHORT).show();
                  retrieveMovies(ViewMain.TOP_RATED_LIST_SELECTION,GeneralData.DEFAULT_PAGE_NUMBER,false);
              }
          }

          @Override
          public void onLoaderReset(Loader<Cursor> loader) {

          }
      });
    }

    public void retrieveFavoriteMovies(final Context context){
        mView.getActivity().getSupportLoaderManager().restartLoader(FavoriteMoviesLoader.loaderID, null,
                new FavoriteMoviesLoader(context) {
                    @Override
            public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
                if(data != null && data.getCount() > 0){
                    List<Movie> myList = new ArrayList<>();
                    while (data.moveToNext()){
                        Movie movie = new Movie();
                        movie.Movie_Id = data.getString(MoviesContract.FavoriteMoviesEntity.INDEX_COLUMN_MOVIE_ID);
                        movie.OriginallTitle = data.getString(MoviesContract.FavoriteMoviesEntity.INDEX_COLUMN_ORIGINAL_TITLE);
                        movie.Overview = data.getString(MoviesContract.FavoriteMoviesEntity.INDEX_COLUMN_OVERVIEW);
                        movie.images.imagePosterUrl=data.getString(MoviesContract.FavoriteMoviesEntity.INDEX_COLUMN_POSTER_IMAGE);
                        movie.images.imageBackdropsUrl = data.getString(MoviesContract.FavoriteMoviesEntity.INDEX_COLUMN_BACKDROP);
                        movie.images.imageBackdropsRatio =1.77f;
                        movie.RelaseDate = data.getString(MoviesContract.FavoriteMoviesEntity.INDEX_COLUMN_RELEASE_DATE);
                        movie.Vote_Average = data.getLong(MoviesContract.FavoriteMoviesEntity.INDEX_COLUMN_VOTE_RATE);
                        if(movie != null){
                            myList.add(movie);
                        }
                    }
                    data.close();
                    mView.updateAdapterData(myList,false);
                }else{

                    mView.showErrorMsg(context.getString(R.string.favorit_error));
                }

            }
            @Override
            public void onLoaderReset(Loader<Cursor> loader) {

            }
        });
    }

}
