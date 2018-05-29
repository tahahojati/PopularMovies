package com.tpourjalali.popularmovies;

public class MovieVideo {
    private String mId, mKey, mSite, mName;
    private Movie mMovie;
    private int mSize;
    private long mMovieId;

    public long getMovieId() {
        return mMovieId;
    }

    public void setMovieId(long movieId) {
        mMovieId = movieId;
    }

    public String getId() {
        return mId;
    }

    public void setId(String id) {
        mId = id;
    }

    public String getKey() {
        return mKey;
    }

    public void setKey(String key) {
        mKey = key;
    }

    public String getSite() {
        return mSite;
    }

    public void setSite(String site) {
        mSite = site;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public Movie getMovie() {
        return mMovie;
    }

    public void setMovie(Movie movie) {
        mMovie = movie;
    }

    public int getSize() {
        return mSize;
    }

    public void setSize(int size) {
        mSize = size;
    }
}
