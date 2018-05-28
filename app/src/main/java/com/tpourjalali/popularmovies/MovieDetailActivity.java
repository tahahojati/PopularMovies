package com.tpourjalali.popularmovies;

import android.app.LoaderManager;
import android.content.AsyncTaskLoader;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Objects;

public class MovieDetailActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Movie> {
    private static final int LOAD_MOVIE = 0;
    private static final String LOADER_KEY_MOVIE_ID = "movie_id";
    private boolean mMovieLoaded = false;
    private TextView mTitleTV, mDetailsTV, mDescriptionTV, mScoreTV, mMovieVoteCountTV;
    private ImageView mBackdropIV, mMovieThumbnailIV, mFavoriteIV, mRatingIV;
    private RecyclerView mTrailersRV;
    private ViewPager mReviewsVP;

    private Movie mMovie;
    private static final String INTENT_KEY_MOVIE = "movie";
    private static final String INTENT_KEY_MOVIE_ID = "movie_id";
    private long mMovieId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);
        Intent intent = getIntent();
        mMovie = (Movie)intent.getSerializableExtra(INTENT_KEY_MOVIE);
        mMovieId = intent.getLongExtra(INTENT_KEY_MOVIE_ID,0);
        Bundle args = new Bundle();
        args.putLong(LOADER_KEY_MOVIE_ID, mMovieId);
        getLoaderManager().restartLoader(LOAD_MOVIE, args, this).forceLoad();
        initViewFields();
        populateUI();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                supportFinishAfterTransition();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void initViewFields() {
        mTitleTV = findViewById(R.id.movie_title_tv);
        mDescriptionTV = findViewById(R.id.movie_description_tv);
        mScoreTV = findViewById(R.id.movie_score_tv);
        mMovieThumbnailIV = findViewById(R.id.movie_thumbnail_iv);
        mDetailsTV = findViewById(R.id.movie_details_tv);
        mBackdropIV = findViewById(R.id.movie_backdrop_iv);
        mFavoriteIV = findViewById(R.id.heart_iv);
        mRatingIV = findViewById(R.id.movie_star_iv);
        mTrailersRV = findViewById(R.id.movie_trailers_rv);
        mReviewsVP = findViewById(R.id.movie_reviews_vp);
        mMovieVoteCountTV = findViewById(R.id.movie_vote_count_tv);
    }

    private void populateUI() {
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        String detailsString =
                getString(R.string.movie_subtitle_details,
                        mMovie.getReleaseDate(),
                        mMovie.getRunTime()/60,
                        mMovie.getRunTime()%60,
                        mMovie.getOriginalLanguage(),
                        TextUtils.join(", ", mMovie.getGenres())
                );
        mTitleTV.setText(mMovie.getTitle());
        mMovieVoteCountTV.setText(Integer.toString(mMovie.getVoteCount()));
        mDetailsTV.setText(detailsString);
        mScoreTV.setText(Double.toString(mMovie.getVoteAverage()));
        mDescriptionTV.setText(mMovie.getOverview());
        Glide.with(this)
                .load(mMovie.getFullImagePath(Movie.API_POSTER_SIZE_W780, null))
                .into(mMovieThumbnailIV);
        Glide.with(this)
                .load(mMovie.getFullImagePath(null, Movie.MOVIE_IMAGE_TYPE_BACKDROP))
//                .resize(dm.widthPixels,0)
//                .centerInside()
                .into(mBackdropIV);
    }

    public static Intent newIntent(@NonNull Context context, @NonNull Movie movie, long movie_id){
        Objects.requireNonNull(movie);
        Objects.requireNonNull(context);
        Intent intent = new Intent(context, MovieDetailActivity.class);
        intent.putExtra(INTENT_KEY_MOVIE, movie);
        intent.putExtra(INTENT_KEY_MOVIE_ID, movie_id);
        return intent;
    }

    @Override
    public Loader<Movie> onCreateLoader(int id, Bundle args) {
        return new MovieLoader(this,
                Long.toString(args.getLong(LOADER_KEY_MOVIE_ID,123)));
    }

    @Override
    public void onLoadFinished(Loader<Movie> loader, Movie data) {
        if(data != null)
            mMovie = data;
        mMovieLoaded = true;
        populateUI();
    }

    @Override
    public void onLoaderReset(Loader<Movie> loader) {

    }
    private static class MovieLoader extends AsyncTaskLoader<Movie>{
        private String mMovieId = "" ;
        private final Context mContext;
        public MovieLoader(Context context, String movieId) {
            super(context);
            mContext = context;
            mMovieId = movieId;
        }
        @Override
        public Movie loadInBackground() {
            try {
                URL url = new URL(
                        Uri.parse(Movie.TMDB_URL)
                        .buildUpon()
                        .appendEncodedPath(Movie.TMDB_PATH_GET_MOVIE.replace("?", mMovieId))
                        .appendQueryParameter(Movie.TMDB_KEY_API, mContext.getString(R.string.tmdb_api_key_v3)).toString()
                );
                InputStream os = url.openStream();
                ByteArrayOutputStream bas = new ByteArrayOutputStream();
                int character = os.read();
                while(character != -1){
                    bas.write(character);
                    character = os.read();
                }
                return new Movie.Builder().setFromTMDBMovieDetailJson(bas.toString()).build();
            } catch (MalformedURLException e) {
                Log.e("Movie Detail Loader", "Could not parse Url" , e);
            } catch (IOException e) {
                Log.e("Movie Detail Loader", "Could not open connection", e);
            }
            return null;
        }
    }
}
