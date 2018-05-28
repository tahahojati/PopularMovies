package com.tpourjalali.popularmovies;


import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.AsyncTaskLoader;
import android.text.TextUtils;
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
import java.util.Map;
import java.util.Objects;

public class MovieLoader extends AsyncTaskLoader<List<Movie>> {
    private static final String TAG="MovieLoader";
    private final String mSortPath;
    private static final String ID_DISCOVERY = "discovery_uri";
    private final String mTmdbApiKey;

    public MovieLoader(Context activityContext, String tmdbApiKey, String sortPath){
        super(activityContext);
        mSortPath = sortPath;
        mTmdbApiKey = tmdbApiKey;
    }
    @Nullable
    @Override
    public List<Movie> loadInBackground() {
        try {
            URL url = new URL(getUri(ID_DISCOVERY, mSortPath, null));
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
            Log.e(TAG, "TMDB url is malinformed: "+ getUri(ID_DISCOVERY, mSortPath, null), e);
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
                            .setFromTMDBMovieDetailJson(jo);
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

    private String getUri(@NonNull String id, @Nullable String path, @Nullable Map<String, String> encodedQueryParams){
        switch (id){
            case ID_DISCOVERY:
                Objects.requireNonNull(path);
                String today = new SimpleDateFormat("yyyy-MM-dd")
                        .format(Calendar.getInstance().getTime());
                return Uri.parse(Movie.TMDB_URL)
                        .buildUpon()
                        .appendEncodedPath(path)
                        .appendQueryParameter(Movie.TMDB_KEY_API, mTmdbApiKey)
                        .appendQueryParameter(Movie.TMDB_KEY_RELEASE_LTE, today)
                        .build()
                        .toString();
            default:
                return null;
        }
    }
}
