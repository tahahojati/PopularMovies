package com.tpourjalali.popularmovies;

class MovieReview {
    private Movie mMovie;
    private long mMovieId;
    private String mId, mAuthor, mContent, mUrl;

    public Movie getMovie() {
        return mMovie;
    }

    public void setMovie(Movie movie) {
        mMovie = movie;
    }

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

    public String getAuthor() {
        return mAuthor;
    }

    public void setAuthor(String author) {
        mAuthor = author;
    }

    public String getContent() {
        return mContent;
    }

    public void setContent(String content) {
        mContent = content;
    }

    public String getUrl() {
        return mUrl;
    }

    public void setUrl(String url) {
        mUrl = url;
    }
}
