package com.abdelmun3m.movie_land.Widget;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.widget.ProgressBar;
import android.widget.RemoteViews;

import com.abdelmun3m.movie_land.Movie;
import com.abdelmun3m.movie_land.MoviesProvider.MoviesContract;
import com.abdelmun3m.movie_land.R;
import com.abdelmun3m.movie_land.utilities.DynamicHeightNetworkImageView;
import com.abdelmun3m.movie_land.utilities.MovieAPI;
import com.abdelmun3m.movie_land.utilities.NetworkSingleton;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;

import java.net.URL;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * Implementation of App Widget functionality.
 */
public class MovieWidget extends AppWidgetProvider {


    Movie currentMovie = null;
    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId, Bitmap img) {

        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.movie_widget);

        //TODO create services that will get online movie

        views.setImageViewBitmap(R.id.widget_poster_image,img);
        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            getRecommendedMovie(context,appWidgetManager, appWidgetId);
        }
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }

    public Movie getRecommendedMovie(final Context context, final AppWidgetManager appWidgetManager,
                                     final int appWidgetId){

        Cursor favorite =  context.getApplicationContext().getContentResolver()
                .query(MoviesContract.FavoriteMoviesEntity.FAVORITE_MOVIES_URI
                        ,null,null,null,
                        null,null);

        if(favorite.getCount() > 0) {
            favorite.moveToFirst();
            String id =favorite.getString(MoviesContract.FavoriteMoviesEntity.INDEX_COLUMN_MOVIE_ID);
            URL mUrl= MovieAPI.Build_Movie_Recommendation(id);
            StringRequest getMovies = new StringRequest(Request.Method.GET, mUrl.toString(),
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                List<Movie> listOfMovies = MovieAPI.getListOfMovies(response);
                                int index = new Random().nextInt(listOfMovies.size());
                                MovieWidget.this.currentMovie = listOfMovies.get(index);
                                NetworkSingleton.getInstance(context)
                                        .getImageLoader()
                                        .get(MovieWidget.this.currentMovie.getPosterUrl(),
                                                new ImageLoader.ImageListener() {
                                                    @Override
                                                    public void onResponse(ImageLoader.ImageContainer response, boolean isImmediate) {
                                                        Bitmap img = response.getBitmap();
                                                        updateAppWidget(context, appWidgetManager, appWidgetId,img);
                                                    }
                                                    @Override
                                                    public void onErrorResponse(VolleyError error) {
                                                    }
                                                });

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
            NetworkSingleton.getInstance(context).addToRequestQueue(getMovies,"WIDGET");

        }
        return  null;
    }



}

