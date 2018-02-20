package com.abdelmun3m.movie_land.FavoriteMovieIntentTask;

import android.content.Context;

import android.os.Bundle;
import com.abdelmun3m.movie_land.Movie;
import com.abdelmun3m.movie_land.MoviesProvider.MoviesContract;
import com.abdelmun3m.movie_land.R;

import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.database.Cursor;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.LoaderManager.LoaderCallbacks;


import java.util.ArrayList;
import java.util.List;

/**
 * Created by abdelmun3m on 16/02/18.
 */

public abstract class FavoriteMoviesLoader implements LoaderManager.LoaderCallbacks<Cursor> {

    public static int loaderID =  111;
    private Context context;

    public FavoriteMoviesLoader(Context context){

        this.context = context;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        CursorLoader loader = new CursorLoader(this.context,
                MoviesContract.FavoriteMoviesEntity.FAVORITE_MOVIES_URI,
                null,
                null,
                null,
                null);

        return loader;


    }
/*
    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

        if(data != null && data.getCount() > 0){
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
        }

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
*/
}
