package com.abdelmun3m.movie_land;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toolbar;

import com.abdelmun3m.movie_land.FavoriteMovieIntentTask.FavoriteMovieService;
import com.abdelmun3m.movie_land.MoviesProvider.*;
import com.abdelmun3m.movie_land.FavoriteMovieIntentTask.FavoriteMovieTasks;
import com.abdelmun3m.movie_land.MoviewRecyclerView.ReviewAdapter;
import com.abdelmun3m.movie_land.MoviewRecyclerView.TrailerRecyclerView;
import com.abdelmun3m.movie_land.utilities.DynamicHeightNetworkImageView;
import com.abdelmun3m.movie_land.utilities.NetworkSingleton;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;

import org.json.JSONException;

import java.net.URL;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MovieDetails extends AppCompatActivity implements TrailerRecyclerView.trailerClicke ,
        LoaderManager.LoaderCallbacks<String[]>,ReviewAdapter.reviewClick{


    private static final int LOADER_ID = 101 ;
    private Movie CurrentMovie = null;

    @BindView(R.id.tv_md_Originaltitle)
            TextView originaltitle;
    @BindView(R.id.tv_md_overview)
            TextView overView;
    @BindView(R.id.tv_md_vote_average)
            TextView voteAverage;
    @BindView(R.id.tv_md_ReleaseDate)
            TextView releaseDate;
    @BindView(R.id.tv_review_error)
            TextView reviewError;
    @BindView(R.id.img_star_movie)
            ImageView star;
    @BindView(R.id.rv_reviews)
            RecyclerView mReviewRecyclerView;
    @BindView(R.id.rv_trailers)
            RecyclerView mTreilerRecyclerView;
    @BindView(R.id.backdrops)
            NetworkImageView backdrop;
    @BindView(R.id.loader)
            ProgressBar loader;

    TrailerRecyclerView adapter;
    ReviewAdapter rAdapter;

    private static final int REVIEW_MANAGER = 1;
    private static final int TRAILER_MANAGER = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.details);
        ButterKnife.bind(this);
        Intent in = getIntent();

       // Toolbar mToolbar = (Toolbar)findViewById(R.id.details_toolbar);
        if(in.hasExtra(GeneralData.INTENT_TAG)){
            CurrentMovie = in.getParcelableExtra(GeneralData.INTENT_TAG);
        }

        if(CurrentMovie != null){


            //mToolbar.setTitle(CurrentMovie.OriginallTitle);
            originaltitle.setText(CurrentMovie.OriginallTitle);
            releaseDate.setText("Release "+CurrentMovie.RelaseDate);
            voteAverage.setText("Rate "+String.valueOf(CurrentMovie.Vote_Average));
            overView.setText(CurrentMovie.Overview);

            CurrentMovie.Movie_DB_ID = getMovieDbId(CurrentMovie.Movie_Id);
            if(CurrentMovie.Movie_DB_ID > -1){
                setFavoritMovie(true);
                CurrentMovie.favorite = 1;
            }else {
                setFavoritMovie(false);
                CurrentMovie.favorite = 0;
            }

/*            star.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(CurrentMovie.favorite == 0){
                        setFavoritMovie(true);
                        CurrentMovie.favorite = 1;
                        IntentInsertNewMovie();
                    }else {
                        setFavoritMovie(false);
                        CurrentMovie.favorite = 0;
                      IntentDeleteFavoriteMovie();
                    }
                }
            });*/
        }


        NetworkSingleton.getInstance(this).getImageLoader().get(CurrentMovie.getPosterUrl(), new ImageLoader.ImageListener() {
            @Override
            public void onResponse(ImageLoader.ImageContainer response, boolean isImmediate) {
                Bitmap img = response.getBitmap();
                if(img != null){
                    loader.setVisibility(View.GONE);
                    backdrop.setImageBitmap(img);
                }


            }

            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        backdrop.setImageUrl(CurrentMovie.getPosterUrl(),
                NetworkSingleton.getInstance(this).getImageLoader());
       // backdrop.setAspectRatio(CurrentMovie.images.imageBackdropsRatio);
        //backdrop.setAspectRatio(.5f);


        mTreilerRecyclerView = (RecyclerView) findViewById(R.id.rv_trailers);
        adapter = new TrailerRecyclerView(this);
        mTreilerRecyclerView.setAdapter(adapter);
        GridLayoutManager  manager =new GridLayoutManager(this,2, LinearLayoutManager.VERTICAL,false);
        mTreilerRecyclerView.setLayoutManager(manager);
        mTreilerRecyclerView.setHasFixedSize(true);


        mReviewRecyclerView = (RecyclerView) findViewById(R.id.rv_reviews);
        rAdapter = new ReviewAdapter(this);
        mReviewRecyclerView.setAdapter(rAdapter);
        GridLayoutManager manager2 =new GridLayoutManager(this,2, LinearLayoutManager.VERTICAL,false);
        mReviewRecyclerView.setLayoutManager(manager2);
        mReviewRecyclerView.setHasFixedSize(true);

        getSupportLoaderManager().initLoader(LOADER_ID,null,this);
        if(MovieAPI.NetworkConnectivityAvilable(this)){
            new ReviewLoadtask().execute();
        }
    }
    @Override
    public void onTrailerClieck(String url) {
        openWebPage(url);
    }

    @Override
    public void onReviewClick(String url) {
        GeneralData.mylog(url);
        openWebPage(url);
    }

    @Override
    public Loader<String[]> onCreateLoader(int id, Bundle args) {
        return new AsyncTaskLoader<String[]>(this) {
            @Override
            protected void onStartLoading() {
                    forceLoad();
            }

            @Override
            public void deliverResult(String[] data) {super.deliverResult(data);}

            @Override
            public String[] loadInBackground() {
                URL url = MovieAPI.Build_Movie_Trailer_Url(String.valueOf(CurrentMovie.Movie_Id));
                GeneralData.mylog(""+url);
                String response = MovieAPI.get_Response(url,MovieDetails.this);
                if(response == null || response.equals("")){
                    return null;}
                try {return MovieAPI.getTrailerKey(response);}
                catch (JSONException e)
                    {e.printStackTrace();}
                return null;
            }
        };

    }

    @Override
    public void onLoadFinished(Loader<String[]> loader, String[] data) {
        if(data != null){
            adapter.updateTrailers(data);
        }
    }

    @Override
    public void onLoaderReset(Loader<String[]> loader) {
    }


    public void openWebPage(String url) {
        Uri webpage = Uri.parse(url);
        Intent intent = new Intent(Intent.ACTION_VIEW, webpage);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }


    public void setFavoritMovie(boolean favorite){
        /*if(favorite){
            star.setImageResource(android.R.drawable.btn_star_big_on);
        }else {
            star.setImageResource(android.R.drawable.btn_star_big_off);
        }*/
    }

    public long getMovieDbId(String id){
        Uri uri = MoviesContract.FavoriteMoviesEntity.BuildMovieUriWithId(id);
        Cursor query = getContentResolver().query(uri, null, null, null, null);
       if (query != null && query.getCount() <= 0) return -1;
        query.moveToFirst();
        long aLong = query.getLong(MoviesContract.FavoriteMoviesEntity.INDEX_COLUMN_MOVIE_DB_ID);
        query.close();
        return aLong;
    }

    private void IntentDeleteFavoriteMovie() {
        Intent in = new Intent(MovieDetails.this, FavoriteMovieService.class);
        in.setAction(FavoriteMovieTasks.DELETE_FAVORITE_MOVIE_TASK);
        in.putExtra(FavoriteMovieTasks.DELETE_MOVIE_INTENT_KEY,CurrentMovie.Movie_Id);
        startService(in);
    }

    private void IntentInsertNewMovie() {
        Intent in = new Intent(MovieDetails.this, FavoriteMovieService.class);
        in.setAction(FavoriteMovieTasks.INSERT_NEW_FAVORITE_MOVIE_TASK);
        in.putExtra(FavoriteMovieTasks.MOVIE_INTENT_KEY,CurrentMovie);
        startService(in);
    }


    private class ReviewLoadtask extends AsyncTask<Void,Void,List<Review>> {

        @Override
        protected List<Review> doInBackground(Void... params) {
            URL review = MovieAPI.Build_Movies_Review_Url(CurrentMovie.Movie_Id);
            GeneralData.mylog(review.toString());
            String response = MovieAPI.get_Response(review,MovieDetails.this);
            if(response == null || response.equals("")){
                return null;
            }
            try {
                return MovieAPI.getReviewURL(response);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(List<Review> reviews) {
            super.onPostExecute(reviews);
            if(reviews != null){
                if(reviews.size() > 0){
                    rAdapter.updateReviews(reviews);
                   mReviewRecyclerView.setVisibility(View.VISIBLE);
                   reviewError.setVisibility(View.INVISIBLE);
                }else{
                    mReviewRecyclerView.setVisibility(View.INVISIBLE);
                    reviewError.setVisibility(View.VISIBLE);
                }
            }else{
                mReviewRecyclerView.setVisibility(View.INVISIBLE);
              reviewError.setVisibility(View.VISIBLE);
            }
        }
    }
}
