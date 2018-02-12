package com.abdelmun3m.movie_land.Views;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.design.widget.AppBarLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.abdelmun3m.movie_land.Controlers.ControllerDetail;
import com.abdelmun3m.movie_land.GeneralData;
import com.abdelmun3m.movie_land.Movie;
import com.abdelmun3m.movie_land.MoviesProvider.*;
import com.abdelmun3m.movie_land.MoviewRecyclerView.ReviewAdapter;
import com.abdelmun3m.movie_land.MoviewRecyclerView.TrailerRecyclerView;
import com.abdelmun3m.movie_land.R;
import com.abdelmun3m.movie_land.Review;
import com.abdelmun3m.movie_land.utilities.ImageLoaderHelper;
import com.abdelmun3m.movie_land.utilities.NetworkSingleton;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ViewDetail extends AppCompatActivity implements TrailerRecyclerView.trailerClicke ,
        ReviewAdapter.reviewClick{


    private static final int LOADER_ID = 101 ;
    private Movie CurrentMovie = null;
    private ControllerDetail controler;

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
    @BindView(R.id.poster)
            NetworkImageView poster;
    @BindView(R.id.loader)
            ProgressBar loader;
    @BindView(R.id.detailAppBar)
            AppBarLayout appBar;
    TrailerRecyclerView adapter;
    ReviewAdapter rAdapter;

    private static final int REVIEW_MANAGER = 1;
    private static final int TRAILER_MANAGER = 1;

    //TODO remember to update your coude to retrive the full movie info in this activity not in main
    // we don't need the all info about movie in the main view we don't need it for each movie

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.details);
        ButterKnife.bind(this);


        if(savedInstanceState == null){
            appBar.setExpanded(true);
            Intent in = getIntent();
            if(in.hasExtra(GeneralData.INTENT_TAG)){
                CurrentMovie = in.getParcelableExtra(GeneralData.INTENT_TAG);
            }
        }else{
            appBar.setExpanded(false);
            CurrentMovie = savedInstanceState.getParcelable(getString(R.string.vd_currentMovie_key));
        }

        if(CurrentMovie != null){
            originaltitle.setText(CurrentMovie.OriginallTitle);

            //ToDo ReDesign ReleaseDate And Vote Average ar remove strings
            releaseDate.setText("Release "+CurrentMovie.RelaseDate);
            voteAverage.setText("Rate "+String.valueOf(CurrentMovie.Vote_Average));
            overView.setText(CurrentMovie.Overview);
            CurrentMovie.Movie_DB_ID = getMovieDbId(CurrentMovie.Movie_Id);
            controler = new ControllerDetail(this);

            if(savedInstanceState == null){
                controler.loadMovieInfo(CurrentMovie);
            }else{
                initializeReviews();
                initializeTrailers();
            }


            if(CurrentMovie.Movie_DB_ID > -1){
                setFavoritMovie(true);
                CurrentMovie.favorite = 1;
            }else {
                setFavoritMovie(false);
                CurrentMovie.favorite = 0;
            }


            loadPosterImage();


         star.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(CurrentMovie.favorite == 0){
                        setFavoritMovie(true);
                        CurrentMovie.favorite = 1;
                        controler.IntentInsertNewMovie(CurrentMovie);
                    }else {
                        setFavoritMovie(false);
                        CurrentMovie.favorite = 0;
                      controler.IntentDeleteFavoriteMovie(CurrentMovie.Movie_Id);
                    }
                }
            });

        }
}

    public void initializeReviews() {
        mReviewRecyclerView = (RecyclerView) findViewById(R.id.rv_reviews);
        rAdapter = new ReviewAdapter(this);
        mReviewRecyclerView.setAdapter(rAdapter);
        GridLayoutManager manager2 =new GridLayoutManager(
                this,
                1,
                LinearLayoutManager.VERTICAL,
                false);
        mReviewRecyclerView.setLayoutManager(manager2);
        mReviewRecyclerView.setHasFixedSize(true);
        updateReviewsList(CurrentMovie.reviews);
    }

    private void loadPosterImage() {

       // poster.setImageResource(R.drawable.movie_green);
        /*ImageLoaderHelper.getInstance(this).getImageLoader().get(CurrentMovie.getPosterUrl(), new ImageLoader.ImageListener() {
            @Override
            public void onResponse(ImageLoader.ImageContainer response, boolean isImmediate) {
                Bitmap img = response.getBitmap();
                if(img != null){
                    Toast.makeText(ViewDetail.this, ""+CurrentMovie.getPosterUrl(), Toast.LENGTH_SHORT).show();
                    loader.setVisibility(View.GONE);
                    poster.setVisibility(View.VISIBLE);
                    poster.setImageBitmap(response.getBitmap());

                }
            }
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(ViewDetail.this, ""+error, Toast.LENGTH_SHORT).show();
            }
        });*/

             poster.setImageUrl(CurrentMovie.getPosterUrl(),NetworkSingleton.getInstance(this)
               .getImageLoader());
             loader.setVisibility(View.GONE);
    }


    public void initializeTrailers(){
        adapter = new TrailerRecyclerView(this);
        mTreilerRecyclerView.setAdapter(adapter);
        GridLayoutManager  manager =new GridLayoutManager(
                this,
                2,
                LinearLayoutManager.VERTICAL,
                false);
        mTreilerRecyclerView.setLayoutManager(manager);
        mTreilerRecyclerView.setHasFixedSize(true);
        updateTrailersList(CurrentMovie.trailersKeys);
    }


    @Override
    public void onTrailerClick(String movieKey) {
        Intent i = new Intent(this,MovieDisplayActivity.class);
        i.putExtra(this.getString(R.string.movie_Video_Key),movieKey);
        startActivity(i);
    }

    @Override
    public void onReviewClick(String url) {
        openWebPage(url);
    }


    public void updateTrailersList(String[] data){
        if(data != null && data.length > 0){
            adapter.updateTrailers(data);
        }
    }

    public void updateReviewsList(Review[] reviews){
        if(reviews != null && reviews.length > 0){
                rAdapter.updateReviews(reviews);
                mReviewRecyclerView.setVisibility(View.VISIBLE);
                reviewError.setVisibility(View.INVISIBLE);
            }else{
            mReviewRecyclerView.setVisibility(View.INVISIBLE);
            reviewError.setVisibility(View.VISIBLE);
        }
    }


    public void openWebPage(String url) {
        Uri webpage = Uri.parse(url);
        Intent intent = new Intent(Intent.ACTION_VIEW, webpage);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }


    public void setFavoritMovie(boolean favorite){
        if(favorite){
            star.setImageResource(android.R.drawable.btn_star_big_on);
        }else {
            star.setImageResource(android.R.drawable.btn_star_big_off);
        }
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

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(getString(R.string.vd_currentMovie_key),CurrentMovie);
    }
}
