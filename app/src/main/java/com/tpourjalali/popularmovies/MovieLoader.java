package com.tpourjalali.popularmovies;


import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.AsyncTaskLoader;
import android.text.method.MovementMethod;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class MovieLoader extends AsyncTaskLoader<List<Movie>> {
    private static final String TAG="MovieLoader";
    private final String mSortby;
    public static final String TMDB_PATH_DISCOVER_MOVIE = "3/discover/movie";
    private static final String ID_DISCOVERY = "discovery_uri";
    public static final String TMDB_KEY_SORTBY = "sort_by";
    private final String mTmdbApiKey;
    private static final String TMDB_KEY_PAGE = "page";
    private static final String TMDB_KEY_RELEASE_LTE = "release_date.lte";
    private static final String TMDB_URL = "https://api.themoviedb.org/";
    private static final String TMDB_KEY_API = "api_key";
    public MovieLoader(Context activityContext, String tmdbApiKey, String sortby){
        super(activityContext);
        mSortby = sortby;
        mTmdbApiKey = tmdbApiKey;
    }
    @Nullable
    @Override
    public List<Movie> loadInBackground() {
        try {
            URL url = new URL(getUri(ID_DISCOVERY));
            //Log.d(TAG, "heres is the uri: "+getUri(ID_DISCOVERY));
            InputStream httpStream = url.openStream();
            int charachter = httpStream.read();
            ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
            while(charachter != -1){
                //Log.d(TAG, "reading stream ");
                byteStream.write(charachter);
                charachter = httpStream.read();
            }
            //Log.d(TAG, "tmdb response: "+byteStream.toString());
            return (List<Movie>)parseJSON(ID_DISCOVERY, byteStream.toString());
        } catch (MalformedURLException e) {
            Log.e(TAG, "TMDB url is malinformed: "+ getUri(ID_DISCOVERY), e);
        } catch (IOException e) {
            Log.e(TAG, "http error",e);
        }
        return null ;
    }

    private Object parseJSON(String type_id, String s) {
        Log.d(TAG, s);
        switch (type_id){
            case ID_DISCOVERY:
                List<Movie> movieList = new ArrayList<>();
                try{
                    JSONArray jarr = new JSONObject(s).getJSONArray("results");
                    for(int i = 0; i < jarr.length(); ++i){
                        JSONObject jo = jarr.getJSONObject(i);
                        Movie.Builder mb = new Movie.Builder()
                                .title(jo.optString(Movie.JSON_KEY_TITLE))
                                .overview(jo.optString(Movie.JSON_KEY_OVERVIEW))
                                .id(jo.optInt(Movie.JSON_KEY_ID))
                                .originalLanguage(jo.optString(Movie.JSON_KEY_ORIGINALLANGUAGE))
                                .posterPath(jo.optString(Movie.JSON_KEY_POSTER_PATH))
                                .voteAverage(jo.optDouble(Movie.JSON_KEY_VOTE_AVERAGE));
                        try{
                            mb.releaseDate("yyyy-mm-dd", jo.optString(Movie.JSON_KEY_RELEASEDATE));
                        } catch (ParseException e) {
                            Log.e(TAG, "could not parse release date string(require yyyy-MM-dd: "+ jo.optString(Movie.JSON_KEY_RELEASEDATE));
                        }
                        movieList.add(mb.build());
                    }
                } catch (JSONException e) {
                    Log.e(TAG,"error parsing movie array json",e);
                }
                return movieList;
            default:
                return null;
        }
    }

    private String getUri(@NonNull String id){
        switch (id){
            case ID_DISCOVERY:
                String today = new SimpleDateFormat("yyyy-MM-dd")
                        .format(Calendar.getInstance().getTime());
                return Uri.parse(TMDB_URL)
                        .buildUpon()
                        .appendEncodedPath(TMDB_PATH_DISCOVER_MOVIE)
                        .appendQueryParameter(TMDB_KEY_API, mTmdbApiKey)
                        .appendQueryParameter(TMDB_KEY_PAGE, "1")
                        .appendQueryParameter(TMDB_KEY_SORTBY, mSortby)
                        .appendQueryParameter(TMDB_KEY_RELEASE_LTE, today)
                        .build()
                        .toString();
            default:
                return null;
        }
    }
}
