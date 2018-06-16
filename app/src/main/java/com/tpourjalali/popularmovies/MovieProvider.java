package com.tpourjalali.popularmovies;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

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
        switch (sUriMatcher.match(uri)){
            case CODE_POPULAR_MOVIES:
                throw new UnsupportedOperationException("URI did not match any of the available options");
            case CODE_TOPRATED_MOVIES:
                throw new UnsupportedOperationException("URI did not match any of the available options");
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
        List<Movie> movieList;
        List<MovieReview> reviewList;
        List<MovieVideo> videoList;
        switch (sUriMatcher.match(uri)){
            case CODE_POPULAR_MOVIES:
                movieList = TMDB.downloadPopularMovieList();
                return cursorFromMovieList(movieList);
            case CODE_TOPRATED_MOVIES:
                movieList = TMDB.downloadTopRatedMovieList();
                return cursorFromMovieList(movieList);
            case CODE_FAVORITE_MOVIE:

            case CODE_SINGLE_MOVIE:

            case CODE_Review_FOR_MOVIE:

            case CODE_TRAILERS_FOR_MOVIE:

            case CODE_IMAGE:
                break;
            default:
                throw new UnsupportedOperationException("URI did not match any of the available options");
        }
        return null;
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

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
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
