package com.tpourjalali.popularmovies;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class MovieDatabaseOpenHelper extends SQLiteOpenHelper{
    public static final int VERSION = 1;
    public static final String DBNAME = "popular_movies_db";
    public MovieDatabaseOpenHelper(Context context) {
        super(context, DBNAME, null, VERSION);
    }

    @Override
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);
        db.setForeignKeyConstraintsEnabled(true);

    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        createMovieTable(db);
        createReviewTable(db);
        createVideoTable(db);
    }

    private void createMovieTable(SQLiteDatabase db) {
        String moviesCreate = "CREATE TABLE " + MovieProviderContract.MovieEntry.TABLE_NAME + "("
                + MovieProviderContract.MovieEntry._ID + " integer primary key,"
                +MovieProviderContract.MovieEntry.COLUMN_TMDB_ID            +" integer, \n"
                +MovieProviderContract.MovieEntry.COLUMN_POSTER_PATH		    +" text, \n"
                +MovieProviderContract.MovieEntry.COLUMN_ADULT				+" boolean,\n"
                +MovieProviderContract.MovieEntry.COLUMN_OVERVIEW			+" text,\n"
                +MovieProviderContract.MovieEntry.COLUMN_RELEASE_DATE		+" text,\n"
                +MovieProviderContract.MovieEntry.COLUMN_ORIGINAL_TITLE		+" text,\n"
                +MovieProviderContract.MovieEntry.COLUMN_ORIGINAL_LANGUAGE	+" text,\n"
                +MovieProviderContract.MovieEntry.COLUMN_TITLE				+" text,\n"
                +MovieProviderContract.MovieEntry.COLUMN_BACKDROP_PATH		+" text,\n"
                +MovieProviderContract.MovieEntry.COLUMN_POPULARITY			+" float,\n"
                +MovieProviderContract.MovieEntry.COLUMN_VOTE_COUNT			+" integer,\n"
                +MovieProviderContract.MovieEntry.COLUMN_VIDEO				+" boolean,\n"
                +MovieProviderContract.MovieEntry.COLUMN_VOTE_AVERAGE		+" float,\n"
                +MovieProviderContract.MovieEntry.COLUMN_GENRES				+" text,\n"
                +MovieProviderContract.MovieEntry.COLUMN_RUNTIME			    +" integer,\n"
                +MovieProviderContract.MovieEntry.COLUMN_USER_RATING        +" integer,\n"
                +MovieProviderContract.MovieEntry.COLUMN_FAVORITE           +" boolean\n"
                + ");";
        db.beginTransaction();
        db.execSQL(moviesCreate);
        db.endTransaction();
    }

    private void createReviewTable(SQLiteDatabase db) {
        String reviewsCreate = "CREATE TABLE " + MovieProviderContract.ReviewEntry.TABLE_NAME + "("
                + MovieProviderContract.ReviewEntry._ID + " text primary key,"
                +MovieProviderContract.ReviewEntry.COLUMN_AUTHOR            +" text, \n"
                +MovieProviderContract.ReviewEntry.COLUMN_CONTENT		    +" text, \n"
                +MovieProviderContract.ReviewEntry.COLUMN_MOVIE_ID			+" integer,\n"
                +MovieProviderContract.ReviewEntry.COLUMN_URL			+" text,\n"
                +"FOREIGN KEY("+ MovieProviderContract.ReviewEntry.COLUMN_MOVIE_ID+") REFERENCES "
                + MovieProviderContract.MovieEntry.TABLE_NAME+"("+ MovieProviderContract.MovieEntry._ID + ")"
                + "ON DELETE CASCADE"
                + ");";
        db.beginTransaction();
        db.execSQL(reviewsCreate);
        db.endTransaction();
    }
    private void createVideoTable(SQLiteDatabase db) {
        String reviewsCreate = "CREATE TABLE " + MovieProviderContract.VideoEntry.TABLE_NAME + "("
                + MovieProviderContract.VideoEntry._ID + " text PRIMARY KEY,\n"
                + MovieProviderContract.VideoEntry.COLUMN_KEY           + " text, \n"
                + MovieProviderContract.VideoEntry.COLUMN_SITE		    + " text, \n"
                + MovieProviderContract.VideoEntry.COLUMN_MOVIE_ID			+ " integer,\n"
                + MovieProviderContract.VideoEntry.COLUMN_SIZE			+ " integer,\n"
                + MovieProviderContract.VideoEntry.COLUMN_NAME			+ " text,\n"
                + "FOREIGN KEY("+ MovieProviderContract.VideoEntry.COLUMN_MOVIE_ID+") REFERENCES "
                + MovieProviderContract.MovieEntry.TABLE_NAME+"("+ MovieProviderContract.MovieEntry._ID + ")"
                + "ON DELETE CASCADE"
                + ");";
        db.beginTransaction();
        db.execSQL(reviewsCreate);
        db.endTransaction();
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
