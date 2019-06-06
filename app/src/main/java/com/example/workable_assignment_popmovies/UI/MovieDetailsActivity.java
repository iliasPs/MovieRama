package com.example.workable_assignment_popmovies.UI;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.example.workable_assignment_popmovies.Adapters.MovieReviewsRvAdapter;
import com.example.workable_assignment_popmovies.Adapters.MovieTrailersRvAdapter;
import com.example.workable_assignment_popmovies.Adapters.SimilarAdapter;
import com.example.workable_assignment_popmovies.Database.AppDatabase;
import com.example.workable_assignment_popmovies.Helpers.Constants;
import com.example.workable_assignment_popmovies.Models.Cast;
import com.example.workable_assignment_popmovies.Models.Movie;
import com.example.workable_assignment_popmovies.Models.Review;
import com.example.workable_assignment_popmovies.Models.Similar;
import com.example.workable_assignment_popmovies.Models.Trailer;
import com.example.workable_assignment_popmovies.NetworkUtils.GetMoviesAsync;
import com.example.workable_assignment_popmovies.R;
import com.example.workable_assignment_popmovies.Utils.AppExecutors;
import com.example.workable_assignment_popmovies.Utils.JsonUtils;
import com.squareup.picasso.Picasso;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MovieDetailsActivity extends AppCompatActivity implements GetMoviesAsync.AsyncTaskListener {

    public static final String EXTRA_MOVIE = "MOVIE";
    private static final String REVIEW_BUTTON = "reviews";
    private static final String REVIEWS_KEY = "reviews_key";
    private static final String TRAILERS_KEY = "trailers_key";
    private static final String IS_IN_FAVORITES = "isInFavorites";
    private static final String INSTANCE_MOVIE_ID = "instanceMovieId";
    private static final String LOG_TAG = MovieDetailsActivity.class.getSimpleName();
    private static final int DEFAULT_MOVIE_ID = -1;
    public ArrayList<Review> mReviews = new ArrayList<>();
    public ArrayList<Trailer> mTrailers = new ArrayList<>();
    public ArrayList<Similar> mSimilars = new ArrayList<>();

    private Cast mCast;
    public boolean isReviewButtonClicked;
    @BindView(R.id.reviewText)
    TextView reviewText;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.movieTitle)
    TextView movieTitleTv;
    @BindView(R.id.movieReleaseDate)
    TextView movieReleaseDateTv;
    @BindView(R.id.rating)
    RatingBar movieAvgTv;
    @BindView(R.id.moviePlot)
    TextView moviePlotTv;
    @BindView(R.id.poster_path)
    ImageView moviePosterIv;
    @BindView(R.id.movieDirector)
    TextView movieDirector;
    @BindView(R.id.movieCast)
    TextView movieCast;
    private GetMoviesAsync newTask;
    @Nullable
    @BindView(R.id.rv_movieReviews)
    RecyclerView mReviewList;
    @Nullable
    @BindView(R.id.trailers_rv)
    RecyclerView mTrailersList;
    @Nullable
    @BindView(R.id.similarRV)
    RecyclerView mSimilarsList;
    @BindView(R.id.movieGenre)
    TextView movieGenre;
    Movie mMovie;
    @BindView(R.id.toggleButton)
    ToggleButton favoriteToggle;
    @BindView(R.id.trailerToggle)
    ToggleButton trailerToggle;
    @BindView(R.id.similarToggle)
    ToggleButton similarToggle;
    @BindView(R.id.reviewToggle)
    ToggleButton reviewToggle;
    @BindView(R.id.trailerGroup)
    View trailergroup;
    @BindView(R.id.similarGroup)
    View similarGroup;
    private SimilarAdapter mSimilarAdapter;
    private MovieTrailersRvAdapter mTrailerAdapter;
    private MovieReviewsRvAdapter mReviewAdapter;
    private AppDatabase mDb;
    private int mMovieId = DEFAULT_MOVIE_ID;
    private boolean isInFavsAlready;
    private boolean isSimilarsButtonClicked;
    String actors;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);
        mDb = AppDatabase.getInstance(getApplicationContext());

        ButterKnife.bind(this);
        restoreData(savedInstanceState);

        toolbar.setVisibility(View.VISIBLE);
        toolbar.setTitle(getResources().getString(R.string.movie_details_title));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        //adding listeners to togglebuttons to handle calls
        reviewToggle.setOnCheckedChangeListener(new ShowReviewsListener());
        trailerToggle.setOnCheckedChangeListener(new ShowTrailersListener());
        similarToggle.setOnCheckedChangeListener(new ShowSimilarListener());

        mReviewAdapter = new MovieReviewsRvAdapter(MovieDetailsActivity.this, mReviews);
        mReviewList.addItemDecoration(new DividerItemDecoration(getApplicationContext(), DividerItemDecoration.VERTICAL));
        mReviewList.setAdapter(mReviewAdapter);

        mTrailerAdapter = new MovieTrailersRvAdapter(MovieDetailsActivity.this, mTrailers);
        mTrailersList.setAdapter(mTrailerAdapter);

        mSimilarAdapter = new SimilarAdapter(MovieDetailsActivity.this, mSimilars);
        mSimilarsList.setAdapter(mSimilarAdapter);

        Intent i = getIntent();
        if (i != null && i.hasExtra(EXTRA_MOVIE)) {
            if (mMovieId == DEFAULT_MOVIE_ID) {
                mMovieId = i.getIntExtra(EXTRA_MOVIE, DEFAULT_MOVIE_ID);
                mMovie = i.getParcelableExtra(EXTRA_MOVIE);
                populateUI(mMovie);
            }
        }
        //getting cast
        String movieid = String.valueOf(mMovie.getId());
        newTask = new GetMoviesAsync(MovieDetailsActivity.this, null, Constants.CODE_CAST, movieid);
        newTask.execute();


        if (isReviewButtonClicked) {
            showReviews();
        }
        int movieID = mMovie.getId();
        isMovieInFavorites(movieID);
        favoriteToggle.setOnCheckedChangeListener(new FavoriteListener());
    }


    private void restoreData(Bundle savedInstanceState) {
        if (savedInstanceState != null && savedInstanceState.containsKey(INSTANCE_MOVIE_ID)) {
            mMovieId = savedInstanceState.getInt(INSTANCE_MOVIE_ID, DEFAULT_MOVIE_ID);
        }
        if (savedInstanceState != null && savedInstanceState.containsKey(IS_IN_FAVORITES)) {
            isInFavsAlready = savedInstanceState.getBoolean(IS_IN_FAVORITES, false);
        }
        if (savedInstanceState != null && savedInstanceState.containsKey(REVIEW_BUTTON)) {
            isReviewButtonClicked = savedInstanceState.getBoolean(REVIEW_BUTTON, false);
            if (isReviewButtonClicked) {
            }
        }
        if (savedInstanceState != null && savedInstanceState.containsKey(REVIEWS_KEY)) {
            mReviews = savedInstanceState.getParcelableArrayList(REVIEWS_KEY);
            mReviewAdapter = new MovieReviewsRvAdapter(MovieDetailsActivity.this, mReviews);
            mReviewList.swapAdapter(mReviewAdapter, true);
        }
        if (savedInstanceState != null && savedInstanceState.containsKey(TRAILERS_KEY)) {
            mTrailers = savedInstanceState.getParcelableArrayList(TRAILERS_KEY);
            mTrailerAdapter = new MovieTrailersRvAdapter(MovieDetailsActivity.this, mTrailers);
            mTrailersList.swapAdapter(mTrailerAdapter, true);
        }

    }


    private void setReviews() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        mReviewList.setLayoutManager(linearLayoutManager);
        String movieid = String.valueOf(mMovie.getId());
        newTask = new GetMoviesAsync(MovieDetailsActivity.this, null, Constants.CODE_REVIEWS, movieid);
        newTask.execute();
    }

    private void setTrailers() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        mTrailersList.setLayoutManager(linearLayoutManager);
        String movieid = String.valueOf(mMovie.getId());
        newTask = new GetMoviesAsync(MovieDetailsActivity.this, null, Constants.CODE_TRAILERS, movieid);
        newTask.execute();
    }

    private void setSimilars() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        mSimilarsList.setLayoutManager(linearLayoutManager);
        String movieid = String.valueOf(mMovie.getId());
        newTask = new GetMoviesAsync(MovieDetailsActivity.this, null, Constants.CODE_SIMILAR, movieid);
        newTask.execute();
    }

    private void populateUI(Movie movie) {
        Picasso.get()
                .load(movie.getPoster_path())
                .into(moviePosterIv);

        movieTitleTv.setText(movie.getTitle());
        movieReleaseDateTv.setText(modifyDateLayout(movie.getRelease_date()));
        movieAvgTv.setRating(Float.valueOf(movie.getVote_average()) / 2);
        moviePlotTv.setText(movie.getPlot());

        ArrayList<Integer> genres = movie.getGenre_ids();
        StringBuilder stringBuilder = new StringBuilder();
        String comma = "";
        for (int i = 0; i < genres.size(); i++) {
            int genreid = (genres.get(i));

            stringBuilder.append(comma);
            stringBuilder.append(genreMapping(genreid));
            comma = ", ";
        }
        movieGenre.setText(stringBuilder);

    }

    private void populateCrew() {
        actors = mCast.getActors();
        movieCast.setText(actors);
        movieDirector.setText(mCast.getDirector());
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt(INSTANCE_MOVIE_ID, mMovieId);
        outState.putBoolean(IS_IN_FAVORITES, isInFavsAlready);
        outState.putBoolean(REVIEW_BUTTON, isReviewButtonClicked);
        outState.putParcelableArrayList(REVIEWS_KEY, mReviews);
        outState.putParcelableArrayList(TRAILERS_KEY, mTrailers);
        super.onSaveInstanceState(outState);
    }


    public void showReviews() {
        mReviewList.setHasFixedSize(true);
        mReviewList.setVisibility(View.VISIBLE);
    }

    public void showTrailers() {
        mTrailersList.setHasFixedSize(true);
        mTrailersList.setVisibility(View.VISIBLE);

    }

    private void showSimilars() {
        mSimilarsList.setHasFixedSize(true);
        mSimilarsList.setVisibility(View.VISIBLE);
    }

    private boolean isMovieInFavorites(int id) {
        LiveData<Integer> dbMovieID = mDb.movieDao().searchFavsByMovieIDLive(id);
        dbMovieID.observe(this, new Observer<Integer>() {
                    @Override
                    public void onChanged(@Nullable Integer movieID) {
                        if (movieID != null) {
                            isInFavsAlready = true;
                            favoriteToggle.setChecked(true);
                        } else if (movieID == null) {
                            isInFavsAlready = false;
                            favoriteToggle.setChecked(false);
                        }
                    }
                }
        );
        return isInFavsAlready;
    }

    //getting the results of the http calls
    @Override
    public void processFinish(String jsonString, String code) {
        switch (code) {
            case Constants.CODE_REVIEWS:
                mReviews = JsonUtils.extractReviewsFromJson(jsonString);
                mReviewAdapter.swapData(mReviews);
            case Constants.CODE_TRAILERS:
                mTrailers = JsonUtils.extractTrailersFromJson(jsonString);
                mTrailerAdapter.swapData(mTrailers);
            case Constants.CODE_SIMILAR:
                mSimilars = JsonUtils.extractSimilarsFromJson(jsonString);
                mSimilarAdapter.swapData(mSimilars);
            case Constants.CODE_CAST:
                try {
                    mCast = JsonUtils.extractCastFromJson(jsonString);
                    populateCrew();
                } catch (NullPointerException e) {
                }
        }

    }

    private class FavoriteListener implements CompoundButton.OnCheckedChangeListener {
        @Override
        public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
            if (isChecked && isInFavsAlready == false) {
                AppExecutors.getInstance().diskIO().execute(new Runnable() {
                    @Override
                    public void run() {
                        mDb.movieDao().insertMovie(mMovie);
                    }
                });
            } else if (isChecked == false) {
                AppExecutors.getInstance().diskIO().execute(new Runnable() {
                    @Override
                    public void run() {
                        int id = mMovie.getId();
                        mDb.movieDao().deleteByID(id);
                    }
                });
            }
        }
    }

    private class ShowReviewsListener implements CompoundButton.OnCheckedChangeListener {
        @Override
        public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
            if (isChecked) {
                setReviews();
                showReviews();
                if (mReviews == null) {
                    mReviewList.setVisibility(View.GONE);
                    reviewText.setVisibility(View.VISIBLE);
                    reviewText.setText(R.string.no_reviews);
                }
                isReviewButtonClicked = true;
                reviewToggle.setVisibility(View.GONE);
            } else {
                isReviewButtonClicked = false;
                mReviewList.setVisibility(View.GONE);
            }
        }
    }

    private class ShowTrailersListener implements CompoundButton.OnCheckedChangeListener {
        @Override
        public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
            if (isChecked) {
                trailergroup.setVisibility(View.VISIBLE);
                setTrailers();
                showTrailers();
                isReviewButtonClicked = true;
                trailerToggle.setVisibility(View.GONE);
            } else {
                isReviewButtonClicked = false;
                trailerToggle.setVisibility(View.VISIBLE);
                mTrailersList.setVisibility(View.GONE);
            }
        }
    }

    private class ShowSimilarListener implements CompoundButton.OnCheckedChangeListener {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if (isChecked) {
                similarGroup.setVisibility(View.VISIBLE);
                setSimilars();
                showSimilars();
                isSimilarsButtonClicked = true;
                similarToggle.setVisibility(View.GONE);
            } else {
                isSimilarsButtonClicked = false;
                mTrailersList.setVisibility(View.GONE);

            }
        }
    }

    //genre mapping
    private final String genreMapping(int genreId) {
        final HashMap<Integer, String> data = new HashMap<>();
        data.put(28, "Action");
        data.put(12, "Adventure");
        data.put(16, "Animation");
        data.put(35, "Comedy");
        data.put(80, "Crime");
        data.put(99, "Documentary");
        data.put(18, "Drama");
        data.put(10751, "Family");
        data.put(14, "Fantasy");
        data.put(36, "History");
        data.put(27, "Horror");
        data.put(10402, "Music");
        data.put(9648, "Mystery");
        data.put(10749, "Romance");
        data.put(878, "Science Fiction");
        data.put(10770, "TV Movie");
        data.put(53, "Thriller");
        data.put(10752, "War");
        data.put(37, "Western");
        return data.get(genreId);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }

    //format date helper
    private String modifyDateLayout(String inputDate) {
        Date date;
        try {
            date = new SimpleDateFormat("yyyy-MM-dd").parse(inputDate);
        } catch (ParseException e) {
            return null;
        }
        return new SimpleDateFormat("d MMM yyyy").format(date);
    }
}
