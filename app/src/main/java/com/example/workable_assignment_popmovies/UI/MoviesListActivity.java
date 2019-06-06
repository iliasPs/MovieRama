package com.example.workable_assignment_popmovies.UI;


import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SearchView;

import com.example.workable_assignment_popmovies.Adapters.MovieListRvAdapter;
import com.example.workable_assignment_popmovies.Database.AppDatabase;
import com.example.workable_assignment_popmovies.Helpers.Constants;
import com.example.workable_assignment_popmovies.Models.Movie;
import com.example.workable_assignment_popmovies.NetworkUtils.GetMoviesAsync;
import com.example.workable_assignment_popmovies.R;
import com.example.workable_assignment_popmovies.Utils.JsonUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MoviesListActivity extends AppCompatActivity implements GetMoviesAsync.AsyncTaskListener, MovieListRvAdapter.RvClickListener {


    private static final String LOG_TAG = MoviesListActivity.class.getSimpleName();
    @BindView(R.id.mainLayout)
    RelativeLayout relativeLayout;
    @BindView(R.id.search_bar)
    SearchView searchView;
    @BindView(R.id.swipe)
    SwipeRefreshLayout swipeRefreshLayout;
    private boolean loading = false;
    int pastVisibleItems, visibleItemCount;
    int totalItemCount = 0;
    @BindView(R.id.rv_movies)
    RecyclerView mMoviesRV;
    private LinearLayoutManager linearLayoutManager;
    GetMoviesAsync.AsyncTaskListener listener;
    public List<Movie> mMoviesList = new ArrayList<>();
    public MovieListRvAdapter movieRecycleViewAdapter;
    GetMoviesAsync newTask;
    @BindView(R.id.pbar)
    ProgressBar mPbar;
    ArrayList<Movie> mMoviesFull = new ArrayList<>();
    private static Movie currentMovie;
    private static AppDatabase mDb;
    private static String jsonStringForPagination;
    private static String initialJsonString;

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if (mMoviesFull != null) {
            outState.putParcelableArrayList(Constants.MOVIE_LIST, mMoviesFull);
            outState.putInt(Constants.ITEM_VIEW_COUNT, totalItemCount);
            outState.putParcelableArrayList(Constants.MINI_MOVIE_LIST, (ArrayList<? extends Parcelable>) mMoviesList);
        }
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState != null
                && savedInstanceState.containsKey(Constants.MOVIE_LIST)
                && savedInstanceState.containsKey(Constants.ITEM_VIEW_COUNT) && savedInstanceState.containsKey(Constants.MINI_MOVIE_LIST)) {
            mMoviesFull = savedInstanceState.getParcelableArrayList(Constants.MOVIE_LIST);
            totalItemCount = savedInstanceState.getInt(Constants.ITEM_VIEW_COUNT);
            mMoviesList = savedInstanceState.getParcelableArrayList(Constants.MINI_MOVIE_LIST);
            movieRecycleViewAdapter = new MovieListRvAdapter(MoviesListActivity.this, mMoviesFull, this);
            mMoviesRV.setAdapter(movieRecycleViewAdapter);
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        mDb = AppDatabase.getInstance(this);
        Log.d(LOG_TAG, " mdb instance" + mDb);

        if (savedInstanceState != null && savedInstanceState.containsKey(Constants.MOVIE_LIST) && savedInstanceState.containsKey(Constants.ITEM_VIEW_COUNT)) {
            mMoviesFull = savedInstanceState.getParcelableArrayList(Constants.MOVIE_LIST);
            totalItemCount = savedInstanceState.getInt(Constants.ITEM_VIEW_COUNT);
            movieRecycleViewAdapter = new MovieListRvAdapter(MoviesListActivity.this, mMoviesFull, this);
            mMoviesRV.swapAdapter(movieRecycleViewAdapter, true);
            Log.d(LOG_TAG, "getting movies from instance ");
        } else {
            newTask = new GetMoviesAsync(MoviesListActivity.this, null, Constants.CODE_POPULAR, null);
            newTask.execute();

        }
        searchView.setQueryHint(getResources().getString(R.string.searchHint));
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                String searchWord = query.toLowerCase();
                newTask = new GetMoviesAsync(MoviesListActivity.this, searchWord, Constants.CODE_SEARCH, null);
                newTask.execute();

                //getting the query to the search activity
                Intent i = new Intent(getApplicationContext(), SearchResultsActivity.class);
                i.putExtra("query", searchWord);
                startActivity(i);

                Log.d(LOG_TAG, "asdfsdf " + searchWord);
                return false;
            }
            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        //init and set the adapter
        movieRecycleViewAdapter = new MovieListRvAdapter(MoviesListActivity.this, mMoviesFull, this);
        mMoviesRV.setAdapter(movieRecycleViewAdapter);
        linearLayoutManager = new LinearLayoutManager(this);
        mMoviesRV.setLayoutManager(linearLayoutManager);
        mMoviesRV.setHasFixedSize(true);

        //implementing infinite scrolling
        mMoviesRV.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                    loading = true;
                }
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                //check for scroll down
                if (dy > 0)
                {
                    visibleItemCount = linearLayoutManager.getChildCount();
                    totalItemCount = linearLayoutManager.getItemCount();
                    pastVisibleItems = linearLayoutManager.findFirstVisibleItemPosition();

                    if (loading && (visibleItemCount + pastVisibleItems) >= totalItemCount) {
                        loading = false;
                        fetchMoreData();
                    }
                }
            }
        });

        //refreshing
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        newTask = new GetMoviesAsync(MoviesListActivity.this, null, Constants.CODE_POPULAR, null);
                        newTask.execute();
                        totalItemCount = 0;
                        swipeRefreshLayout.setRefreshing(false);
                    }
                }, 2000);

            }
        });

    }

    //helper for the infinite Scroll
    private void fetchMoreData() {
        mPbar.setVisibility(View.VISIBLE);
        Log.d(LOG_TAG, "item count " + totalItemCount);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mMoviesList = JsonUtils.extractFeatureFromJson(jsonStringForPagination, totalItemCount);
                Log.d(LOG_TAG, "json string for paging " + jsonStringForPagination);
                if (mMoviesList != null) {
                    movieRecycleViewAdapter.addMovie(mMoviesList);
                    mPbar.setVisibility(View.GONE);
                    Log.d(LOG_TAG, "mmovies list size " + mMoviesList.size());
                }

            }
        }, 1000);

        mMoviesFull.addAll(mMoviesList);
        JSONArray moviesArray = getJsonArray();
        if (totalItemCount == moviesArray.length()) {
            Snackbar snackbar = Snackbar
                    .make(relativeLayout, getResources().getText(R.string.no_more_movies_snack), Snackbar.LENGTH_LONG);
            snackbar.show();
            mPbar.setVisibility(View.GONE);
        }
    }

    // getting the length of the initial json array we downloaded to help infinite scroll tell the user we are out of items
    private JSONArray getJsonArray() {
        JSONArray moviesArray = null;
        try {
            JSONObject baseJsonResponse = new JSONObject(initialJsonString);
            moviesArray = baseJsonResponse.getJSONArray("results");

        } catch (JSONException e) {
            Log.d(LOG_TAG, "Json e " + e);
        }
        return moviesArray;
    }


    /**
     * getting results back from GetMoviesAsyncTask
     *
     * @param jsonString
     * @param code
     */
    @Override
    public void processFinish(String jsonString, String code) {
        initialJsonString = jsonString;
        jsonStringForPagination = jsonString;
        mMoviesFull = JsonUtils.extractFeatureFromJson(jsonString, totalItemCount);
        movieRecycleViewAdapter.swapData(mMoviesFull);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //setting null to avoid memory leaks
        listener = null;
    }


    @Override
    protected void onResume() {
        super.onResume();
        //removing the focus from searchview because on rotation it was opening the keyboard
        searchView.clearFocus();
        movieRecycleViewAdapter.notifyDataSetChanged();
    }


    //adapter informing this activity, which movie was clicked
    @Override
    public void rvClickListener(View v, int position) {
        Movie movie = mMoviesFull.get(position);
        currentMovie = mMoviesFull.get(position);
        Log.d(LOG_TAG, "current movie " + currentMovie.getTitle());

    }

}
