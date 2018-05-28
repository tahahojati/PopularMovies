package com.tpourjalali.popularmovies;

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
    private String mSortingCriteria = Movie.TMDB_PATH_POPULAR_MOVIE;

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
        args.putString(ARG_SORT_BY, mSortingCriteria);
        LoaderManager lm = getSupportLoaderManager();
        lm.restartLoader(LOAD_POPULAR_MOVIE_LIST, args, this).forceLoad();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater mi = getMenuInflater();
        mi.inflate(R.menu.movie_list_menu, menu);
        switch (mSortingCriteria){
            case Movie.TMDB_PATH_TOP_RATED_MOVIE:
                menu.findItem(R.id.menu_sort_rating).setChecked(true);
                break;
            case Movie.TMDB_PATH_POPULAR_MOVIE:
            default:
                mSortingCriteria = Movie.TMDB_PATH_POPULAR_MOVIE;
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
                setSortingCriteria(Movie.TMDB_PATH_POPULAR_MOVIE);
            case R.id.menu_sort_rating:
                item.setChecked(true);
                setSortingCriteria(Movie.TMDB_PATH_TOP_RATED_MOVIE);
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    public void setSortingCriteria(String criteria) {
        if(criteria == null )
            criteria = Movie.TMDB_PATH_POPULAR_MOVIE;
        if(criteria.equals(mSortingCriteria))
            return;
        else {
            mSortingCriteria = criteria;
            reloadMovies();
        }

    }

    @NonNull
    @Override
    public Loader onCreateLoader(int id, @Nullable Bundle args) {
        switch (id){
            case LOAD_POPULAR_MOVIE_LIST:
                Log.d(TAG, "creating a new MovieLoader!");
                return new MovieLoader(this, TMDB_API_KEY_V3, args.getString(ARG_SORT_BY));
            default:
                throw new IllegalArgumentException("MovieLoader createLoader called with invalid id: "+id);
        }
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
            Intent intent = MovieDetailActivity.newIntent(MovieListActivity.this, mMovie);
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
        Log.d(TAG, "url: "+ mMovie.getFullImagePath(Movie.API_POSTER_SIZE_ORIGINAL, null));
        Glide.with(itemView.getContext())
                .load(mMovie.getFullImagePath(Movie.API_POSTER_SIZE_ORIGINAL, null))
//                .placeholder(R.drawable.ic_poster_placeholder)
                .into(mPosterIv);
    }
}