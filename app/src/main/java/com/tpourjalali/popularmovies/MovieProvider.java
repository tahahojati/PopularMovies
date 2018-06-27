package com.tpourjalali.popularmovies;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class MovieProvider extends ContentProvider {
    private static final String TAG = "MoviesProvider";
    private static final String AUTHORITY = MovieProviderContract.AUTHORITY;
    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private static final int CODE_POPULAR_MOVIES = 10;
    private static final int CODE_TOPRATED_MOVIES = 20;
    private static final int CODE_SINGLE_MOVIE = 30;
    private static final int CODE_FAVORITE_MOVIE = 35;
    private static final int CODE_REVIEW_FOR_MOVIE = 40;
    private static final int CODE_TRAILERS_FOR_MOVIE = 50;
    private static final int CODE_IMAGE = 60;


    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        // Implement this to handle requests to delete one or more rows.
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public String getType(Uri uri) {
        switch (sUriMatcher.match(uri)) {
            case CODE_POPULAR_MOVIES:
            case CODE_TOPRATED_MOVIES:
            case CODE_FAVORITE_MOVIE:
            case CODE_REVIEW_FOR_MOVIE:
            case CODE_TRAILERS_FOR_MOVIE:
                return MovieProviderContract.MovieEntry.CONTENT_DIR_TYPE;
            case CODE_SINGLE_MOVIE:
            case CODE_IMAGE:
                return MovieProviderContract.MovieEntry.CONTENT_ITEM_TYPE;
            default:
                throw new UnsupportedOperationException("URI did not match any of the available options");
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        switch (sUriMatcher.match(uri)){
            case CODE_POPULAR_MOVIES:
            case CODE_TOPRATED_MOVIES:
            case CODE_FAVORITE_MOVIE:
            case CODE_SINGLE_MOVIE:
            case CODE_REVIEW_FOR_MOVIE:
            case CODE_TRAILERS_FOR_MOVIE:
            case CODE_IMAGE:
            default:
                throw new UnsupportedOperationException("URI did not match any of the available options");
        }
    }

    @Override
    public boolean onCreate() {
        // TODO: Implement this to initialize your content provider on startup.
        return false;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        /* TODO:
        * 1- Query should hit local db first.
        * 2- Should be able to query favorite movies.
        * 3- Query should check local db even for popular movies, just so we can mark the favorite movies.
         */
        MovieDatabaseOpenHelper helper;
        MatrixCursor res;
        List<ContentValues> movieList;
        List<ContentValues> reviewList;
        List<ContentValues> videoList;
        long movie_id;
        Log.d(TAG, "uri: "+uri.toString()+" match: "+sUriMatcher.match(uri));
        switch (sUriMatcher.match(uri)){
            case CODE_POPULAR_MOVIES:
                movieList = TMDB.downloadPopularMovieList();
                res = cursorFromMovieList(movieList);
                applyFavoriteMoviesInMovieCursor(res);
                return cursorFromMovieList(movieList);
            case CODE_TOPRATED_MOVIES:
                movieList = TMDB.downloadTopRatedMovieList();
                return cursorFromMovieList(movieList);
            case CODE_FAVORITE_MOVIE:
                helper = new MovieDatabaseOpenHelper(getContext());
                try(SQLiteDatabase db  = helper.getReadableDatabase()){
                    Cursor cursor = db.query(MovieProviderContract.MovieEntry.TABLE_NAME,
                            projection,
                            selection,
                            selectionArgs,
                            null,
                            null,
                            sortOrder);
                    return cursor;
                }
            case CODE_SINGLE_MOVIE:
                //TODO: update this
                movie_id = Long.parseLong(uri.getLastPathSegment());
                movieList = new ArrayList<>();
                movieList.add(TMDB.downloadMovie(movie_id));
                return cursorFromMovieList(movieList);
            case CODE_REVIEW_FOR_MOVIE:
                movie_id = Long.parseLong(uri.getLastPathSegment());
                reviewList = TMDB.downloadMovieReviewList(movie_id);
                return cursorFromReviewList(reviewList);
            case CODE_TRAILERS_FOR_MOVIE:
                movie_id = Long.parseLong(uri.getLastPathSegment());
                videoList = TMDB.downloadMovieVideoList(movie_id);
                return cursorFromVideoList(videoList);
            case CODE_IMAGE:
                break;
            default:
                throw new UnsupportedOperationException("URI did not match any of the available options");
        }
        return null;
    }
    /*
    * Does a query of favorite movies table looking for ids in the provided cursor. It then sets the favorite column to true for every item found.
     */
    private void applyFavoriteMoviesInMovieCursor(@NonNull MatrixCursor res) {
        List<String> ids = new LinkedList<>();
        res.moveToFirst();
        while(!res.isAfterLast()){
            ids.add(Long.toString(res.getLong(res.getColumnIndex(MovieProviderContract.MovieEntry._ID))));
            res.moveToNext();
        }
        String ids_str = TextUtils.join(", ", ids);
        String where = MovieProviderContract.MovieEntry._ID + " IN (?) ";
        Cursor favorites = query(MovieProviderContract.MovieEntry.FAVORITE_MOVIES_URI, new String[]{MovieProviderContract.MovieEntry._ID}, where, new String[]{ids_str},null);
        int favoritesIdColumnIndex = favorites.getColumnIndex(MovieProviderContract.MovieEntry._ID);
        int resIdColumnIndex = res.getColumnIndex(MovieProviderContract.MovieEntry._ID);
        //now for each item in res, check the favorites O(n^2)..
        res.moveToFirst();
        while(!res.isAfterLast()){
            favorites.moveToFirst();
            while (!favorites.isAfterLast()){
                if(res.getLong(resIdColumnIndex) == favorites.getLong(favoritesIdColumnIndex)){
                    res.addRow(res.RowBuilder);
                    break;
                }
                favorites.moveToNext();
            }
            res.moveToNext();
        }
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        /* TODO:
        * Update should insert/remove reviews, videos, and images from local storage.
         */
        switch (sUriMatcher.match(uri)){
            case CODE_SINGLE_MOVIE:
                MovieDatabaseOpenHelper helper = new MovieDatabaseOpenHelper(getContext());
                final SQLiteDatabase db = helper.getWritableDatabase();
                db.beginTransaction();
                if(!values.containsKey(MovieProviderContract.MovieEntry.COLUMN_FAVORITE) || !values.getAsBoolean(MovieProviderContract.MovieEntry.COLUMN_FAVORITE)){
                    // need to remove from db
                    db.delete(MovieProviderContract.MovieEntry.TABLE_NAME, MovieProviderContract.MovieEntry._ID + "=?",new String[]{
                            uri.getLastPathSegment()
                    });

                    //TODO: delete the images as well
                } else {
                    //download the movie and all of its artifacts and store them to db.
                    long movie_id = Long.parseLong(uri.getLastPathSegment());
                    ContentValues movie = TMDB.downloadMovie(movie_id);
                    List<ContentValues> videos = TMDB.downloadMovieVideoList(movie_id);
                    List<ContentValues> reviews = TMDB.downloadMovieReviewList(movie_id);

                    db.insert(MovieProviderContract.MovieEntry.TABLE_NAME, null, values);
                    videos.stream().forEach(video -> db.insert(MovieProviderContract.VideoEntry.TABLE_NAME, null, video));
                    reviews.stream().forEach(review -> db.insert(MovieProviderContract.ReviewEntry.TABLE_NAME, null, review));
                }
                db.setTransactionSuccessful();
                db.endTransaction();
                db.close();
                return 1;
            case CODE_POPULAR_MOVIES:
            case CODE_TOPRATED_MOVIES:
            case CODE_FAVORITE_MOVIE:
            case CODE_REVIEW_FOR_MOVIE:
            case CODE_TRAILERS_FOR_MOVIE:
            case CODE_IMAGE:
            default:
                throw new UnsupportedOperationException("URI did not match any of the available options");
        }
    }

    private MatrixCursor cursorFromMovieList(List<ContentValues> movieList) {
        String[] colNames = new String[]{
                MovieProviderContract.MovieEntry.COLUMN_TMDB_ID             ,
                MovieProviderContract.MovieEntry.COLUMN_POSTER_PATH			,
                MovieProviderContract.MovieEntry.COLUMN_ADULT				,
                MovieProviderContract.MovieEntry.COLUMN_OVERVIEW				,
                MovieProviderContract.MovieEntry.COLUMN_RELEASE_DATE			,
                MovieProviderContract.MovieEntry._ID					    ,
                MovieProviderContract.MovieEntry.COLUMN_ORIGINAL_TITLE		,
                MovieProviderContract.MovieEntry.COLUMN_ORIGINAL_LANGUAGE	,
                MovieProviderContract.MovieEntry.COLUMN_TITLE				,
                MovieProviderContract.MovieEntry.COLUMN_BACKDROP_PATH		,
                MovieProviderContract.MovieEntry.COLUMN_POPULARITY			,
                MovieProviderContract.MovieEntry.COLUMN_VOTE_COUNT			,
                MovieProviderContract.MovieEntry.COLUMN_VIDEO				,
                MovieProviderContract.MovieEntry.COLUMN_VOTE_AVERAGE			,
                MovieProviderContract.MovieEntry.COLUMN_GENRES				,
                MovieProviderContract.MovieEntry.COLUMN_RUNTIME				,
//                MovieProviderContract.MovieEntry.COLUMN_USER_RATING         ,
//                MovieProviderContract.MovieEntry.COLUMN_FAVORITE
        };
        final MatrixCursor mc =  new MatrixCursor(colNames,movieList.size());
        for(ContentValues movie: movieList){
            mc.addRow(new Object[]{
                    movie.getAsLong(MovieProviderContract.MovieEntry.COLUMN_TMDB_ID)             ,
                    movie.getAsString(MovieProviderContract.MovieEntry.COLUMN_POSTER_PATH)			,
                    movie.getAsBoolean(MovieProviderContract.MovieEntry.COLUMN_ADULT)		,
                    movie.getAsString( MovieProviderContract.MovieEntry.COLUMN_OVERVIEW)				,
                    movie.getAsString(MovieProviderContract.MovieEntry.COLUMN_RELEASE_DATE)			,
                    movie.getAsLong(MovieProviderContract.MovieEntry._ID)					    ,
                    movie.getAsString(MovieProviderContract.MovieEntry.COLUMN_ORIGINAL_TITLE)		,
                    movie.getAsString(MovieProviderContract.MovieEntry.COLUMN_ORIGINAL_LANGUAGE)	,
                    movie.getAsString(MovieProviderContract.MovieEntry.COLUMN_TITLE)				,
                    movie.getAsString(MovieProviderContract.MovieEntry.COLUMN_BACKDROP_PATH)		,
                    movie.getAsString(MovieProviderContract.MovieEntry.COLUMN_POPULARITY)			,
                    movie.getAsInteger(MovieProviderContract.MovieEntry.COLUMN_VOTE_COUNT)			,
                    movie.getAsString(MovieProviderContract.MovieEntry.COLUMN_VIDEO)				,
                    movie.getAsDouble(MovieProviderContract.MovieEntry.COLUMN_VOTE_AVERAGE	)		,
                    movie.getAsString(MovieProviderContract.MovieEntry.COLUMN_GENRES	)			,
                    movie.getAsInteger(MovieProviderContract.MovieEntry.COLUMN_RUNTIME	)			,
            });
        }
        return mc;
    }
    private MatrixCursor cursorFromReviewList(List<ContentValues> reviewList) {
        String[] colNames = new String[]{
                MovieProviderContract.ReviewEntry.COLUMN_MOVIE_ID             ,
                MovieProviderContract.ReviewEntry.COLUMN_URL			,
                MovieProviderContract.ReviewEntry.COLUMN_CONTENT				,
                MovieProviderContract.ReviewEntry.COLUMN_AUTHOR				,
                MovieProviderContract.ReviewEntry._ID					    ,
        };
        final MatrixCursor mc =  new MatrixCursor(colNames,reviewList.size());
        for(ContentValues review: reviewList){
            mc.addRow(new Object[]{
                    review.getAsLong(MovieProviderContract.ReviewEntry.COLUMN_MOVIE_ID)             ,
                    review.getAsString(MovieProviderContract.ReviewEntry.COLUMN_URL)			,
                    review.getAsString(MovieProviderContract.ReviewEntry.COLUMN_CONTENT)				,
                    review.getAsString(MovieProviderContract.ReviewEntry.COLUMN_AUTHOR)				,
                    review.getAsString(MovieProviderContract.ReviewEntry._ID)
            });
        }
        return mc;
    }
    private MatrixCursor cursorFromVideoList(List<ContentValues> reviewList) {
        String[] colNames = new String[]{
                MovieProviderContract.VideoEntry.COLUMN_MOVIE_ID             ,
                MovieProviderContract.VideoEntry.COLUMN_SIZE			,
                MovieProviderContract.VideoEntry.COLUMN_NAME				,
                MovieProviderContract.VideoEntry.COLUMN_KEY				,
                MovieProviderContract.VideoEntry._ID					    ,
                MovieProviderContract.VideoEntry.COLUMN_SITE					    ,
        };
        final MatrixCursor mc =  new MatrixCursor(colNames,reviewList.size());
        for(ContentValues video: reviewList){
            mc.addRow(new Object[]{
                    video.getAsLong(MovieProviderContract.VideoEntry.COLUMN_MOVIE_ID)             ,
                    video.getAsInteger(MovieProviderContract.VideoEntry.COLUMN_SIZE)			,
                    video.getAsString(MovieProviderContract.VideoEntry.COLUMN_NAME)				,
                    video.getAsString(MovieProviderContract.VideoEntry.COLUMN_KEY)				,
                    video.getAsString(MovieProviderContract.VideoEntry._ID)					    ,
                    video.getAsString(MovieProviderContract.VideoEntry.COLUMN_SITE)
            });
        }
        return mc;
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
                MovieProviderContract.MovieEntry.FAVORITE_MOVIES_URI.getPath(),
                CODE_FAVORITE_MOVIE
        );
        res.addURI(
                MovieProviderContract.AUTHORITY,
                MovieProviderContract.MovieEntry.SINGLE_MOVIE_URI.getPath()+"/#",
                CODE_SINGLE_MOVIE
        );
        res.addURI(
                MovieProviderContract.AUTHORITY,
                MovieProviderContract.ReviewEntry.CONTENT_URI.getPath()+"/#",
                CODE_REVIEW_FOR_MOVIE
        );
        res.addURI(
                MovieProviderContract.AUTHORITY,
                MovieProviderContract.VideoEntry.CONTENT_URI.getPath()+"/#",
                CODE_TRAILERS_FOR_MOVIE
        );
        return res;
    }
}
