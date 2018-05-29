package com.tpourjalali.popularmovies;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.AsyncTaskLoader;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

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


    public static final int MOVIE_LIST_LOADER_SORT_POPULAR = 42;
    public static final int MOVIE_LIST_LOADER_SORT_RATING = 234;

    //END OF PUBLIC CONSTANTS



    public static AsyncTaskLoader<List<Movie>> createMovieListLoader(final int sorting_key, final Context context){
        return new AsyncTaskLoader<List<Movie>>(context) {
            private Object parseJSON(String s) {
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

            private String getUri(){
                String path = null;
                switch (sorting_key) {
                    case MOVIE_LIST_LOADER_SORT_POPULAR:
                        path = TMDB.TMDB_PATH_POPULAR_MOVIE;
                        break;
                    case MOVIE_LIST_LOADER_SORT_RATING:
                        path = TMDB.TMDB_PATH_TOP_RATED_MOVIE;
                        break;
                    default:
                        throw new IllegalArgumentException("Wront sorting key: "+Integer.toString(sorting_key));
                }
                String today = new SimpleDateFormat("yyyy-MM-dd")
                        .format(Calendar.getInstance().getTime());
                return Uri.parse(TMDB.TMDB_URL)
                        .buildUpon()
                        .appendEncodedPath(path)
                        .appendQueryParameter(TMDB.TMDB_KEY_API, getContext().getString(R.string.tmdb_api_key_v3))
                        .appendQueryParameter(TMDB.TMDB_KEY_RELEASE_LTE, today)
                        .build()
                        .toString();
            }
            @Override
            public List<Movie> loadInBackground() {
                try {
                    //Log.d(TAG, "heres is the uri: "+getUri(ID_DISCOVERY));
                    String jsonStr = NetUtils.openPage(getUri());
                    return (List<Movie>)parseJSON(jsonStr);
                } catch (MalformedURLException e) {
                    Log.e(TAG, "TMDB url is malinformed: "+ getUri(), e);
                } catch (IOException e) {
                    Log.e(TAG, "http error",e);
                }
                return null ;            }
        };
    }
    public static AsyncTaskLoader<Movie> createMovieDetailLoader(final long movie_id, Context context){
        return new AsyncTaskLoader<Movie>(context) {
            @Override
            public Movie loadInBackground() {
                try {
                    String url =
                            Uri.parse(TMDB.TMDB_URL)
                                    .buildUpon()
                                    .appendEncodedPath(TMDB.TMDB_PATH_GET_MOVIE.replace("?", Long.toString(movie_id)))
                                    .appendQueryParameter(TMDB.TMDB_KEY_API, getContext().getString(R.string.tmdb_api_key_v3)).toString();
                    return new Movie.Builder().setFromTMDBMovieDetailJson(NetUtils.openPage(url)).build();
                } catch (MalformedURLException e) {
                    Log.e("Movie Detail Loader", "Could not parse Url", e);
                } catch (IOException e) {
                    Log.e("Movie Detail Loader", "Could not open connection", e);
                }
                return null;
            }
        };
    }
    public static AsyncTaskLoader<List<MovieReview>> createMovieReviewListLoader(final long movie_id, final Context context){
        return new AsyncTaskLoader<List<MovieReview>>(context) {
            private Object parseJSON(String s) {
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
            @Override
            public List<MovieReview> loadInBackground() {
                String url = null;
                try {
                    //Log.d(TAG, "heres is the uri: "+getUri(ID_DISCOVERY));
                    url = Uri.parse(TMDB.TMDB_URL)
                            .buildUpon()
                            .appendEncodedPath(
                                    TMDB.TMDB_PATH_GET_REVIEWS
                                            .replace("?", Long.toString(movie_id)))
                            .appendQueryParameter(
                                    TMDB.TMDB_KEY_API,
                                    getContext().getString(R.string.tmdb_api_key_v3)
                            )
                            .appendQueryParameter(TMDB.TMDB_KEY_LANGUAGE, Locale.getDefault().getDisplayLanguage())
                            .toString();
                    String jsonStr = NetUtils.openPage(url);
                    return (List<MovieReview>)parseJSON(jsonStr);
                } catch (MalformedURLException e) {
                    Log.e(TAG, "TMDB url is malinformed: "+ url, e);
                } catch (IOException e) {
                    Log.e(TAG, "http error",e);
                }
                return null ;            }
        };
    }
    public static AsyncTaskLoader<List<MovieVideo>> createMovieVideoListLoader(final long movie_id, final Context context) {
        return new AsyncTaskLoader<List<MovieVideo>>(context) {
            private Object parseJSON(String s) {
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


            @Override
            public List<MovieVideo> loadInBackground() {
                String url = null;
                try {
                    //Log.d(TAG, "heres is the uri: "+getUri(ID_DISCOVERY));
                    url = Uri.parse(TMDB.TMDB_URL)
                            .buildUpon()
                            .appendEncodedPath(
                                    TMDB.TMDB_PATH_GET_VIDEOS
                                            .replace("?", Long.toString(movie_id)))
                            .appendQueryParameter(
                                    TMDB.TMDB_KEY_API,
                                    getContext().getString(R.string.tmdb_api_key_v3)
                            )
                            .appendQueryParameter(TMDB.TMDB_KEY_LANGUAGE, Locale.getDefault().getDisplayLanguage())
                            .toString();
                    String jsonStr = NetUtils.openPage(url);
                    return (List<MovieVideo>) parseJSON(jsonStr);
                } catch (MalformedURLException e) {
                    Log.e(TAG, "TMDB url is malinformed: " + url, e);
                } catch (IOException e) {
                    Log.e(TAG, "http error", e);
                }
                return null;
            }
        };
    }

}
