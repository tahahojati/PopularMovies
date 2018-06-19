package com.tpourjalali.popularmovies;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.content.AsyncTaskLoader;
import android.text.TextUtils;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

final public class MovieUtils {
    @NonNull
    public static ContentValues generateCVForMovieProvider(Movie movie){
        ContentValues cv = new ContentValues();
        cv.put(MovieProviderContract.MovieEntry.COLUMN_TMDB_ID            ,movie.getTmdbId());
        cv.put(MovieProviderContract.MovieEntry.COLUMN_POSTER_PATH		,movie.getPosterPath());
        cv.put(MovieProviderContract.MovieEntry.COLUMN_ADULT				,movie.getAdult());
        cv.put(MovieProviderContract.MovieEntry.COLUMN_OVERVIEW			,movie.getOverview());
        cv.put(MovieProviderContract.MovieEntry.COLUMN_RELEASE_DATE		,movie.getReleaseDate().toString());
        cv.put(MovieProviderContract.MovieEntry._ID					    ,movie.getId());
        cv.put(MovieProviderContract.MovieEntry.COLUMN_ORIGINAL_TITLE		,movie.getOriginalTitle());
        cv.put(MovieProviderContract.MovieEntry.COLUMN_ORIGINAL_LANGUAGE	,movie.getOriginalLanguage());
        cv.put(MovieProviderContract.MovieEntry.COLUMN_TITLE				,movie.getTitle());
        cv.put(MovieProviderContract.MovieEntry.COLUMN_BACKDROP_PATH		,movie.getBackdropPath());
        cv.put(MovieProviderContract.MovieEntry.COLUMN_POPULARITY			,movie.getPopularity());
        cv.put(MovieProviderContract.MovieEntry.COLUMN_VOTE_COUNT			,movie.getVoteCount());
        cv.put(MovieProviderContract.MovieEntry.COLUMN_VIDEO				,movie.getVideo());
        cv.put(MovieProviderContract.MovieEntry.COLUMN_VOTE_AVERAGE		,movie.getVoteAverage());
        cv.put(MovieProviderContract.MovieEntry.COLUMN_GENRES				, TextUtils.join(", ",movie.getGenres()));
        cv.put(MovieProviderContract.MovieEntry.COLUMN_RUNTIME			,movie.getRunTime());
//        cv.put(MovieProviderContract.MovieEntry.COLUMN_USER_RATING        ,movie.get());
        cv.put(MovieProviderContract.MovieEntry.COLUMN_FAVORITE           ,movie.isFavorite());
        return cv;
    }
    public static AsyncTaskLoader<List<Movie>> createMovieListLoader(final Uri uri, final Context context){
        return new AsyncTaskLoader<List<Movie>>(context) {
            @Override
            public List<Movie> loadInBackground() {
                //Old stuff below:
//                if(path == TMDB.TMDB_PATH_TOP_RATED_MOVIE)
//                    return TMDB.downloadTopRatedMovieList();
//                else return TMDB.downloadPopularMovieList();
                ContentResolver cr = context.getContentResolver();
                Cursor movieCursor = cr.query(uri, null, null, null, null);
                MovieProviderContract.MovieEntry.MovieCursorWrapper wrapper = new MovieProviderContract.MovieEntry.MovieCursorWrapper(movieCursor);
                ArrayList<Movie> movieList = new ArrayList<>(wrapper.getCount());
                wrapper.addMoviesToList(movieList);
                return movieList;
            }
        };
    }
    public static AsyncTaskLoader<Movie> createMovieDetailLoader(final long movie_id, Context context){
        return new AsyncTaskLoader<Movie>(context) {
            @Override
            public Movie loadInBackground() {
                ContentResolver cr = context.getContentResolver();
                Uri uri = MovieProviderContract.MovieEntry.SINGLE_MOVIE_URI.buildUpon().appendPath(Long.toString(movie_id)).build();
                Cursor movieCursor = cr.query(uri, null, null, null, null);
                MovieProviderContract.MovieEntry.MovieCursorWrapper wrapper = new MovieProviderContract.MovieEntry.MovieCursorWrapper(movieCursor);
                return wrapper.getMovie();
            }
        };
    }
    public static AsyncTaskLoader<List<MovieReview>> createMovieReviewListLoader(final long movie_id, final Context context){
        return new AsyncTaskLoader<List<MovieReview>>(context) {
            @Override
            public List<MovieReview> loadInBackground() {

                return TMDB.downloadMovieReviewList(movie_id);
            }
        };
    }
    public static AsyncTaskLoader<List<MovieVideo>> createMovieVideoListLoader(final long movie_id, final Context context) {
        return new AsyncTaskLoader<List<MovieVideo>>(context) {
            @Override
            public List<MovieVideo> loadInBackground() {
                return TMDB.downloadMovieVideoList(movie_id);
            }
        };
    }
}
