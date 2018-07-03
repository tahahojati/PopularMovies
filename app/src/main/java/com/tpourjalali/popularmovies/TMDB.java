package com.tpourjalali.popularmovies;

import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.support.v4.content.AsyncTaskLoader;
import android.text.TextUtils;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.MalformedURLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

public class TMDB {
    private static final String TAG = "TMDB CLASS";
    public static final String JSON_KEY_GENRE = "genres";
    public static final String TMDB_PATH_POPULAR_MOVIE = "/3/movie/popular";
    public static final String TMDB_PATH_TOP_RATED_MOVIE = "/3/movie/top_rated";
    public static final String TMDB_PATH_GET_VIDEOS = "/3/movie/?/videos";
    public static final String TMDB_PATH_GET_MOVIE = "/3/movie/?";
    public static final String TMDB_PATH_GET_REVIEWS = "3/movie/?/reviews";
    public static final String TMDB_KEY_SORTBY = "sort_by";
    public static final String TMDB_KEY_PAGE = "page";
    public static final String TMDB_KEY_API = "api_key";
    public static final String TMDB_KEY_RELEASE_LTE = "release_date.lte";
    public static final String TMDB_URL = "https://api.themoviedb.org";
    public static final String API_POSTER_BASE_URL = "https://image.tmdb.org/t/p";
    public static final String API_POSTER_SIZE_ORIGINAL = "/original";
    public static final String API_POSTER_SIZE_W92 = "/w92";
    public static final String API_POSTER_SIZE_W154 = "/w154";
    public static final String API_POSTER_SIZE_W185 = "/w185";
    public static final String API_POSTER_SIZE_W342 = "/w342";
    public static final String API_POSTER_SIZE_W500 = "/w500";
    public static final String API_POSTER_SIZE_W780 = "/w780";
    public static final String JSON_KEY_POSTER_PATH = "poster_path";
    public static final String JSON_KEY_ADULT= "adult";
    public static final int MOVIE_IMAGE_TYPE_BACKDROP = 23;
    public static final int MOVIE_IMAGE_TYPE_POSTER = 20;
    public static final String JSON_KEY_OVERVIEW= "overview";
    public static final String JSON_KEY_RELEASEDATE= "release_date";
    public static final String JSON_KEY_ID= "id";
    public static final String JSON_KEY_ORIGINALTITLE= "original_title";
    public static final String JSON_KEY_ORIGINALLANGUAGE= "original_language";
    public static final String JSON_KEY_TITLE= "title";
    public static final String JSON_KEY_BACKDROP_PATH= "backdrop_path";
    public static final String JSON_KEY_POPULARITY= "popularity";
    public static final String JSON_KEY_VOTE_COUNT= "vote_count";
    public static final String JSON_KEY_VIDEO= "video";
    public static final String JSON_KEY_RUNTIME = "runtime";
    public static final String JSON_KEY_GENRE_NAME = "name";
    public static final String JSON_KEY_VOTE_AVERAGE= "vote_average";
    public static final String TMDB_KEY_LANGUAGE = "language";
    public static final SimpleDateFormat TMDB_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");


    //END OF PUBLIC CONSTANTS
    private static String sTMDBApiKey = null;

    public static boolean initialize(String v3_api_key){
        sTMDBApiKey = v3_api_key;
        if(v3_api_key == null){
            return false;
        }
        return true;
    }
    private static List<ContentValues> parseJSONMovieReviewList(String s, long movie_id) {
        //Log.d(TAG, s);
        List<ContentValues> reviewList = new ArrayList<>();
        try {
            JSONArray jarr = new JSONObject(s).getJSONArray("results");
            for (int i = 0; i < jarr.length(); ++i) {
                JSONObject jo = jarr.getJSONObject(i);
                ContentValues mr = new ContentValues();
                mr.put(MovieProviderContract.ReviewEntry._ID, jo.optString("id"));
                mr.put(MovieProviderContract.ReviewEntry.COLUMN_AUTHOR, jo.optString("author"));
                mr.put(MovieProviderContract.ReviewEntry.COLUMN_CONTENT, jo.optString("content"));
                mr.put(MovieProviderContract.ReviewEntry.COLUMN_URL, jo.optString("url"));
                mr.put(MovieProviderContract.ReviewEntry.COLUMN_MOVIE_ID, movie_id);
                reviewList.add(mr);
            }
        } catch (JSONException e) {
            Log.e(TAG, "error parsing movie array json", e);
        }
        return reviewList;
    }
    private static List<ContentValues> parseJSONMovieList(String s) {
        //Log.d(TAG, s);
        List<ContentValues> movieList = new ArrayList<>();
        try{
            JSONArray jarr = new JSONObject(s).getJSONArray("results");
            for(int j = 0; j < jarr.length(); ++j) {
                ContentValues mv = parseJSONMovie(jarr.getJSONObject(j));
                movieList.add(mv);
            }
        } catch (JSONException e) {
            Log.e(TAG,"error parsing movie array json",e);
        }
        return movieList;
    }
    private static List<ContentValues> parseJSONMovieVideoList(String s, long movie_id) {
        //Log.d(TAG, s);
        List<ContentValues> trailerList = new ArrayList<>();
        try {
            JSONArray jarr = new JSONObject(s).getJSONArray("results");
            for (int i = 0; i < jarr.length(); ++i) {
                JSONObject jo = jarr.getJSONObject(i);
                ContentValues mv = new ContentValues();
                mv.put(MovieProviderContract.VideoEntry._ID, jo.optString("id"));
                mv.put(MovieProviderContract.VideoEntry.COLUMN_KEY, jo.optString("key"));
                mv.put(MovieProviderContract.VideoEntry.COLUMN_SITE, jo.optString("site"));
                mv.put(MovieProviderContract.VideoEntry.COLUMN_NAME, jo.optString("name"));
                mv.put(MovieProviderContract.VideoEntry.COLUMN_SIZE, jo.optInt("size"));
                mv.put(MovieProviderContract.VideoEntry.COLUMN_MOVIE_ID, movie_id);
                trailerList.add(mv);
            }
        } catch (JSONException e) {
            Log.e(TAG, "error parsing movie array json", e);
        }
        return trailerList;
    }
    private static ContentValues parseJSONMovie(String s) {
        try {
            JSONObject jo = new JSONObject(s);
            return parseJSONMovie(jo);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }
    private static ContentValues parseJSONMovie(JSONObject jo){
        ContentValues mv = new ContentValues();
        JSONArray genreArray = jo.optJSONArray(TMDB.JSON_KEY_GENRE);
        if (genreArray != null) {
            List<String> genres = new LinkedList<>();
            for (int i = 0; i < genreArray.length(); ++i) {
                genres.add(genreArray.optJSONObject(i).optString(TMDB.JSON_KEY_GENRE_NAME));
            }
            mv.put(MovieProviderContract.MovieEntry.COLUMN_GENRES, TextUtils.join(", ", genres));
        }
        mv.put(MovieProviderContract.MovieEntry.COLUMN_TITLE, jo.optString(TMDB.JSON_KEY_TITLE));
        mv.put(MovieProviderContract.MovieEntry.COLUMN_OVERVIEW, jo.optString(TMDB.JSON_KEY_OVERVIEW));
        mv.put(MovieProviderContract.MovieEntry.COLUMN_RUNTIME, jo.optInt(TMDB.JSON_KEY_RUNTIME));
        mv.put(MovieProviderContract.MovieEntry.COLUMN_VOTE_COUNT, jo.optInt(TMDB.JSON_KEY_VOTE_COUNT));
        mv.put(MovieProviderContract.MovieEntry.COLUMN_TMDB_ID, jo.optInt(TMDB.JSON_KEY_ID));
        mv.put(MovieProviderContract.MovieEntry._ID, jo.optInt(TMDB.JSON_KEY_ID));
        mv.put(MovieProviderContract.MovieEntry.COLUMN_BACKDROP_PATH, jo.optString(TMDB.JSON_KEY_BACKDROP_PATH));
        mv.put(MovieProviderContract.MovieEntry.COLUMN_ORIGINAL_LANGUAGE, jo.optString(TMDB.JSON_KEY_ORIGINALLANGUAGE));
        mv.put(MovieProviderContract.MovieEntry.COLUMN_POSTER_PATH, jo.optString(TMDB.JSON_KEY_POSTER_PATH));
        mv.put(MovieProviderContract.MovieEntry.COLUMN_VOTE_AVERAGE, jo.optDouble(TMDB.JSON_KEY_VOTE_AVERAGE));
        mv.put(MovieProviderContract.MovieEntry.COLUMN_RELEASE_DATE, jo.optString(TMDB.JSON_KEY_RELEASEDATE));
        mv.put(MovieProviderContract.MovieEntry.COLUMN_FAVORITE, 0);
        return mv;
    }
    private static String generateUri(String path, @Nullable List<Object> args){
        Uri.Builder ub = Uri.parse(TMDB_URL).buildUpon();
        switch (path){
            case TMDB_PATH_POPULAR_MOVIE:
            case TMDB_PATH_TOP_RATED_MOVIE:
                String today = TMDB_DATE_FORMAT
                        .format(Calendar.getInstance().getTime());
                ub.appendEncodedPath(path)
                        .appendQueryParameter(TMDB.TMDB_KEY_RELEASE_LTE, today);
                break;
            case TMDB_PATH_GET_MOVIE:
            case TMDB_PATH_GET_REVIEWS:
            case TMDB_PATH_GET_VIDEOS:
                if(args == null || args.size() == 0)
                    throw new IllegalArgumentException("for movie, video, and review uris, you must pass a movie id in the second argument.");
                ub.appendEncodedPath(path);
                ub = Uri.parse(ub.toString().replace("?", args.get(0).toString())).buildUpon();
                break;
            default:
                throw new IllegalArgumentException("path must be one of the constants defined in the class");
        }
        ub.appendQueryParameter(TMDB.TMDB_KEY_API, sTMDBApiKey);
        return ub.toString();
    }
    public static ContentValues downloadMovie(long movie_id){
        String url = null ;
        List<Object> args = new ArrayList<>(1);
        args.add(new Long(movie_id));
        try {
            url = generateUri(TMDB_PATH_GET_MOVIE, args);
            String jsonStr = NetUtils.openPage(url);
            return parseJSONMovie(jsonStr);
        } catch (MalformedURLException e) {
            Log.e(TAG, "TMDB url is malinformed: "+ url, e);
        } catch (IOException e) {
            Log.e(TAG, "http error",e);
        }
        return null ;
    }
    public static List<ContentValues> downloadMovieReviewList(long movie_id){
        String url = null ;
        List<Object> args = new ArrayList<>(1);
        args.add(new Long(movie_id));
        try {
            url = generateUri(TMDB_PATH_GET_REVIEWS, args);
            String jsonStr = NetUtils.openPage(url);
            return parseJSONMovieReviewList(jsonStr, movie_id);
        } catch (MalformedURLException e) {
            Log.e(TAG, "TMDB url is malinformed: "+ url, e);
        } catch (IOException e) {
            Log.e(TAG, "http error",e);
        }
        return null ;
    }
    public static List<ContentValues> downloadPopularMovieList(){
        String url = null ;
        try {
            url = generateUri(TMDB_PATH_POPULAR_MOVIE, null);
            String jsonStr = NetUtils.openPage(url);
            return parseJSONMovieList(jsonStr);
        } catch (MalformedURLException e) {
            Log.e(TAG, "TMDB url is malinformed: "+ url, e);
        } catch (IOException e) {
            Log.e(TAG, "http error",e);
        }
        return null ;
    }
    public static List<ContentValues> downloadTopRatedMovieList() {
        String url = null;
        try {
            url = generateUri(TMDB_PATH_TOP_RATED_MOVIE, null);
            String jsonStr = NetUtils.openPage(url);
            return parseJSONMovieList(jsonStr);
        } catch (MalformedURLException e) {
            Log.e(TAG, "TMDB url is malinformed: " + url, e);
        } catch (IOException e) {
            Log.e(TAG, "http error", e);
        }
        return null;
    }
    public static List<ContentValues> downloadMovieVideoList(long movie_id){
        String url = null ;
        List<Object> args = new ArrayList<>(1);
        args.add(new Long(movie_id));
        try {
            url = generateUri(TMDB_PATH_GET_VIDEOS, args);
            String jsonStr = NetUtils.openPage(url);
            return parseJSONMovieVideoList(jsonStr, movie_id);
        } catch (MalformedURLException e) {
            Log.e(TAG, "TMDB url is malinformed: "+ url, e);
        } catch (IOException e) {
            Log.e(TAG, "http error",e);
        }
        return null ;
    }


}


//old parse function version:
/*
    private static List<ContentValues> parseJSONMovieReviewList(String s, long movie_id) {
        //Log.d(TAG, s);
        List<MovieReview> reviewList = new ArrayList<>();
        try {
            JSONArray jarr = new JSONObject(s).getJSONArray("results");
            for (int i = 0; i < jarr.length(); ++i) {
                JSONObject jo = jarr.getJSONObject(i);
                MovieReview mr = new MovieReview();
                mr.setId(jo.optString("id"));
                mr.setAuthor(jo.optString("author"));
                mr.setContent(jo.optString("content"));
                mr.setUrl(jo.optString("url"));
                mr.setMovieId(movie_id);
                reviewList.add(mr);
            }
        } catch (JSONException e) {
            Log.e(TAG, "error parsing movie array json", e);
        }
        return reviewList;
    }
    private static List<ContentValues> parseJSONMovieList(String s) {
        //Log.d(TAG, s);
        List<Movie> movieList = new ArrayList<>();
        try{
            JSONArray jarr = new JSONObject(s).getJSONArray("results");
            for(int i = 0; i < jarr.length(); ++i){
                JSONObject jo = jarr.getJSONObject(i);
                Movie.Builder mb = new Movie.Builder()
                        .setFromTMDBMovieDetailJson(jo);
                movieList.add(mb.build());
            }
        } catch (JSONException e) {
            Log.e(TAG,"error parsing movie array json",e);
        }
        return movieList;
    }
    private static List<ContentValues> parseJSONMovieVideoList(String s, long movie_id) {
        //Log.d(TAG, s);
        List<MovieVideo> trailerList = new ArrayList<>();
        try {
            JSONArray jarr = new JSONObject(s).getJSONArray("results");
            for (int i = 0; i < jarr.length(); ++i) {
                JSONObject jo = jarr.getJSONObject(i);
                MovieVideo mv = new MovieVideo();
                mv.setId(jo.optString("id"));
                mv.setKey(jo.optString("key"));
                mv.setSite(jo.optString("site"));
                mv.setName(jo.optString("name"));
                mv.setSize(jo.optInt("size"));
                mv.setMovieId(movie_id);
                trailerList.add(mv);
            }
        } catch (JSONException e) {
            Log.e(TAG, "error parsing movie array json", e);
        }
        return trailerList;
    }
 */