package com.tpourjalali.popularmovies;

import android.app.Application;

public class PopularMoviesApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        String tmdb_key = getString(R.string.tmdb_api_key_v3);
        TMDB.initialize(tmdb_key);
    }
}
