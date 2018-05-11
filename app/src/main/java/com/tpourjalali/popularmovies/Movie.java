package com.tpourjalali.popularmovies;

public class Movie {
    private String mPosterPath;
    private Boolean mAdult;
    private String mOverview;
    private String mReleaseDate;
    private long mId;
    private String mOriginalTitle;
    private String mOriginalLanguage;
    private String mTitle;
    private String mBackdropPath;
    private Double mPopularity;
    private int mVoteCount;
    private Boolean mVideo;
    private Double mVoteAverage;
    public static final String JSON_KEY_POSTER_PATH = "poster_path";
    public static final String JSON_KEY_ADULT= "adult";
    public static final String JSON_KEY_OVERVIEW= "overview";
    public static final String JSON_KEY_RELEASEDATE= "release_date";
    public static final String JSON_KEY_ID= "id";
    public static final String JSON_KEY_ORIGINALTITLE= "original_title";
    public static final String JSON_KEY_ORIGINALLANGUAGE= "original_language";
    public static final String JSON_KEY_TITLE= "title";
    public static final String JSON_KEY_BACKDROP_PATH= "backdrop_path";
    public static final String JSON_KEY_POPULARITY= "popularity";
    public static final String JSON_KEY_VOTE_COUNT= "vote_count";
    public static final String JSON_KEY_VIDEO= "video";
    public static final String JSON_KEY_VOTE_AVERAGE= "vote_average";

    public static class Builder{
        private Movie mMovie = new Movie();
        public Movie build(){
            return mMovie;
        }
        public Builder posterPath(String s){
            mMovie.setPosterPath(s);
            return this;
        }
        public Builder title(String s){
            mMovie.setTitle(s);
            return this;
        }
        public Builder overview(String s){
            mMovie.setOverview(s);
            return this;
        }
        public Builder releaseDate(String s){
            mMovie.setReleaseDate(s);
            return this;
        }
        public Builder voteAverage(Double d){
            mMovie.setVoteAverage(d);
            return this;
        }
        public Builder id(int i){
            mMovie.setId(i);
            return this;
        }
    }

    private Movie(){}
    public String getPosterPath() {
        return mPosterPath;
    }

    public void setPosterPath(String posterPath) {
        mPosterPath = posterPath;
    }

    public Boolean getAdult() {
        return mAdult;
    }

    public void setAdult(Boolean adult) {
        mAdult = adult;
    }

    public String getOverview() {
        return mOverview;
    }

    public void setOverview(String overview) {
        mOverview = overview;
    }

    public String getReleaseDate() {
        return mReleaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        mReleaseDate = releaseDate;
    }

    public long getId() {
        return mId;
    }

    public void setId(long id) {
        mId = id;
    }

    public String getOriginalTitle() {
        return mOriginalTitle;
    }

    public void setOriginalTitle(String originalTitle) {
        mOriginalTitle = originalTitle;
    }

    public String getOriginalLanguage() {
        return mOriginalLanguage;
    }

    public void setOriginalLanguage(String originalLanguage) {
        mOriginalLanguage = originalLanguage;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public String getBackdropPath() {
        return mBackdropPath;
    }

    public void setBackdropPath(String backdropPath) {
        mBackdropPath = backdropPath;
    }

    public Double getPopularity() {
        return mPopularity;
    }

    public void setPopularity(Double popularity) {
        mPopularity = popularity;
    }

    public int getVoteCount() {
        return mVoteCount;
    }

    public void setVoteCount(int voteCount) {
        mVoteCount = voteCount;
    }

    public Boolean getVideo() {
        return mVideo;
    }

    public void setVideo(Boolean video) {
        mVideo = video;
    }

    public Double getVoteAverage() {
        return mVoteAverage;
    }

    public void setVoteAverage(Double voteAverage) {
        mVoteAverage = voteAverage;
    }
}