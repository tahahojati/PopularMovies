package com.tpourjalali.popularmovies;

import android.content.AsyncTaskLoader;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;
import java.util.Objects;

public class MovieDetailActivity extends AppCompatActivity {
    private static final int LOAD_MOVIE = 0;
    private static final int LOAD_TRAILERS = 1 ;
    private static final int LOAD_REVIEWS = 2;
    private static final String TAG = "MovieDetailActivity";
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
        Log.d(TAG, "Movie id is: "+mMovieId);
        Bundle args = new Bundle();
        args.putLong(LOADER_KEY_MOVIE_ID, mMovieId);
        LoaderManager lm = getSupportLoaderManager();
        lm.restartLoader(LOAD_MOVIE, args, createMovieDetailLoaderCallBacks()).forceLoad();
        lm.restartLoader(LOAD_TRAILERS, null, createMovieVideoLoaderCallBacks()).forceLoad();
        lm.restartLoader(LOAD_REVIEWS, null, createMovieReviewLoaderCallBacks()).forceLoad();
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
        mTrailersRV.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        mTrailersRV.setAdapter(createTrailersAdapter());
        mReviewsVP.setAdapter(createReviewsAdapter());
        mReviewsVP.setPageMargin(30);

        mFavoriteIV.setOnClickListener((v) -> {
            mMovie.setFavorite(!mMovie.isFavorite());
            AsyncTask.THREAD_POOL_EXECUTOR.execute(()->{
                ContentValues cv = MovieUtils.generateCVForMovieProvider(mMovie);
                getContentResolver().update(
                        MovieProviderContract.MovieEntry.SINGLE_MOVIE_URI.buildUpon().appendPath(Long.toString(mMovie.getId())).build(),
                        cv,
                        null,
                        null
                );
            });
            if(mMovie.isFavorite()){
                mFavoriteIV.setImageLevel(1);
            } else {
                mFavoriteIV.setImageLevel(0);
            }
        });
    }

    private PagerAdapter createReviewsAdapter() {
        return new PagerAdapter() {
            @Override
            public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
                container.removeView((View)object);
            }

            @NonNull
            @Override
            public Object instantiateItem(@NonNull ViewGroup container, int position) {
                View v = getLayoutInflater().inflate(R.layout.movie_review_list_item, container, false);
                MovieReview mr = mMovie.getReviews().get(position);
                ((TextView)v.findViewById(R.id.review_author_tv)).setText(
                        getString(R.string.movie_detail_review_author, mr.getAuthor()));
                ((TextView)v.findViewById(R.id.review_content_tv)).setText(mr.getContent());
                container.addView(v);
                return v;
            }

            @Override
            public int getCount() {
                return mMovie.getReviews().size();
            }

            @Override
            public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
                return view == object;
            }
        };
    }

    private RecyclerView.Adapter<TrailerHolder> createTrailersAdapter() {
        return new RecyclerView.Adapter<TrailerHolder>() {
            @NonNull
            @Override
            public TrailerHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                LayoutInflater li = getLayoutInflater();
                View v = li.inflate(R.layout.movie_trailer_item_view, parent, false);
                return new TrailerHolder(v);
            }

            @Override
            public void onBindViewHolder(@NonNull TrailerHolder holder, int position) {
                holder.bindVideo(mMovie.getVideos().get(position));
            }

            @Override
            public int getItemCount() {
                return mMovie.getVideos().size();
            }
        };
    }

    private void launchTrailer(MovieVideo movieVideo){
        String YouTubeBase = "https://www.youtube.com/watch";
        Uri uri = Uri.parse(YouTubeBase)
                .buildUpon()
                .appendQueryParameter("v", movieVideo.getKey())
                .build();
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(intent);
    }

    private void populateUI() {
        String detailsString =
                getString(R.string.movie_subtitle_details,
                        mMovie.getReleaseDate(),
                        mMovie.getRunTime()/60,
                        mMovie.getRunTime()%60,
                        mMovie.getOriginalLanguage().toUpperCase(),
                        TextUtils.join(", ", mMovie.getGenres())
                );
        mTitleTV.setText(mMovie.getTitle());
        if(mMovie.isFavorite()){
            mFavoriteIV.setImageLevel(1);
        } else {
            mFavoriteIV.setImageLevel(0);
        }
        mMovieVoteCountTV.setText(Integer.toString(mMovie.getVoteCount()));
        mDetailsTV.setText(detailsString);
        mScoreTV.setText(Double.toString(mMovie.getVoteAverage()));
        mDescriptionTV.setText(mMovie.getOverview());
        Glide.with(this)
                .load(mMovie.getFullImagePath(TMDB.API_POSTER_SIZE_W780, null))
                .into(mMovieThumbnailIV);
        Glide.with(this)
                .load(mMovie.getFullImagePath(null, TMDB.MOVIE_IMAGE_TYPE_BACKDROP))
                .into(mBackdropIV);
    }

    private void reloadMovieReviews(){
        mReviewsVP.getAdapter().notifyDataSetChanged();
    }

    private void reloadMovieVideos(){
        mTrailersRV.getAdapter().notifyDataSetChanged();
    }


    public static Intent newIntent(@NonNull Context context, @NonNull Movie movie, long movie_id){
        Objects.requireNonNull(movie);
        Objects.requireNonNull(context);
        Intent intent = new Intent(context, MovieDetailActivity.class);
        intent.putExtra(INTENT_KEY_MOVIE, movie);
        intent.putExtra(INTENT_KEY_MOVIE_ID, movie_id);
        return intent;
    }

    private LoaderManager.LoaderCallbacks<Movie> createMovieDetailLoaderCallBacks(){
        return new LoaderManager.LoaderCallbacks<Movie>() {
            @Override
            public Loader<Movie> onCreateLoader(int id, Bundle args) {
                return MovieUtils.createMovieDetailLoader(mMovie.getTmdbId(),MovieDetailActivity.this);
            }

            @Override
            public void onLoadFinished(Loader<Movie> loader, Movie data) {
                if(data != null)
                    mMovie =  data;
                mMovieLoaded = true;
                populateUI();
            }

            @Override
            public void onLoaderReset(Loader<Movie> loader) {

            }
        };
    }
    private LoaderManager.LoaderCallbacks<List<MovieReview>> createMovieReviewLoaderCallBacks(){
        return new LoaderManager.LoaderCallbacks<List<MovieReview>>(){

            @NonNull
            @Override
            public Loader<List<MovieReview>> onCreateLoader(int id, @Nullable Bundle args) {
                return MovieUtils.createMovieReviewListLoader(mMovieId, MovieDetailActivity.this);
            }

            @Override
            public void onLoadFinished(@NonNull Loader<List<MovieReview>> loader, List<MovieReview> data) {
                mMovie.setReviews(data);
                reloadMovieReviews();
            }

            @Override
            public void onLoaderReset(@NonNull Loader<List<MovieReview>> loader) {

            }
        };
    }
    private LoaderManager.LoaderCallbacks<List<MovieVideo>> createMovieVideoLoaderCallBacks(){
        return new LoaderManager.LoaderCallbacks<List<MovieVideo>>(){

            @NonNull
            @Override
            public Loader<List<MovieVideo>> onCreateLoader(int id, @Nullable Bundle args) {
                return MovieUtils.createMovieVideoListLoader(mMovieId, MovieDetailActivity.this);
            }

            @Override
            public void onLoadFinished(@NonNull Loader<List<MovieVideo>> loader, List<MovieVideo> data) {
                mMovie.setVideos(data);
                reloadMovieVideos();
            }

            @Override
            public void onLoaderReset(@NonNull Loader<List<MovieVideo>> loader) {

            }
        };
    }

    private class TrailerHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private ImageView mPreviewIV, mPlayIV;
        private TextView mNameTV;
        private MovieVideo mVideo;
        public TrailerHolder(View itemView) {
            super(itemView);
            mPreviewIV = itemView.findViewById(R.id.trailer_preview_iv);
            mNameTV = itemView.findViewById(R.id.trailer_name_tv);
            mPlayIV = itemView.findViewById(R.id.trailer_play_iv);
            mPreviewIV.setOnClickListener(this);
            mPlayIV.setOnClickListener(this);
        }
        @Override
        public void onClick(View v) {
            launchTrailer(mVideo);
        }
        public void bindVideo(MovieVideo movieVideo){
            mVideo = movieVideo;
            Log.d(TAG, "name of video: "+mVideo.getName());
            mNameTV.setText(mVideo.getName());
        }
    }
}
