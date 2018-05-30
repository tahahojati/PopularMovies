package com.tpourjalali.popularmovies;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;

public class MovieProvider extends ContentProvider {
    private static final String TAG = "MoviesProvider";
    private static final String AUTHORITY = MovieProviderContract.AUTHORITY;
    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private static final int CODE_POPULAR_MOVIES = 10;
    private static final int CODE_TOPRATED_MOVIES = 20;
    private static final int CODE_SINGLE_MOVIE = 30;
    private static final int CODE_Review_FOR_MOVIE = 40;
    private static final int CODE_TRAILERS_FOR_MOVIE = 50;
    private static final int CODE_IMAGE = 60;

    public MovieProvider() {
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        // Implement this to handle requests to delete one or more rows.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public String getType(Uri uri) {
        // TODO: Implement this to handle requests for the MIME type of the data
        // at the given URI.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        // TODO: Implement this to handle requests to insert a new row.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public boolean onCreate() {
        // TODO: Implement this to initialize your content provider on startup.
        return false;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        switch (sUriMatcher.match(uri)){
            case CODE_POPULAR_MOVIES:

                break;
            case CODE_TOPRATED_MOVIES:

            case CODE_SINGLE_MOVIE:

            case CODE_Review_FOR_MOVIE:

            case CODE_TRAILERS_FOR_MOVIE:

            case CODE_IMAGE:

            default:
                throw new UnsupportedOperationException("URI did not match any of the available options");
        }
        return null;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        // TODO: Implement this to handle requests to update one or more rows.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    private static UriMatcher buildUriMatcher(){
        UriMatcher res = new UriMatcher(UriMatcher.NO_MATCH);
        res.addURI(
                MovieProviderContract.AUTHORITY,
                MovieProviderContract.MovieEntry.POPULAR_MOVIES_URI.getPath(),
                CODE_POPULAR_MOVIES
        );
        res.addURI(
                MovieProviderContract.AUTHORITY,
                MovieProviderContract.MovieEntry.TOPRATED_MOVIES_URI.getPath(),
                CODE_TOPRATED_MOVIES
        );
        res.addURI(
                MovieProviderContract.AUTHORITY,
                MovieProviderContract.MovieEntry.SINGLE_MOVIE_URI.getPath()+"/#",
                CODE_SINGLE_MOVIE
        );
        return res;
    }
}
