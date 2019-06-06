package com.example.workable_assignment_popmovies.Database;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import com.example.workable_assignment_popmovies.Models.Movie;

import java.util.List;

@Dao
public interface MovieDao {

    @Query("SELECT * FROM moviesTable ORDER BY id")
    LiveData<List<Movie>> loadAllMovies();

    @Query("SELECT * FROM moviesTable WHERE id = :id")
    LiveData<Movie> loadMovieById(int id);

    @Query("SELECT id FROM moviesTable WHERE id = :id")
    int searchFavsByMovieID(int id);

    @Query("SELECT id FROM moviesTable WHERE id = :id")
    LiveData<Integer> searchFavsByMovieIDLive(int id);

    @Insert
    void insertMovie(Movie movie);

    @Query("DELETE FROM moviesTable WHERE movieId = :movieID")
    void deleteByID(int movieID);

    @Delete
    void deleteMovie(Movie movie);
}
