package com.tpourjalali.popularmovies;

import android.app.Application;

import com.facebook.stetho.Stetho;

public class PopularMoviesApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Stetho.initializeWithDefaults(this);
        String tmdb_key = getString(R.string.tmdb_api_key_v3);
        TMDB.initialize(tmdb_key);
    }
}
