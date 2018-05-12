package com.tpourjalali.popularmovies;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.recyclerview.extensions.ListAdapter;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class MovieListActivity extends AppCompatActivity  implements LoaderManager.LoaderCallbacks<List<Movie>>{
    public static final int LOAD_POPULAR_MOVIE_LIST = 10;
    private static final String TAG="MovieListActivity";
    private String TMDB_API_KEY_V3;
    private RecyclerView mRecyclerView;
    private MovieAdapter mAdapter;
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
        LoaderManager lm = getSupportLoaderManager();
        lm.restartLoader(LOAD_POPULAR_MOVIE_LIST, null, this).forceLoad();
    }

    @NonNull
    @Override
    public Loader onCreateLoader(int id, @Nullable Bundle args) {
        switch (id){
            case LOAD_POPULAR_MOVIE_LIST:
                Log.d(TAG, "creating a new MovieLoader!");
                return new MovieLoader(this, TMDB_API_KEY_V3);
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