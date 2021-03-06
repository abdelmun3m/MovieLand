package com.abdelmun3m.movie_land;


import android.util.Log;

public class GeneralData {

    public static final String API_KEY = "a26b061467611fb1fc2dabf560a402c6";
    public static String YOUTUP_KEY = "AIzaSyBiQbI9ZId2_iyUjmhaHv-GQBQbTeWkw1Y";
    public static String ADS_ID = "";
    public static String UNIT_ID="ca-app-pub-3444558145434192/8849895143";
    public static final String MOVIE_API_URL  = "https://api.themoviedb.org/3/";
    public static final String GENERAL_MOVIES_URL  = MOVIE_API_URL+"movie/";
    public static final String POPULAR_MOVIES_URL  = GENERAL_MOVIES_URL+"popular";
    public static final String TOP_RATED_MOVIES_URL   = GENERAL_MOVIES_URL+"top_rated";

    public static final String QUERY_API_KEY = "api_key";
    public static final String QUERY_LANGUAGE = "language";
    public static final String DEFAULT_LANG   = "en-US";
    public static final String QUERY_PAGE  = "page";
    public static final String Json_TOTAL_PAGES  = "total_pages";
    public static int TOTAL_PAGES  = 1;
    public static int DEFAULT_PAGE_NUMBER = 1;
    public static  int CURRENT_PAGE =1;
    public static String w185 = "w185/";
    public static String w500 = "w500/";
    public static String w780 = "w780/";
    public static String w342 = "w342/";
    public static final String IMAGE_URL = "http://image.tmdb.org/t/p/"+w342;
    public static final String IMAGE_BACKDROP_URL = "http://image.tmdb.org/t/p/"+w780;
    public static final String  TAG= "PouplarMovies";
    public static final String Json_Result  = "results";
    public static final String Json_MOVIE_id  = "id";
    public static final String Json_MOVIE_OVERVIEW  = "overview";
    public static final String Json_MOVIE_ORIGINAL_TITLE  = "original_title";
    public static final String Json_MOVIE_RELEASE_DATE  = "release_date";
    public static final String Json_MOVIE_POSTER_IMAGE  = "poster_path";
    public static final String Json_MOVIE_VOTE_AVERAGE  = "vote_average";
    public static final String Json_MOVIE_TRAILER_KEY  = "key";
    public static final String Json_MOVIE_TRAILER_SITE  = "site";
    public static final String Json_MOVIE_TRAILER_SITE_VALUE  = "YouTube";
    public static final String Json_APPEND_REQUEST_KEY_OPTION = "append_to_response";

    public static final String Json_Posters = "posters";
    public static final String Json_backdrops = "backdrops";
    public static final String Json_Image_Ratio = "aspect_ratio";
    public static final String Json_Image_Path = "file_path";

    public static final String INTENT_TAG = "Selected_Movie";
    public static final String YOUTUBE_MOVIE = "https://www.youtube.com/watch?v=";


    public static void mylog(String s){
        Log.w(TAG,s);
    }

}
