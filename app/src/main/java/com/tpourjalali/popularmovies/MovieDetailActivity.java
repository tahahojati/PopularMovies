package com.tpourjalali.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.Objects;

public class MovieDetailActivity extends AppCompatActivity {
    private TextView mTitleTV, mSubtitleTV, mDescriptionTV, mDateTV, mScoreTV, mLangTV;
    private ImageView mBackgroundIV;
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
        mSubtitleTV = findViewById(R.id.movie_subtitle_tv);
        mDescriptionTV = findViewById(R.id.movie_description_tv);
        mDateTV = findViewById(R.id.movie_date_tv);
        mScoreTV = findViewById(R.id.movie_score_tv);
        mLangTV = findViewById(R.id.movie_language_tv);
        mBackgroundIV = findViewById(R.id.movie_background_iv);
    }

    private void populateUI() {
        mTitleTV.setText(mMovie.getTitle());
        mDateTV.setText(mMovie.getReleaseDate("yyyy MMM d"));
        mScoreTV.setText(Double.toString(mMovie.getVoteAverage()));
        mDescriptionTV.setText(mMovie.getOverview());
        mSubtitleTV.setText(mMovie.getTitle());
        mLangTV.setText(mMovie.getOriginalLanguage().toUpperCase());
        Picasso.get().load(mMovie.getPosterPath(Movie.API_POSTER_SIZE_W780))
                .into(mBackgroundIV);
    }

    public static Intent newIntent(@NonNull Context context, @NonNull Movie movie){
        Objects.requireNonNull(movie);
        Objects.requireNonNull(context);
        Intent intent = new Intent(context, MovieDetailActivity.class);
        intent.putExtra(INTENT_KEY_MOVIE, movie);
        return intent;
    }
}
