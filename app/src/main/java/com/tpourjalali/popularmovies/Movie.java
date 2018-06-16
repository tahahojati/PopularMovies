package com.tpourjalali.popularmovies;

import android.support.annotation.Nullable;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Movie implements Serializable {
    private static final String TAG = "MovieClass";
    private String mPosterPath;
    private Boolean mAdult;
    private boolean mFavorite;
    private String mOverview;
    private Date mReleaseDate;
    private long mTmdbId;
    private long mId;
    private String mOriginalTitle;
    private String mOriginalLanguage;
    private String mTitle;
    private String mBackdropPath;
    private Double mPopularity;
    private int mVoteCount;
    private Boolean mVideo;
    private Double mVoteAverage;
    private List<String> mGenres = new ArrayList<>();
    private List<MovieReview> mReviews = new ArrayList<>();
    private List<MovieVideo> mVideos = new ArrayList<>();
    private int mRunTime;

    public static class Builder{
        private Movie mMovie = new Movie();
        public Builder(){}
        private Builder(Movie movie) {
            mMovie = movie;
        }
        public Builder setFromTMDBMovieDetailJson(String str){
            JSONObject jo = null;
            try {
                jo = new JSONObject(str);
                return setFromTMDBMovieDetailJson(jo);
            }
            catch (JSONException e) {
                Log.e(TAG, "Could not parse JSON: "+str, e);
            }
            return this;
        }
        public Builder setFromTMDBMovieDetailJson(JSONObject jo){

                JSONArray genreArray = jo.optJSONArray(TMDB.JSON_KEY_GENRE);
                if(genreArray != null){
                    List<String> genres = new ArrayList<>();
                    for(int i = 0; i < genreArray.length(); ++i){
                        genres.add(genreArray.optJSONObject(i).optString(TMDB.JSON_KEY_GENRE_NAME));
                    }
                    genres(genres);
                }
                title(jo.optString(TMDB.JSON_KEY_TITLE))
                    .overview(jo.optString(TMDB.JSON_KEY_OVERVIEW))
                    .runTimeMinutes(jo.optInt(TMDB.JSON_KEY_RUNTIME))
                    .voteCount(jo.optInt(TMDB.JSON_KEY_VOTE_COUNT))
                    .tmdbId(jo.optInt(TMDB.JSON_KEY_ID))
                    .backdropPath(jo.optString(TMDB.JSON_KEY_BACKDROP_PATH))
                    .originalLanguage(jo.optString(TMDB.JSON_KEY_ORIGINALLANGUAGE))
                    .posterPath(jo.optString(TMDB.JSON_KEY_POSTER_PATH))
                    .voteAverage(jo.optDouble(TMDB.JSON_KEY_VOTE_AVERAGE));
                try{
                    releaseDate("yyyy-mm-dd", jo.optString(TMDB.JSON_KEY_RELEASEDATE));
                } catch (ParseException e) {
                Log.e(TAG, "could not parse release date string(require yyyy-MM-dd: "+ jo.optString(TMDB.JSON_KEY_RELEASEDATE));
            }
            return this;
        }

        public Builder id(long id) {
            mMovie.setId(id);
            return this;
        }

        public Builder backdropPath(String path){
            mMovie.setBackdropPath(path);
            return this;
        }
        public Builder voteCount(int i) {
            mMovie.setVoteCount(i);
            return this;
        }

        public Builder runTimeMinutes(int mins) {
            mMovie.setRunTime(mins);
            return this;
        }

        public Builder genres(List<String> genres) {
            mMovie.setGenres(genres);
            return this;
        }

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
        public Builder releaseDate(String format, String s) throws ParseException {
            SimpleDateFormat sdf = new SimpleDateFormat(format);
            mMovie.setReleaseDate(sdf.parse(s));
            return this;
        }
        public Builder voteAverage(Double d){
            mMovie.setVoteAverage(d);
            return this;
        }
        public Builder tmdbId(long i){
            mMovie.setTmdbId(i);
            return this;
        }

        public Builder originalLanguage(String s) {
            mMovie.setOriginalLanguage(s);
            return this;
        }
    }

    private Movie(){}
    public Builder buildUpon(){
        return new Builder(this);
    }
    public void setId(long id) {
        mId = id;
    }

    public boolean isFavorite() {
        return mFavorite;
    }

    public void setFavorite(boolean favorite) {
        mFavorite = favorite;
    }

    public String getPosterPath() {
        return mPosterPath;
    }
    public String getFullImagePath(@Nullable String posterSize, @Nullable Integer imageType){
        String path = null;
        if(imageType == null)
            imageType = TMDB.MOVIE_IMAGE_TYPE_POSTER;
        if (posterSize == null)
            posterSize = TMDB.API_POSTER_SIZE_ORIGINAL;
        switch (imageType){
            case TMDB.MOVIE_IMAGE_TYPE_BACKDROP:
                path = mBackdropPath;
                break;
            default:
                path = mPosterPath;
        }
        Log.d(TAG, "image url: " + TMDB.API_POSTER_BASE_URL + posterSize + path);
        return TMDB.API_POSTER_BASE_URL  + posterSize + path;
    }





    public void setPosterPath(String posterPath) {
        mPosterPath = posterPath;
    }

    public void setGenres(List<String> genres) {
        if(genres == null)
            mGenres.clear();
        else
            mGenres = genres;
    }

    public List<MovieReview> getReviews() {
        return mReviews;
    }
    public long getId(){
        return mId;
    }
    public void setReviews(List<MovieReview> reviews) {
        if(reviews == null){
            mReviews.clear();
            return;
        }
        mReviews = reviews;
    }

    public List<MovieVideo> getVideos() {
        return mVideos;
    }

    public void setVideos(List<MovieVideo> videos) {
        if(videos == null){
            mVideos.clear();
            return;
        }
        mVideos = videos;
    }

    public void setRunTime(int runTime) {
        mRunTime = runTime;
    }

    public List<String> getGenres() {
        return mGenres;
    }

    public int getRunTime() {
        return mRunTime;
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

    public Date getReleaseDate() {
        return mReleaseDate;
    }
    public String getReleaseDate(String format){
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        return sdf.format(mReleaseDate);
    }

    public void setReleaseDate(Date releaseDate) {
        mReleaseDate = releaseDate;
    }

    public long getTmdbId() {
        return mTmdbId;
    }

    public void setTmdbId(long id) {
        mTmdbId = id;
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
