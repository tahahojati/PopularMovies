package com.tpourjalali.popularmovies;

import android.content.ContentResolver;
import android.database.AbstractCursor;
import android.database.Cursor;
import android.database.CursorWrapper;
import android.net.Uri;
import android.provider.BaseColumns;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 *  So we need three different "tables":
 *  1- Movies -> can give favorite, popular, top_rated
 *  2- Images -> either should provide a uri in a way that gives readonly access to the any image or should provide a bitmap
 *  3- Trailers -> For now we only want a url, later we might need actual videos.., or images
 *  4- Reviews
 */
public final class MovieProviderContract {
    private static final String TAG  = "MovieProviderContract";
    public static final String AUTHORITY = "com.tpourjalali.popularmovies";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://"+AUTHORITY);
    public final static class MovieEntry implements BaseColumns{
        public static final SimpleDateFormat MOVIE_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
        public static final String TABLE_NAME                   = "movie";
        public static final String POPULAR_MOVIES_PATH          = "popular";
        public static final String TOP_MOVIES_PATH              = "top_rated";
        public static final String FAVORITE_MOVIES_PATH         = "favorite";
        public static final String MOVIE_ID_PATH                = "id";
        //End of intenal constants
        public static final String COLUMN_TMDB_ID               = "tmdb_id";
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
        public static final Uri FAVORITE_MOVIES_URI = CONTENT_URI
                .buildUpon()
                .appendPath(FAVORITE_MOVIES_PATH)
                .build();
        static {
            Log.d("Movies Contract", CONTENT_DIR_TYPE+"\t"+CONTENT_ITEM_TYPE);
        }

        //End of Constants
        public static class MovieCursorWrapper extends CursorWrapper {
            public MovieCursorWrapper(Cursor cursor) {
                super(cursor);
            }
            public Movie getMovie(){
                if(isClosed())
                    return null;
                try {
                    if (isBeforeFirst() || isAfterLast())
                        moveToFirst();
                    String genres = getString(getColumnIndex(COLUMN_GENRES));
                    if(genres == null) genres = "";
                    return new Movie.Builder()
                            .id(getLong(getColumnIndex(_ID)))
                            .tmdbId(getLong(getColumnIndex(COLUMN_TMDB_ID)))
                            .overview(getString(getColumnIndex(COLUMN_OVERVIEW)))
                            .originalLanguage(getString(getColumnIndex(COLUMN_ORIGINAL_LANGUAGE)))
                            .releaseDate("yyyy-MM-dd", getString(getColumnIndex(COLUMN_RELEASE_DATE)))
                            .voteCount(getInt(getColumnIndex(COLUMN_VOTE_COUNT)))
                            .posterPath(getString(getColumnIndex(COLUMN_POSTER_PATH)))
                            .backdropPath(getString(getColumnIndex(COLUMN_BACKDROP_PATH)))
                            .title(getString(getColumnIndex(COLUMN_TITLE)))
                            .genres(Arrays.asList(TextUtils.split(genres, ",")))
                            .runTimeMinutes(getInt(getColumnIndex(COLUMN_RUNTIME)))
                            .voteAverage(getDouble(getColumnIndex(COLUMN_VOTE_AVERAGE)))
                            .build();
                } catch (ParseException e) {
                    Log.e("Movie Provider Contract", "release date formate is incorrect: "+ getString(getColumnIndex(COLUMN_RELEASE_DATE)), e);
                }
                return null;
            }
            public void addMoviesToList(@NonNull List<Movie> list){
                Objects.requireNonNull(list);
                if(getCount() > 0){
                    moveToFirst();
                    list.add(getMovie());
                    while(moveToNext()){
                        list.add(getMovie());
                    }
                }
            }
        }
    }
    public final static class ReviewEntry implements BaseColumns{
        public static final String TABLE_NAME = "review";
        public static final String PATH = "review";
        public static final String COLUMN_MOVIE_ID = "movie_id";
        public static final String _ID = "id";
        public static final String COLUMN_AUTHOR = "author";
        public static final String COLUMN_CONTENT = "content";
        public static final String COLUMN_URL = "url";

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH).build();
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + CONTENT_URI.getEncodedPath();
        public static final String CONTENT_DIR_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + CONTENT_URI.getEncodedPath();

        public static class ReviewCursorWrapper extends CursorWrapper {
            public ReviewCursorWrapper(Cursor cursor) {
                super(cursor);
            }
            public MovieReview getMovieReview(){
                if(isClosed())
                    return null;
                if (isBeforeFirst() || isAfterLast())
                    moveToFirst();
                MovieReview mr = new MovieReview();
                mr.setId(getString(getColumnIndex(_ID)));
                mr.setAuthor(getString(getColumnIndex(COLUMN_AUTHOR)));
                mr.setContent(getString(getColumnIndex(COLUMN_CONTENT)));
                mr.setMovieId(getLong(getColumnIndex(COLUMN_MOVIE_ID)));
                mr.setUrl(getString(getColumnIndex(COLUMN_URL)));
                return mr;
            }
            public void addMovieReviewsToList(@NonNull List<MovieReview> list){
                Objects.requireNonNull(list);
                if(getCount() > 0){
                    moveToFirst();
                    list.add(getMovieReview());
                    while(moveToNext()){
                        list.add(getMovieReview());
                    }
                }
            }
        }
    }
    public final static class VideoEntry implements BaseColumns{
        public static final String TABLE_NAME = "video";
        public static final String PATH = "video";

        public static final String COLUMN_MOVIE_ID = "movie_id";
        public static final String _ID = "id";
        public static final String COLUMN_KEY = "key";
        public static final String COLUMN_SITE = "site";
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_SIZE = "size";


        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH).build();
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + CONTENT_URI.getEncodedPath();
        public static final String CONTENT_DIR_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + CONTENT_URI.getEncodedPath();
        public static class VideoCursorWrapper extends CursorWrapper {
            public VideoCursorWrapper(Cursor cursor) {
                super(cursor);
            }
            public MovieVideo getMovieVideo(){
                if(isClosed())
                    return null;
                if (isBeforeFirst() || isAfterLast())
                    moveToFirst();
                MovieVideo mr = new MovieVideo();
                Log.d(TAG, "Columns: "+ TextUtils.join(", ",getColumnNames()) + " Name: "+COLUMN_NAME);
                mr.setId(getString(getColumnIndex(_ID)));
                mr.setName(getString(getColumnIndex(COLUMN_NAME)));
                mr.setKey(getString(getColumnIndex(COLUMN_KEY)));
                mr.setMovieId(getLong(getColumnIndex(COLUMN_MOVIE_ID)));
                mr.setSize(getInt(getColumnIndex(COLUMN_SIZE)));
                mr.setSite(getString(getColumnIndex(COLUMN_SITE)));
                return mr;
            }
            public void addMovieVideosToList(@NonNull List<MovieVideo> list){
                Objects.requireNonNull(list);
                if(getCount() > 0){
                    moveToFirst();
                    list.add(getMovieVideo());
                    while(moveToNext()){
                        list.add(getMovieVideo());
                    }
                }
            }
        }
    }
}
