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
    private String mOverview;
    private Date mReleaseDate;
    private long mId;
    private String mOriginalTitle;
    private String mOriginalLanguage;
    private String mTitle;
    private String mBackdropPath;
    private Double mPopularity;
    private int mVoteCount;
    private Boolean mVideo;
    private Double mVoteAverage;
    public static final String JSON_KEY_GENRE = "genres";
    public static final String TMDB_PATH_POPULAR_MOVIE = "/3/movie/popular";
    public static final String TMDB_PATH_TOP_RATED_MOVIE = "/3/movie/top_rated";
    public static final String TMDB_PATH_GET_VIDEOS = "/3/movie/?/videos";
    public static final String TMDB_PATH_GET_MOVIE = "/3/movie/?";
    public static final String TMDB_PATH_GET_REVIEWS = "3/movie/?/reviews";
    public static final String TMDB_KEY_SORTBY = "sort_by";
    public static final String TMDB_KEY_PAGE = "page";
    public static final String TMDB_KEY_API = "api_key";
    public static final String TMDB_KEY_RELEASE_LTE = "release_date.lte";
    public static final String TMDB_URL = "https://api.themoviedb.org";
    public static final String API_POSTER_BASE_URL = "https://image.tmdb.org/t/p";
    public static final String API_POSTER_SIZE_ORIGINAL = "/original";
    public static final String API_POSTER_SIZE_W92 = "/w92";
    public static final String API_POSTER_SIZE_W154 = "/w154";
    public static final String API_POSTER_SIZE_W185 = "/w185";
    public static final String API_POSTER_SIZE_W342 = "/w342";
    public static final String API_POSTER_SIZE_W500 = "/w500";
    public static final String API_POSTER_SIZE_W780 = "/w780";
    public static final String JSON_KEY_POSTER_PATH = "poster_path";
    public static final String JSON_KEY_ADULT= "adult";
    public static final int MOVIE_IMAGE_TYPE_BACKDROP = 23;
    public static final int MOVIE_IMAGE_TYPE_POSTER = 20;
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
    public static final String JSON_KEY_RUNTIME = "runtime";
    public static final String JSON_KEY_GENRE_NAME = "name";
    public static final String JSON_KEY_VOTE_AVERAGE= "vote_average";
    private List<String> mGenres = new ArrayList<>();
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

                JSONArray genreArray = jo.optJSONArray(Movie.JSON_KEY_GENRE);
                if(genreArray != null){
                    List<String> genres = new ArrayList<>();
                    for(int i = 0; i < genreArray.length(); ++i){
                        genres.add(genreArray.optJSONObject(i).optString(Movie.JSON_KEY_GENRE_NAME));
                    }
                    genres(genres);
                }
                title(jo.optString(Movie.JSON_KEY_TITLE))
                    .overview(jo.optString(Movie.JSON_KEY_OVERVIEW))
                    .runTimeMinutes(jo.optInt(Movie.JSON_KEY_RUNTIME))
                    .voteCount(jo.optInt(Movie.JSON_KEY_VOTE_COUNT))
                    .id(jo.optInt(Movie.JSON_KEY_ID))
                    .backdropPath(jo.optString(Movie.JSON_KEY_BACKDROP_PATH))
                    .originalLanguage(jo.optString(Movie.JSON_KEY_ORIGINALLANGUAGE))
                    .posterPath(jo.optString(Movie.JSON_KEY_POSTER_PATH))
                    .voteAverage(jo.optDouble(Movie.JSON_KEY_VOTE_AVERAGE));
                try{
                    releaseDate("yyyy-mm-dd", jo.optString(Movie.JSON_KEY_RELEASEDATE));
                } catch (ParseException e) {
                Log.e(TAG, "could not parse release date string(require yyyy-MM-dd: "+ jo.optString(Movie.JSON_KEY_RELEASEDATE));
            }
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

        private Builder genres(List<String> genres) {
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
        public Builder id(int i){
            mMovie.setId(i);
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
    public String getPosterPath() {
        return mPosterPath;
    }
    public String getFullImagePath(@Nullable String posterSize, @Nullable Integer imageType){
        String path = null;
        if(imageType == null)
            imageType = MOVIE_IMAGE_TYPE_POSTER;
        if (posterSize == null)
            posterSize = API_POSTER_SIZE_ORIGINAL;
        switch (imageType){
            case MOVIE_IMAGE_TYPE_BACKDROP:
                path = mBackdropPath;
                break;
            default:
                path = mPosterPath;
        }
        Log.d(TAG, "image url: " + API_POSTER_BASE_URL + posterSize + path);
        return API_POSTER_BASE_URL  + posterSize + path;
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
