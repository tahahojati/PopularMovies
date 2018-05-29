package com.tpourjalali.popularmovies;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;
import android.util.Log;

/**
 *  So we need three different "tables":
 *  1- Movies -> can give favorite, popular, top_rated
 *  2- Images -> either should provide a uri in a way that gives readonly access to the any image or should provide a bitmap
 *  3- Trailers -> For now we only want a url, later we might need actual videos.., or images
 *  4- Reviews
 */
public final class MovieProviderContract {
    public static final String AUTHORITY = "com.tpourjalali.popularmovies";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://"+AUTHORITY);
    public final static class MovieEntry implements BaseColumns{
        public static final String TABLE_NAME                   = "movie";
        public static final String POPULAR_MOVIES_PATH          = "popular";
        public static final String TOP_MOVIES_PATH              = "top_rated";
        public static final String MOVIE_ID_PATH                = "id";
        //End of intenal constants
        public static final String COLUMN_POSTER_PATH			= "poster_path";
        public static final String COLUMN_ADULT					= "adult";
        public static final String COLUMN_OVERVIEW				= "overview";
        public static final String COLUMN_RELEASE_DATE			= "release_date";
        public static final String _ID					        = "_id";
        public static final String COLUMN_ORIGINAL_TITLE		= "original_title";
        public static final String COLUMN_ORIGINAL_LANGUAGE		= "original_language";
        public static final String COLUMN_TITLE					= "title";
        public static final String COLUMN_BACKDROP_PATH			= "backdrop_path";
        public static final String COLUMN_POPULARITY			= "popularity";
        public static final String COLUMN_VOTE_COUNT			= "vote_count";
        public static final String COLUMN_VIDEO					= "video";
        public static final String COLUMN_VOTE_AVERAGE			= "vote_average";
        /* TODO: consider the three colummns below */
        public static final String COLUMN_GENRES				= "genres";
        public static final String COLUMN_REVIEWS				= "reviews";
        public static final String COLUMN_VIDEOS				= "videos";
        public static final String COLUMN_RUNTIME				= "runtime";
        public static final String COLUMN_USER_RATING           = "user_rating";
        public static final String COLUMN_FAVORITE              = "favorite";
        //End of Column definitions
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(TABLE_NAME).build();
        public static final String CONTENT_DIR_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + CONTENT_URI.getEncodedPath();
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + CONTENT_URI.getEncodedPath();
        public static final Uri POPULAR_MOVIES_URI = CONTENT_URI
                .buildUpon()
                .appendPath(POPULAR_MOVIES_PATH)
                .build();
        public static final Uri TOPRATED_MOVIES_URI = CONTENT_URI
                .buildUpon()
                .appendPath(TOP_MOVIES_PATH)
                .build();
        public static final Uri SINGLE_MOVIE_URI = CONTENT_URI
                .buildUpon()
                .appendPath(MOVIE_ID_PATH)
                .build();
        static {
            Log.d("Movies Contract", CONTENT_DIR_TYPE+"\t"+CONTENT_ITEM_TYPE);
        }

        //End of Constants
    }
}
