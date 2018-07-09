package com.tpourjalali.popularmovies;

import android.app.usage.NetworkStatsManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Browser;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

public class MovieListActivity extends AppCompatActivity  implements LoaderManager.LoaderCallbacks<List<Movie>>{
    public static final int LOAD_POPULAR_MOVIE_LIST = 10;
    private static final String ARG_SORT_BY = "sort by";
    private static final String TAG = "MovieListActivity";
    private static final int REQUEST_MOVIE_DETAIL = 100;
    private static final String STATE_KEY_SORTING_CRITERIA = "sort_criteria";
    private static final String STATE_KEY_OFFLINE = "mOffline";
    private RecyclerView mRecyclerView;
    private static final IntentFilter NETWORK_INTENT_FILTER = new IntentFilter();
    private MovieAdapter mAdapter;
    private boolean mOffline = false;
    private Uri mSortingCriteria = MovieProviderContract.MovieEntry.POPULAR_MOVIES_URI;

    public static final String RESULT_EXTRA_MOVIE_ID = "movie_id";
    public static final String RESULT_EXTRA_FAVORITE = "favorite";

    static {
        NETWORK_INTENT_FILTER.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
    }

    private BroadcastReceiver mNetworkReceiver = new NetworkReceiver();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            Uri sortc = savedInstanceState.getParcelable(STATE_KEY_SORTING_CRITERIA);
            if(sortc != null)
                mSortingCriteria = sortc;
            mOffline = savedInstanceState.getBoolean(STATE_KEY_OFFLINE, false);
        }
        //check if we are offline.
        checkNetwork();

        Log.d(TAG, "onCreate");
        setContentView(R.layout.activity_movie_list);
        mRecyclerView = findViewById(R.id.recycler_view);
        mAdapter = new MovieAdapter();
        mRecyclerView.setAdapter(mAdapter);
        GridLayoutManager glm = new GridLayoutManager(this, 6);
        mRecyclerView.setLayoutManager(glm);
        glm.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                switch (getResources().getConfiguration().orientation) {
                    case Configuration.ORIENTATION_LANDSCAPE:
                        switch (position) {
                            case 0:
                            case 1:
                                return 3;
                            case 2:
                            case 3:
                            case 4:
                                return 2;
                            default:
                                return 1;
                        }
                    default:
                        switch (position) {
                            case 0:
                                return 6;
                            case 1:
                            case 2:
                                return 3;
                            default:
                                return 2;
                        }
                }
            }
        });
        reloadMovies();

    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onStart was called");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d(TAG, "onRestart was called");
        checkNetwork(); // automatically does the right thing.
    }

    private void registerNetworkBroadcastReceiver() {

    }

    private void checkNetwork() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = cm.getActiveNetworkInfo();
        if (info == null || !info.isConnectedOrConnecting()) {
            setNetworkStatus(true);
        } else {
            setNetworkStatus(false);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume was called");
        registerReceiver(mNetworkReceiver, NETWORK_INTENT_FILTER);
    }

    @Override
    protected void onPause() {
        unregisterReceiver(mNetworkReceiver);
        super.onPause();
    }

    @Override
    protected void onStop() {
        Log.d(TAG, "onStop was called");
        super.onStop();
    }

    private void reloadMovies(){
//        Bundle args= new Bundle();
//        args.putParcelable(ARG_SORT_BY, mSortingCriteria);
        LoaderManager lm = getSupportLoaderManager();
        lm.restartLoader(LOAD_POPULAR_MOVIE_LIST, null, this).forceLoad();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater mi = getMenuInflater();
        mi.inflate(R.menu.movie_list_menu, menu);
        if(mOffline){
            menu.findItem(R.id.menu_sort_rating).setEnabled(false);
            menu.findItem(R.id.menu_sort_popularity).setEnabled(false);
        }
        if (mSortingCriteria.equals(MovieProviderContract.MovieEntry.TOPRATED_MOVIES_URI)) {
            menu.findItem(R.id.menu_sort_rating).setChecked(true);
        }
        else if (mSortingCriteria.equals(MovieProviderContract.MovieEntry.FAVORITE_MOVIES_URI)){
            menu.findItem(R.id.menu_sort_favorites).setChecked(true);
        } else {
            mSortingCriteria = MovieProviderContract.MovieEntry.POPULAR_MOVIES_URI;
            menu.findItem(R.id.menu_sort_popularity).setChecked(true);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.d(TAG, item.getTitle().toString());
        item.getGroupId();
        switch (item.getItemId()) {
            case R.id.menu_sort_popularity:
                item.setChecked(true);
                setSortingCriteria(MovieProviderContract.MovieEntry.POPULAR_MOVIES_URI);
                break;
            case R.id.menu_sort_rating:
                item.setChecked(true);
                setSortingCriteria(MovieProviderContract.MovieEntry.TOPRATED_MOVIES_URI);
                break;
            case R.id.menu_sort_favorites:
                item.setChecked(true);
                setSortingCriteria(MovieProviderContract.MovieEntry.FAVORITE_MOVIES_URI);
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }
    private void setSortingCriteria(@Nullable Uri criteria) {
        if(criteria == null )
            criteria = MovieProviderContract.MovieEntry.POPULAR_MOVIES_URI;
        if(criteria == mSortingCriteria)
            return;
        else {
            mSortingCriteria = criteria;
            reloadMovies();
        }

    }

    @NonNull
    @Override
    public Loader<List<Movie>> onCreateLoader(int id, @Nullable Bundle args) {
        Log.d(TAG, "creating a new MovieLoader!");
        return MovieUtils.createMovieListLoader(mSortingCriteria, MovieListActivity.this);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<List<Movie>> loader, List<Movie> data) {
        mAdapter.setMovies(data);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "onActivityResult was called");
        if(requestCode == REQUEST_MOVIE_DETAIL){
            if(resultCode == RESULT_OK ){
                if(data != null && data.hasExtra(RESULT_EXTRA_MOVIE_ID) && data.hasExtra(RESULT_EXTRA_FAVORITE)){
                    long movie_id = data.getLongExtra(RESULT_EXTRA_MOVIE_ID, 0);
                    boolean favorite = data.getBooleanExtra(RESULT_EXTRA_FAVORITE, false);
                    int i = -1;
                    for(Movie movie: mAdapter.mMovies){
                        ++i;
                        if (movie.getId() == movie_id){
                            movie.setFavorite(favorite);
                            break;
                        }
                    }
                    if(mSortingCriteria.equals(MovieProviderContract.MovieEntry.FAVORITE_MOVIES_URI)){
                        //this must mean that we removed a movie from the favorites.
                        mAdapter.mMovies.remove(i);
                        mAdapter.notifyItemRemoved(i);
                    } else {
                        mAdapter.notifyItemChanged(i);
                    }
                }
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader loader) {

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelable(STATE_KEY_SORTING_CRITERIA, mSortingCriteria);
        outState.putBoolean(STATE_KEY_OFFLINE, mOffline);
        super.onSaveInstanceState(outState);
    }

    private void setNetworkStatus(boolean offline) {
        if (offline == mOffline) return;
        mOffline = offline;
        if (mOffline) {
            setSortingCriteria(MovieProviderContract.MovieEntry.FAVORITE_MOVIES_URI);
            Toast toast = Toast.makeText(this, R.string.offline_toast_msg, Toast.LENGTH_SHORT);
            toast.show();
        } else {
            Toast toast = Toast.makeText(this, R.string.online_toast_msg, Toast.LENGTH_SHORT);
            toast.show();
        }
        invalidateOptionsMenu();
    }

    private class MovieAdapter extends RecyclerView.Adapter<MovieHolder> {
        private List<Movie> mMovies = new ArrayList<>();

        public void setMovies(List<Movie> movies) {
            if (movies != null) {
                mMovies = movies;
                notifyDataSetChanged();
            }
        }

        @NonNull
        @Override
        public MovieHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            Context context = MovieListActivity.this;
            LayoutInflater li = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View v = li.inflate(R.layout.movie_list_item, parent, false);
            return new MovieHolder(v, new PosterClickListener(), new FavoriteClickListener());
        }

        @Override
        public void onBindViewHolder(@NonNull MovieHolder holder, int position) {
            holder.bind(mMovies.get(position));
        }

        @Override
        public int getItemCount() {
            return mMovies.size();
        }
    }
    public class PosterClickListener implements View.OnClickListener{
        private Movie mMovie;
        private ImageView mSharedView;
        @Override
        public void onClick(View v) {
            //NOTE: I use startActivityForResult instead of startActivity because I want to update the favorite status of a movie after the app returns from the detail activity.
            Intent intent = MovieDetailActivity.newIntent(MovieListActivity.this, mMovie, mMovie.getTmdbId());
            if(mSharedView != null) {
                ActivityOptionsCompat options =
                        ActivityOptionsCompat.makeSceneTransitionAnimation(MovieListActivity.this,
                                mSharedView, getString(R.string.transition_name_movie_poster));
                startActivityForResult(intent, REQUEST_MOVIE_DETAIL, options.toBundle());
            } else {
                startActivityForResult(intent, REQUEST_MOVIE_DETAIL);
            }
        }
        public void setSharedView(ImageView iv){
            mSharedView = iv;
        }
        public void setMovie(Movie movie) {
            mMovie = movie;
        }
    }

    public class FavoriteClickListener implements View.OnClickListener{
        private Movie mMovie;

        @Override
        public void onClick(View v) {

        }
        public void setMovie(Movie movie) {
            mMovie = movie;
        }
    }

    private class NetworkReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            checkNetwork();
        }
    }

}

class MovieHolder extends RecyclerView.ViewHolder{
    private static final String TAG = "MovieHolder";
    private ImageView mHeartIv;
    private ImageView mPosterIv;
    private Movie mMovie;
    private Context mContext;
    private MovieListActivity.PosterClickListener mPosterClickListener;
    private MovieListActivity.FavoriteClickListener mFavoriteClickListener;
    public MovieHolder(View itemView, MovieListActivity.PosterClickListener posterListener, MovieListActivity.FavoriteClickListener favoriteListener) {
        super(itemView);
        mContext = itemView.getContext();
        mHeartIv = itemView.findViewById(R.id.heart_iv);
        mPosterIv = itemView.findViewById(R.id.poster_iv);
        mPosterIv.setOnClickListener(posterListener);
        mHeartIv.setOnClickListener(favoriteListener);
        mFavoriteClickListener = favoriteListener;
        mPosterClickListener = posterListener;
        mPosterClickListener.setSharedView(mPosterIv);
        mPosterClickListener = posterListener;
    }

    public void bind(Movie movie) {
        if(mMovie != null && mMovie.getId() == movie.getId()){
            Log.d(TAG, "efficient binding");
            //same movie, we do not need to do most of the work.  Just check to see if movie's favorite status has changed.
            if(mMovie.isFavorite() != movie.isFavorite()){
                mMovie.setFavorite(movie.isFavorite());
                drawFavorite();
            }
        } else {
            Log.d(TAG, "inefficient binding");
            mMovie = movie;
            drawFavorite();
            mPosterClickListener.setMovie(mMovie);
            mFavoriteClickListener.setMovie(mMovie);
            Log.d(TAG, "url: " + mMovie.getFullImagePath(TMDB.API_POSTER_SIZE_ORIGINAL, null));
            Glide.with(itemView.getContext())
                    .load(mMovie.getFullImagePath(TMDB.API_POSTER_SIZE_ORIGINAL, null))
//                .placeholder(R.drawable.ic_poster_placeholder)
                    .into(mPosterIv);
        }
    }
    private void drawFavorite(){
        if(mMovie.isFavorite()){
            mHeartIv.setImageLevel(1);
        } else {
            mHeartIv.setImageLevel(0);
        }
    }
}