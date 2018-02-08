package com.abdelmun3m.movie_land.MoviewRecyclerView;

import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.abdelmun3m.movie_land.Movie;
import com.abdelmun3m.movie_land.MoviesProvider.MoviesContract;
import com.abdelmun3m.movie_land.R;
import com.abdelmun3m.movie_land.Views.ViewMain;
import com.abdelmun3m.movie_land.utilities.DynamicHeightNetworkImageView;
import com.android.volley.toolbox.NetworkImageView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MoviesAdapter extends RecyclerView.Adapter<MoviesAdapter.MoviesViewHolder>{


    private List<Movie> MovieList = new ArrayList<>();
    private MovieClick movieClick =null;
    private ViewMain mView ;
    Cursor mCursor;
    public MoviesAdapter(ViewMain c){
        this.movieClick = c;
        this.mView = c;
    }


    public void convertCursorToMovies(Cursor mCursorData){
        List<Movie> myList = new ArrayList<>();
        while (mCursorData.moveToNext()){
            Movie movie = new Movie();
            movie.Movie_Id = mCursorData.getString(MoviesContract.FavoriteMoviesEntity.INDEX_COLUMN_MOVIE_ID);
            movie.OriginallTitle = mCursorData.getString(MoviesContract.FavoriteMoviesEntity.INDEX_COLUMN_ORIGINAL_TITLE);
            movie.Overview = mCursorData.getString(MoviesContract.FavoriteMoviesEntity.INDEX_COLUMN_OVERVIEW);
            movie.PosterImage=mCursorData.getString(MoviesContract.FavoriteMoviesEntity.INDEX_COLUMN_POSTER_IMAGE);
            movie.RelaseDate = mCursorData.getString(MoviesContract.FavoriteMoviesEntity.INDEX_COLUMN_RELEASE_DATE);
            movie.Vote_Average = mCursorData.getLong(MoviesContract.FavoriteMoviesEntity.INDEX_COLUMN_VOTE_RATE);
            if(movie != null){
                myList.add(movie);
            }
        }
        mCursorData.close();
        UpdateListOfMovies(myList);
    }

    public void UpdateListOfMovies(List<Movie> newList){
        this.MovieList = newList;
        notifyDataSetChanged();
    }
    @Override
    public MoviesViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        MoviesViewHolder mViewHolder;
        View mView = LayoutInflater.from(parent.getContext()).inflate(R.layout.movie_card,parent,false);
        mViewHolder = new MoviesViewHolder(mView);
        return mViewHolder;
    }

    //Movie mo = new Movie();
    @Override
    public void onBindViewHolder(MoviesViewHolder holder, int position) {
        holder.bind(MovieList.get(position));
      //  holder.bind(mo);
    }

    @Override
    public int getItemCount() {
        return MovieList.size();
    }




    /**
     *
     *  class MoviewViewHolder
     * **/

    public class MoviesViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{


        Movie currentMovie;
        @BindView(R.id.card_poster)
            DynamicHeightNetworkImageView moviePoster ;
        @BindView(R.id.card_title)
            TextView originalTitle;
        @BindView(R.id.loader)
            ProgressBar loader;

        public MoviesViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
            itemView.setOnClickListener(this);
        }
        public void bind(Movie m){
            this.currentMovie = m;
            if(originalTitle == null || originalTitle.equals("")){
                originalTitle.setText("NO Title");
            }else{
                originalTitle.setText(m.OriginallTitle);
            }
            mView.loadPoster(m,moviePoster,loader);
        }
        @Override
        public void onClick(View v) {
            if(v.getId() == itemView.getId()) movieClick.onMovieClick(this.currentMovie);

        }
    }

    /**
     *
     * Click Event Listener interface
     * **/

    public interface MovieClick{
        void onMovieClick(Movie m);
    }
}
