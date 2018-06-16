package com.tpourjalali.popularmovies;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.support.v4.content.AsyncTaskLoader;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.MalformedURLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
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
    private static List<MovieReview> parseJSONMovieReviewList(String s, long movie_id) {
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
    private static List<Movie> parseJSONMovieList(String s) {
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
    private static List<MovieVideo> parseJSONMovieVideoList(String s, long movie_id) {
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
    private static Movie parseJSONMovie(String s){
        return new Movie.Builder().setFromTMDBMovieDetailJson(s).build();
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
    public static Movie downloadMovie(long movie_id){
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
    public static List<MovieReview> downloadMovieReviewList(long movie_id){
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
    public static List<Movie> downloadPopularMovieList(){
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
    public static List<Movie> downloadTopRatedMovieList() {
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
    public static List<MovieVideo> downloadMovieVideoList(long movie_id){
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
