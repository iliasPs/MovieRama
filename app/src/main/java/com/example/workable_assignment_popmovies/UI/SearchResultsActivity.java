package com.example.workable_assignment_popmovies.UI;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;

import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.example.workable_assignment_popmovies.Adapters.MovieListRvAdapter;
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

public class SearchResultsActivity extends AppCompatActivity implements MovieListRvAdapter.RvClickListener, GetMoviesAsync.AsyncTaskListener {

    private static final String LOG_TAG = SearchResultsActivity.class.getSimpleName();


    ArrayList<Movie> mMoviesFull = new ArrayList<>();
    @BindView(R.id.mainLayout)
    RelativeLayout relativeLayout;
    @BindView(R.id.pbar)
    ProgressBar mPbar;
    @BindView(R.id.rv_movies)
    RecyclerView mMoviesRV;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    GetMoviesAsync newTask;
    GetMoviesAsync.AsyncTaskListener listener;
    private LinearLayoutManager linearLayoutManager;
    public List<Movie> mMoviesList = new ArrayList<>();
    public MovieListRvAdapter movieRecycleViewAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private boolean loading = false;
    int pastVisiblesItems = 0, visibleItemCount, totalItemCount;
    private static String jsonStringForPagination;
    private static String initialJsonString;


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if (mMoviesFull != null) {
            outState.putParcelableArrayList(Constants.MOVIE_LIST, mMoviesFull);
            outState.putInt(Constants.ITEM_VIEW_COUNT, totalItemCount);
            outState.putParcelableArrayList(Constants.MOVIE_LIST, (ArrayList<? extends Parcelable>) mMoviesList);
        }
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_layout);
        ButterKnife.bind(this);
        toolbar.setVisibility(View.VISIBLE);
        toolbar.setTitle(getResources().getString(R.string.search_title));
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (savedInstanceState != null && savedInstanceState.containsKey(Constants.MOVIE_LIST)) {
            mMoviesList = savedInstanceState.getParcelableArrayList(Constants.MOVIE_LIST);
            movieRecycleViewAdapter = new MovieListRvAdapter(SearchResultsActivity.this, mMoviesFull, this);
            mMoviesRV.swapAdapter(movieRecycleViewAdapter, true);
            Log.d(LOG_TAG, "getting movies from instance ");

        } else {
            Intent i = getIntent();
            String query = i.getStringExtra("query");

            newTask = new GetMoviesAsync(SearchResultsActivity.this, query, Constants.CODE_SEARCH, null);
            newTask.execute();

            movieRecycleViewAdapter = new MovieListRvAdapter(SearchResultsActivity.this, mMoviesFull, this);
            mMoviesRV.swapAdapter(movieRecycleViewAdapter, true);
        }
        //init and set the adapter
        movieRecycleViewAdapter = new MovieListRvAdapter(SearchResultsActivity.this, mMoviesFull, this);
        mMoviesRV.setAdapter(movieRecycleViewAdapter);
        linearLayoutManager = new LinearLayoutManager(this);
        mMoviesRV.setLayoutManager(linearLayoutManager);
        mMoviesRV.setHasFixedSize(true);
        mLayoutManager = mMoviesRV.getLayoutManager();

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
                    pastVisiblesItems = linearLayoutManager.findFirstVisibleItemPosition();
                    if (loading && (visibleItemCount + pastVisiblesItems) >= totalItemCount) {
                        Log.d(LOG_TAG, " totalItemCount " + totalItemCount);
                        loading = false;
                        fetchMoreData();
                    }
                }
            }
        });
    }


    //helper for infinite scroll
    private void fetchMoreData() {
        mPbar.setVisibility(View.VISIBLE);
        Log.d(LOG_TAG, "item count " + totalItemCount);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mMoviesList = JsonUtils.extractFeatureFromJson(jsonStringForPagination, totalItemCount);
                Log.d(LOG_TAG, "total count " + totalItemCount);
                if (mMoviesList != null) {
                    movieRecycleViewAdapter.addMovie(mMoviesList);
                    mPbar.setVisibility(View.GONE);
                    Log.d(LOG_TAG, "mmovies list size " + mMoviesList.size());
                }
            }
        }, 1000);

        mMoviesFull.addAll(mMoviesList);

        Log.d(LOG_TAG, "mmovies full " + mMoviesFull.size());
        JSONArray moviesArray = getJsonArray();

        if (totalItemCount == moviesArray.length()) {
            Snackbar snackbar = Snackbar
                    .make(relativeLayout, getResources().getText(R.string.no_more_movies_snack), Snackbar.LENGTH_LONG);
            snackbar.show();
            mPbar.setVisibility(View.GONE);
        }
    }

    @Override
    public void rvClickListener(View v, int position) {
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

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }

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
}
