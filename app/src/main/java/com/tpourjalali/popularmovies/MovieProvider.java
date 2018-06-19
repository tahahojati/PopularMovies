package com.tpourjalali.popularmovies;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import java.util.ArrayList;
import java.util.List;

public class MovieProvider extends ContentProvider {
    private static final String TAG = "MoviesProvider";
    private static final String AUTHORITY = MovieProviderContract.AUTHORITY;
    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private static final int CODE_POPULAR_MOVIES = 10;
    private static final int CODE_TOPRATED_MOVIES = 20;
    private static final int CODE_SINGLE_MOVIE = 30;
    private static final int CODE_FAVORITE_MOVIE = 35;
    private static final int CODE_Review_FOR_MOVIE = 40;
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
            case CODE_Review_FOR_MOVIE:
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
            case CODE_Review_FOR_MOVIE:
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
        List<Movie> movieList;
        List<MovieReview> reviewList;
        List<MovieVideo> videoList;
        long movie_id;
        switch (sUriMatcher.match(uri)){
            case CODE_POPULAR_MOVIES:
                movieList = TMDB.downloadPopularMovieList();
                return cursorFromMovieList(movieList);
            case CODE_TOPRATED_MOVIES:
                movieList = TMDB.downloadTopRatedMovieList();
                return cursorFromMovieList(movieList);
            case CODE_FAVORITE_MOVIE:
                throw new UnsupportedOperationException("URI did not match any of the available options");
            case CODE_SINGLE_MOVIE:
                //TODO: update this
                movie_id = Long.parseLong(uri.getLastPathSegment());
                movieList = new ArrayList<>();
                movieList.add(TMDB.downloadMovie(movie_id));
                return cursorFromMovieList(movieList);
            case CODE_Review_FOR_MOVIE:
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

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        /* TODO:
        * Update should insert/remove reviews, videos, and images from local storage.
         */
        switch (sUriMatcher.match(uri)){
            case CODE_POPULAR_MOVIES:
                throw new UnsupportedOperationException("URI did not match any of the available options");
            case CODE_TOPRATED_MOVIES:
                throw new UnsupportedOperationException("URI did not match any of the available options");
            case CODE_FAVORITE_MOVIE:

            case CODE_SINGLE_MOVIE:
                MovieDatabaseOpenHelper helper = new MovieDatabaseOpenHelper(getContext());
                SQLiteDatabase db = helper.getWritableDatabase();
                if(!values.containsKey(MovieProviderContract.MovieEntry.COLUMN_FAVORITE) || !values.getAsBoolean(MovieProviderContract.MovieEntry.COLUMN_FAVORITE)){
                    // need to remove from db
                    db.delete(MovieProviderContract.MovieEntry.TABLE_NAME, MovieProviderContract.MovieEntry._ID + "=?",new String[]{
                            uri.getLastPathSegment()
                    });
                } else {
                    db.insert(MovieProviderContract.MovieEntry.TABLE_NAME, null, values);
                }
                return 1;
            case CODE_Review_FOR_MOVIE:

            case CODE_TRAILERS_FOR_MOVIE:

            case CODE_IMAGE:
            default:
                throw new UnsupportedOperationException("URI did not match any of the available options");
        }
    }

    private Cursor cursorFromMovieList(List<Movie> movieList) {
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
        for(Movie movie: movieList){
            mc.addRow(new Object[]{
                    movie.getTmdbId(),
                    movie.getPosterPath(),
                    movie.getAdult(),
                    movie.getOverview(),
                    MovieProviderContract.MovieEntry.MOVIE_DATE_FORMAT.format(movie.getReleaseDate()),
                    movie.getId(),
                    movie.getOriginalTitle(),
                    movie.getOriginalLanguage(),
                    movie.getTitle(),
                    movie.getBackdropPath(),
                    movie.getPopularity(),
                    movie.getVoteCount(),
                    movie.getVideo(),
                    movie.getVoteAverage(),
                    movie.getGenres(),
                    movie.getRunTime(),
//                    movie.getUserRating(),
//                    movie.getFavorite(),
            });
        }
        return mc;
    }
    private Cursor cursorFromReviewList(List<MovieReview> reviewList) {
        String[] colNames = new String[]{
                MovieProviderContract.ReviewEntry.COLUMN_MOVIE_ID             ,
                MovieProviderContract.ReviewEntry.COLUMN_URL			,
                MovieProviderContract.ReviewEntry.COLUMN_CONTENT				,
                MovieProviderContract.ReviewEntry.COLUMN_AUTHOR				,
                MovieProviderContract.ReviewEntry._ID					    ,
        };
        final MatrixCursor mc =  new MatrixCursor(colNames,reviewList.size());
        for(MovieReview review: reviewList){
            mc.addRow(new Object[]{
                    review.getMovieId(),
                    review.getUrl(),
                    review.getContent(),
                    review.getAuthor(),
                    review.getId(),
            });
        }
        return mc;
    }
    private Cursor cursorFromVideoList(List<MovieVideo> reviewList) {
        String[] colNames = new String[]{
                MovieProviderContract.VideoEntry.COLUMN_MOVIE_ID             ,
                MovieProviderContract.VideoEntry.COLUMN_SIZE			,
                MovieProviderContract.VideoEntry.COLUMN_NAME				,
                MovieProviderContract.VideoEntry.COLUMN_KEY				,
                MovieProviderContract.VideoEntry._ID					    ,
                MovieProviderContract.VideoEntry.COLUMN_SITE					    ,
        };
        final MatrixCursor mc =  new MatrixCursor(colNames,reviewList.size());
        for(MovieVideo video: reviewList){
            mc.addRow(new Object[]{
                    video.getMovieId(),
                    video.getSize(),
                    video.getName(),
                    video.getKey(),
                    video.getId(),
                    video.getSite(),
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
        return res;
    }
}
