package com.tpourjalali.popularmovies;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
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

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

public class MovieListActivity extends AppCompatActivity  implements LoaderManager.LoaderCallbacks<List<Movie>>{
    public static final int LOAD_POPULAR_MOVIE_LIST = 10;
    private static final String ARG_SORT_BY = "sort by";
    private static final String TAG = "MovieListActivity";
    private String TMDB_API_KEY_V3;
    private RecyclerView mRecyclerView;
    private MovieAdapter mAdapter;
    private int mSortingCriteria = TMDB.MOVIE_LIST_LOADER_SORT_POPULAR;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TMDB_API_KEY_V3 = getString(R.string.tmdb_api_key_v3);
        setContentView(R.layout.activity_movie_list);
        mRecyclerView = findViewById(R.id.recycler_view);
        mAdapter = new MovieAdapter();
        mRecyclerView.setAdapter(mAdapter);
        GridLayoutManager glm = new GridLayoutManager(this, 6);
        mRecyclerView.setLayoutManager(glm);
        glm.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                switch (position){
                    case 0:
                        return 6;
                    case 1:
                    case 2:
                        return 3;
                    default:
                        return 2;
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        reloadMovies();
    }
    private void reloadMovies(){
        Bundle args= new Bundle();
        args.putInt(ARG_SORT_BY, mSortingCriteria);
        LoaderManager lm = getSupportLoaderManager();
        lm.restartLoader(LOAD_POPULAR_MOVIE_LIST, args, this).forceLoad();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater mi = getMenuInflater();
        mi.inflate(R.menu.movie_list_menu, menu);
        switch (mSortingCriteria){
            case TMDB.MOVIE_LIST_LOADER_SORT_RATING:
                menu.findItem(R.id.menu_sort_rating).setChecked(true);
                break;
            case TMDB.MOVIE_LIST_LOADER_SORT_POPULAR:
            default:
                mSortingCriteria = TMDB.MOVIE_LIST_LOADER_SORT_POPULAR;
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
                setSortingCriteria(TMDB.MOVIE_LIST_LOADER_SORT_POPULAR);
            case R.id.menu_sort_rating:
                item.setChecked(true);
                setSortingCriteria(TMDB.MOVIE_LIST_LOADER_SORT_RATING);
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    private void setSortingCriteria(Integer criteria) {
        if(criteria == null )
            criteria = TMDB.MOVIE_LIST_LOADER_SORT_POPULAR;
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
        return TMDB.createMovieListLoader(mSortingCriteria, MovieListActivity.this);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<List<Movie>> loader, List<Movie> data) {
        mAdapter.setMovies(data);
    }

    @Override
    public void onLoaderReset(@NonNull Loader loader) {

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
            Intent intent = MovieDetailActivity.newIntent(MovieListActivity.this, mMovie, mMovie.getId());
            if(mSharedView != null) {
                ActivityOptionsCompat options =
                        ActivityOptionsCompat.makeSceneTransitionAnimation(MovieListActivity.this,
                                mSharedView, getString(R.string.transition_name_movie_poster));
                startActivity(intent, options.toBundle());
            } else {
                startActivity(intent);
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
        mMovie = movie;
        mPosterClickListener.setMovie(mMovie);
        mFavoriteClickListener.setMovie(mMovie);
        Log.d(TAG, "url: "+ mMovie.getFullImagePath(TMDB.API_POSTER_SIZE_ORIGINAL, null));
        Glide.with(itemView.getContext())
                .load(mMovie.getFullImagePath(TMDB.API_POSTER_SIZE_ORIGINAL, null))
//                .placeholder(R.drawable.ic_poster_placeholder)
                .into(mPosterIv);
    }
}