package com.tpourjalali.popularmovies;


import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;

public class MovieLoader implements LoaderManager.LoaderCallbacks<String> {
    private Context mApplicationContext;
    private final String TMDB_API_KEY_V3;
    public MovieLoader(Context applicationContext) {
        mApplicationContext = applicationContext;
        Resources resources = mApplicationContext.getResources();
        TMDB_API_KEY_V3 = resources.getString(R.string.tmdb_api_key_v3);
    }

    @NonNull
    @Override
    public Loader<String> onCreateLoader(int id, @Nullable Bundle args) {
        return new AsyncTaskLoader<String>(mApplicationContext) {
            @Nullable
            @Override
            public String loadInBackground() {
                return null;
            }
        };
    }

    @Override
    public void onLoadFinished(@NonNull Loader<String> loader, String data) {

    }

    @Override
    public void onLoaderReset(@NonNull Loader<String> loader) {

    }
}
