package com.tpourjalali.popularmovies;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class MovieListActivity extends AppCompatActivity  implements LoaderManager.LoaderCallbacks<List<Movie>>{
    public static final int LOAD_POPULAR_MOVIE_LIST = 10;
    private static final String ARG_SORT_BY = "sort by";
    private static final String TAG="MovieListActivity";
    private static final String SORT_POPULARITY = "popularity.desc";
    private static final String SORT_RATING = "vote_average.desc";
    private String TMDB_API_KEY_V3;
    private RecyclerView mRecyclerView;
    private MovieAdapter mAdapter;
    private String mSortingCriteria = SORT_POPULARITY;

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
            case SORT_RATING:
                menu.findItem(R.id.menu_sort_rating).setChecked(true);
                break;
            case SORT_POPULARITY:
            default:
                mSortingCriteria = SORT_POPULARITY;
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
                setSortingCriteria(SORT_POPULARITY);
            case R.id.menu_sort_rating:
                item.setChecked(true);
                setSortingCriteria(SORT_RATING);
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    public void setSortingCriteria(String criteria) {
        if(criteria == null )
            criteria = SORT_POPULARITY;
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
}
class MovieHolder extends RecyclerView.ViewHolder{
    private ImageView mHeartIv;
    private ImageView mPosterIv;
    private Movie mMovie;
    public MovieHolder(View itemView) {
        super(itemView);
        mHeartIv = itemView.findViewById(R.id.heart_iv);
        mPosterIv = itemView.findViewById(R.id.poster_iv);
    }
    public void bind(Movie movie){
        mMovie = movie;
        Picasso.get().load(mMovie.getPosterPath(Movie.API_POSTER_SIZE_ORIGINAL))
                .placeholder(R.drawable.ic_poster_placeholder)
                .into(mPosterIv);
    }
}
class MovieAdapter extends RecyclerView.Adapter<MovieHolder>{
    private List<Movie> mMovies = new ArrayList<>();
    public void setMovies(List<Movie> movies){
        if(movies != null) {
            mMovies = movies;
            notifyDataSetChanged();
        }
    }
    @NonNull
    @Override
    public MovieHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater li = (LayoutInflater) parent.getContext()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = li.inflate(R.layout.movie_list_item, parent, false);
        return new MovieHolder(v);
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