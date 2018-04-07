package com.abdelmun3m.movie_land.Presenters;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.widget.ProgressBar;

import com.abdelmun3m.movie_land.Movie;
import com.abdelmun3m.movie_land.MoviewRecyclerView.MoviesAdapter;
import com.abdelmun3m.movie_land.utilities.DynamicHeightNetworkImageView;

import java.util.List;

/**
 * Created by abdelmun3m on 07/04/18.
 */

public interface ViewMainPresenter extends MoviesAdapter.MovieClick{
     void loadPoster(Movie m, DynamicHeightNetworkImageView moviePoster, ProgressBar loader);
     void setLayoutManager();
     void showRecyclerView();
     void setDataloaded(Boolean loaded);
     void updateAdapterData(List<Movie> movies, boolean append);
     void appendMovieRecommendation(List<Movie> movies);
     void showErrorMsg(String msg);
     Context getContext();
     AppCompatActivity getActivity();
     void loadNextPage();
}
