package com.tpourjalali.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.Objects;

public class MovieDetailActivity extends AppCompatActivity {
    private TextView mTitleTV, mDetailsTV, mDescriptionTV, mScoreTV;
    private ImageView mBackdropIV, mMovieThumbnailIV, mFavoriteIV, mRatingIV;
    private RecyclerView mTrailersRV;
    private ViewPager mReviewsVP;

    private Movie mMovie;
    private static final String INTENT_KEY_MOVIE = "movie";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);
        Intent intent = getIntent();
        mMovie = (Movie) intent.getSerializableExtra(INTENT_KEY_MOVIE);
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

    public static Intent newIntent(@NonNull Context context, @NonNull Movie movie){
        Objects.requireNonNull(movie);
        Objects.requireNonNull(context);
        Intent intent = new Intent(context, MovieDetailActivity.class);
        intent.putExtra(INTENT_KEY_MOVIE, movie);
        return intent;
    }
}
