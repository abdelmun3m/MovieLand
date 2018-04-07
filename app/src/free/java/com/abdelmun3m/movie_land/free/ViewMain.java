package com.abdelmun3m.movie_land.free;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.abdelmun3m.movie_land.Controlers.ControllerMain;
import com.abdelmun3m.movie_land.GeneralData;
import com.abdelmun3m.movie_land.MoviewRecyclerView.MoviesAdapter;
import com.google.android.gms.ads.MobileAds;


import com.abdelmun3m.movie_land.Movie;
import com.abdelmun3m.movie_land.R;
import com.abdelmun3m.movie_land.utilities.DynamicHeightNetworkImageView;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;


import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.abdelmun3m.movie_land.GeneralData.CURRENT_PAGE;
import static com.abdelmun3m.movie_land.Views.ViewMain.LAST_CATEGORY;

public class ViewMain extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        com.abdelmun3m.movie_land.Presenters.ViewMainPresenter {




    //--------------------------------------
    private static final int POPULAR_LIST_SELECTION = 2;
    private static final int TOP_RATED_LIST_SELECTION = 1;
    private static final int FAVORITE_MOVIES_SELECTION = 3;
    private int currentCategory = TOP_RATED_LIST_SELECTION;

    //----------------------------------------

    private ControllerMain controller;
    private MoviesAdapter mAdapter;
    @BindView(R.id.rv_moviesView2)
    RecyclerView mRecyclerView ;
    @BindView(R.id.tv_ErrorMessage)
    TextView mErrorMessageField;


    GridLayoutManager manager;
    private Parcelable mListState;
    private boolean dataloaded = false;
    List<Movie> movieList ;
    private boolean dualMode = true;
    private static final String FreeVersionID = "com.abdelmun3m.movie_land.free";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_main);
        ButterKnife.bind(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        int height = size.y;
        MobileAds.initialize(this, GeneralData.ADS_ID);
        AdView mAdView = (AdView)findViewById(R.id.addView);
            mAdView.setVisibility(View.VISIBLE);

        AdRequest adRequest = new AdRequest.Builder()
                    .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                    .build();
        mAdView.loadAd(adRequest);


        if(width < 800){
            //display menue selection beside actitvity title to display drawable
            dualMode = false;
            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                    this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
            drawer.addDrawerListener(toggle);
            toggle.syncState();


        }
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //------------------------------------------------------------------------
        controller = new ControllerMain(this);
        if(savedInstanceState == null) {
            setLayoutManager();
            controller.retrieveMovies(currentCategory,GeneralData.DEFAULT_PAGE_NUMBER,false);
        }
    }

    public void setDataloaded(Boolean loaded){
        this.dataloaded = loaded;
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (!dualMode && drawer.isDrawerOpen(GravityCompat.START) ) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.view_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        if (id == R.id.top_rated) {
            // Handle the camera action
            currentCategory = TOP_RATED_LIST_SELECTION;
            controller.retrieveMovies(currentCategory, GeneralData.DEFAULT_PAGE_NUMBER,false);
        } else if (id == R.id.popular_movies) {
            currentCategory = POPULAR_LIST_SELECTION;
            controller.retrieveMovies(currentCategory,GeneralData.DEFAULT_PAGE_NUMBER,false);
        } else if (id == R.id.recommended) {
            controller.getRecommendation();
        }else if(id == R.id.favorite){
            currentCategory = FAVORITE_MOVIES_SELECTION;
            controller.retrieveFavoriteMovies(this);
        }else if (id == R.id.about) {

        }

        mRecyclerView.scrollToPosition(0);

        if(!dualMode){
            //close navigation after selection
            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            drawer.closeDrawer(GravityCompat.START);
        }
        return true;
    }

    /**
     * used to change the background color of
     * drawer list items according to the current selected category
     * @input index represent index of item in the list
     * @output void
     * */
    public void updateAdapterData(List<Movie> movies,boolean append){

            if(mAdapter == null){
                mAdapter = new MoviesAdapter(null);
            }
            mAdapter.UpdateListOfMovies(movies,append);
            loading=false;
            showRecyclerView();
    }


    public void appendMovieRecommendation(List<Movie> movies){

        if(movies == null) return;
        if(mAdapter == null){
            mAdapter = new MoviesAdapter(null);
        }
        mAdapter.appendListOfRecommendations(movies);
        movieList = mAdapter.getMovieList();
        showRecyclerView();

    }

    public void showErrorMsg(String msg){
        mRecyclerView.setVisibility(View.GONE);
        mErrorMessageField.setVisibility(View.VISIBLE);
        mErrorMessageField.setText(msg);
    }

    @Override
    public Context getContext() {
        return this;
    }

    @Override
    public AppCompatActivity getActivity() {
        return this;
    }

    @Override
    public void loadNextPage() {
       // Toast.makeText(this, ""+GeneralData.TOTAL_PAGES, Toast.LENGTH_SHORT).show();
        if(CURRENT_PAGE < GeneralData.TOTAL_PAGES){
            loading = true;
            CURRENT_PAGE++;
            controller.retrieveMovies(currentCategory,CURRENT_PAGE,true);
        }else {
            Toast.makeText(this, "No More Movies ", Toast.LENGTH_SHORT).show();
            loading = true;
        }

    }

    public void showRecyclerView(){

        mRecyclerView.setVisibility(View.VISIBLE);
        mErrorMessageField.setVisibility(View.GONE);
    }
    boolean loading = false;
    public void setLayoutManager(){
        int vertical = LinearLayoutManager.VERTICAL;
        int spanCount =  (dualMode) ? 2 : 2;

        manager =new GridLayoutManager(this, spanCount, vertical,false);
        mRecyclerView.setLayoutManager(manager);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if(dy > 0) //check for scroll down
                {
                   int  visibleItemCount = manager.getChildCount();
                    int totalItemCount = manager.getItemCount();
                    int pastVisiblesItems = manager.findFirstVisibleItemPosition();
                        if(!loading){
                            if ( (visibleItemCount + pastVisiblesItems) >= totalItemCount)
                            {
                                Toast.makeText(ViewMain.this,  "loading ..", Toast.LENGTH_SHORT).show();
                                loadNextPage();
                                //Do pagination.. i.e. fetch new data

                            }
                        }
                    
                }
            }
        });
        mAdapter = new MoviesAdapter(this);
        mRecyclerView.setAdapter(mAdapter);
    }

    public synchronized void loadPoster(Movie m, DynamicHeightNetworkImageView moviePoster, ProgressBar loader) {
            //controller.setMovieImages(m,moviePoster,loader);
       // if(!dualMode){
            controller.loadImage(loader,m,moviePoster,false);
        //}
//        else{
//            controller.setMovieImages(m,moviePoster,loader,dualMode);
//        }

    }
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(getString(R.string.vm_load_key),dataloaded);
      //  outState.putInt("currentListPosition",mAdapter.getCurrentPosition());
        //outState.putInt(LAST_CATEGORY,currentCategory);
        if(dataloaded){
            mListState = manager.onSaveInstanceState();
            outState.putParcelableArrayList(getString(R.string.vm_list_key),
                    (ArrayList<? extends Parcelable>) mAdapter.getMovieList());
            outState.putParcelable(getString(R.string.vm_list_state_key), mListState);
        }
    }
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState != null) {
            dataloaded = savedInstanceState.getBoolean(getString(R.string.vm_load_key));
          //  currentCategory=savedInstanceState.getInt(LAST_CATEGORY);
           // mRecyclerView.scrollToPosition(savedInstanceState.getInt("currentListPosition"));
            if(dataloaded){
                mListState = savedInstanceState.getParcelable(getString(R.string.vm_list_state_key));
                movieList = savedInstanceState
                        .getParcelableArrayList(getString(R.string.vm_list_key));
                setLayoutManager();
                mAdapter.UpdateListOfMovies(movieList,false);
            }else {
                showErrorMsg(getString(R.string.error_activity_state));
            }
        }
    }
    @Override
    protected void onResume() {
        super.onResume();
        if (mListState != null) {
            manager.onRestoreInstanceState(mListState);
        }
        if(currentCategory == FAVORITE_MOVIES_SELECTION){
            controller.retrieveFavoriteMovies(this);
        }
    }
    @Override
    public void onMovieClick(Movie m, View moviePoster) {
        controller.movieClicked(m,moviePoster);
    }

}
