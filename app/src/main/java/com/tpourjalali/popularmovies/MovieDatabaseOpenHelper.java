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
    public void onCreate(SQLiteDatabase db) {
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

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
