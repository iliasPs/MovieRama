package com.example.workable_assignment_popmovies.Adapters;


import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.ToggleButton;
import com.example.workable_assignment_popmovies.Database.AppDatabase;
import com.example.workable_assignment_popmovies.Models.Movie;
import com.example.workable_assignment_popmovies.R;
import com.example.workable_assignment_popmovies.UI.MovieDetailsActivity;
import com.example.workable_assignment_popmovies.UI.MoviesListActivity;
import com.example.workable_assignment_popmovies.Utils.AppExecutors;
import com.squareup.picasso.Picasso;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;


public class MovieListRvAdapter extends RecyclerView.Adapter<MovieListRvAdapter.MoviesViewHolder>  {
    private MoviesListActivity moviesListActivity = new MoviesListActivity();
    private Context mContext;
    private List<Movie> mMovies;
    private static RvClickListener listener;
    private boolean isInFavsAlready;
    private static String LOG_TAG = MovieListRvAdapter.class.getSimpleName();

    public interface RvClickListener{
         void rvClickListener(View v, int position);
    }

    public void swapData(List<Movie> movies)
    {
        if(movies == null || movies.size()==0)
            return;
        if (mMovies != null && mMovies.size()>0)
            mMovies.clear();
        mMovies.addAll(movies);
        notifyDataSetChanged();
    }

    public MovieListRvAdapter(Context c, List<Movie> myMovieData, RvClickListener listener) {
        this.mContext = c;
        this.mMovies =myMovieData;
        this.listener = listener;

    }

    @NonNull
    @Override
    public MovieListRvAdapter.MoviesViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        Context context = viewGroup.getContext();
        int layoutIdForListItem = R.layout.movie_list_item;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;
        View view = inflater.inflate(layoutIdForListItem, viewGroup, shouldAttachToParentImmediately);
        MoviesViewHolder viewHolder = new MoviesViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MovieListRvAdapter.MoviesViewHolder holder, int position) {
        Movie movie = this.mMovies.get(position);
        Picasso.get()
                .load(movie.getPoster_path())
                .placeholder(R.drawable.ic_launcher_background)
                .error(R.drawable.ic_launcher_foreground)
                .into(holder.moviePosterIv);
        holder.movieTitleTv.setText(movie.getTitle());
        holder.ratingBar.setRating(Float.valueOf(movie.getVote_average())/2);
        holder.movieReleaseDateTv.setText(modifyDateLayout(movie.getRelease_date()));
        //trying to set the togglebuttons according to the movie items existence in db
         boolean bool =isMovieInFavorites(movie);
        if(bool){
            moviesListActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    holder.favButton.setChecked(true);
                }
            });

        }else{
            moviesListActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    holder.favButton.setChecked(false);
                }
            });

        }
    }

    @Override
    public int getItemCount() {
         return (null != mMovies ? mMovies.size() : 0);
    }

    /**
     * Cache of the children views for a list item.
     */
    public class MoviesViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener {
        ImageView moviePosterIv;
        TextView movieTitleTv;
        RatingBar ratingBar;
        TextView movieReleaseDateTv;
        ToggleButton favButton;

        public MoviesViewHolder(View itemView) {
            super(itemView);
            ratingBar = itemView.findViewById(R.id.rating);
            moviePosterIv = itemView.findViewById(R.id.poster_path);
            movieTitleTv = itemView.findViewById(R.id.movieTitleListItem);
            movieReleaseDateTv = itemView.findViewById(R.id.movieReleaseDate);
            favButton = itemView.findViewById(R.id.toggleButton);
            favButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    int position = getLayoutPosition();
                    final Movie movie = mMovies.get(position);
                    if (isChecked && !isMovieInFavorites(movie)){
                    final AppDatabase mDb = AppDatabase.getInstance(mContext);
                    AppExecutors.getInstance().diskIO().execute(new Runnable() {
                        @Override
                        public void run() {
                            mDb.movieDao().insertMovie(movie);
                        }
                    });
                        favButton.setChecked(true);
                    }
                    else if (!isChecked){
                        final AppDatabase mDb = AppDatabase.getInstance(mContext);
                        AppExecutors.getInstance().diskIO().execute(new Runnable() {
                            @Override
                            public void run() {
                                mDb.movieDao().deleteMovie(movie);
                                Log.d(LOG_TAG, "movie deleted " + movie);
                            }
                        });
                        favButton.setChecked(false);
                    }
                }
            });
            itemView.setOnClickListener(this);
        }
        @Override
        public void onClick(View v) {
            Context context = v.getContext();
            int clickedPosition = getAdapterPosition();
            Intent i = new Intent(context, MovieDetailsActivity.class);
            Movie movie = mMovies.get(clickedPosition);
            i.putExtra(MovieDetailsActivity.EXTRA_MOVIE, movie);
            context.startActivity(i);
            listener.rvClickListener(v, clickedPosition);
        }
    }

    public boolean isMovieInFavorites(Movie movie) {
        final AppDatabase mDb = AppDatabase.getInstance(mContext);
        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                int dbMovieID = mDb.movieDao().searchFavsByMovieID(movie.getId());
                if(dbMovieID !=0){
                    isInFavsAlready = true;
//
                }else{
                    isInFavsAlready= false;
                }
            }
        });
        return isInFavsAlready;
    }



    /**
     * When data changes, this method updates the list of taskEntries
     * and notifies the adapter to use the new values on it
     */
    public void setMovies(List<Movie> movieEntries) {
        mMovies = movieEntries;
        notifyDataSetChanged();

    }

    public void addMovie(List<Movie> movie){
        for (Movie m : movie){
            mMovies.add(m);
        }
        notifyDataSetChanged();
    }

    private String modifyDateLayout(String inputDate){

        Date date;
        try {
            date = new SimpleDateFormat("yyyy-MM-dd").parse(inputDate);
        }catch (ParseException e){
            return null;
        }
        return new SimpleDateFormat("d MMM yyyy").format(date);
    }

    //helper
    public Movie getMovieAt(int position){
        return mMovies.get(position);
    }

}

