package com.tpourjalali.popularmovies;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MovieListActivity extends AppCompatActivity  implements LoaderManager.LoaderCallbacks{
    public static final int LOAD_POPULAR_MOVIE_LIST = 10;
    private String TMDB_API_KEY_V3;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TMDB_API_KEY_V3 = getString(R.string.tmdb_api_key_v3);
        setContentView(R.layout.activity_movie_list);
    }

    @NonNull
    @Override
    public Loader onCreateLoader(int id, @Nullable Bundle args) {
        switch (id){
            case LOAD_POPULAR_MOVIE_LIST:
                return new MovieLoader(this, TMDB_API_KEY_V3);
            default:
                throw new IllegalArgumentException("MovieLoader createLoader called with invalid id: "+id);
        }
    }
    @Override
    public void onLoadFinished(@NonNull Loader loader, Object data) {

    }

    @Override
    public void onLoaderReset(@NonNull Loader loader) {

    }
}
