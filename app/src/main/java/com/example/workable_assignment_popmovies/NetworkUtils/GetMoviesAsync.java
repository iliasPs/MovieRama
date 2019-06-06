package com.example.workable_assignment_popmovies.NetworkUtils;

import android.os.AsyncTask;
import android.util.Log;

import com.example.workable_assignment_popmovies.Helpers.Constants;
import com.example.workable_assignment_popmovies.Models.Movie;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

public class GetMoviesAsync extends AsyncTask<URL, Void, String> {


    private String userOption;
    private ArrayList<Movie> movieList;
    private AsyncTaskListener listener;
    private String code;
    private String queryText;
    private String movieId;


    private static final String LOG_TAG = GetMoviesAsync.class.getSimpleName();

    public interface AsyncTaskListener {

        void processFinish(String output, String code);
    }

    public GetMoviesAsync(AsyncTaskListener listener, String queryText, String queryCode, String movieId) {
        this.listener = listener;
        this.queryText = queryText;
        this.code = queryCode;
        this.movieId = movieId;
    }

    @Override
    protected String doInBackground(URL... urls) {
        URL searchMovieObjectUrl;
        String jsonString = "";
        switch (code) {
            case Constants.CODE_POPULAR:
                searchMovieObjectUrl = NetworkUtils.createUrl(Constants.CODE_POPULAR, null, null);
                try {
                    jsonString = NetworkUtils.makeHttpRequest(searchMovieObjectUrl);
                } catch (IOException e) {
                    Log.e("Main Activity", "Problem making the HTTP request.", e);
                }
                return jsonString;
            case Constants.CODE_SEARCH:
                searchMovieObjectUrl = NetworkUtils.createUrl(Constants.CODE_SEARCH, queryText, null);
                try {
                    jsonString = NetworkUtils.makeHttpRequest(searchMovieObjectUrl);

                } catch (IOException e) {
                    Log.e("Main Activity", "Problem making the HTTP request.", e);
                }
                return jsonString;

            case Constants.CODE_CAST:
                searchMovieObjectUrl = NetworkUtils.createUrl(Constants.CODE_CAST, null, movieId);
                try {
                    jsonString = NetworkUtils.makeHttpRequest(searchMovieObjectUrl);
                }catch (IOException e) {
                    Log.e("Main Activity", "Problem making the HTTP request.", e);
                }return jsonString;

            case Constants.CODE_REVIEWS:
                searchMovieObjectUrl = NetworkUtils.createUrl(Constants.CODE_REVIEWS, null, movieId);
                try {
                    jsonString = NetworkUtils.makeHttpRequest(searchMovieObjectUrl);
                }catch (IOException e) {
                    Log.e("Main Activity", "Problem making the HTTP request.", e);
                }return jsonString;

            case Constants.CODE_TRAILERS:
                searchMovieObjectUrl = NetworkUtils.createUrl(Constants.CODE_TRAILERS, null, movieId);
                try {
                    jsonString = NetworkUtils.makeHttpRequest(searchMovieObjectUrl);
                }catch (IOException e) {
                    Log.e("Main Activity", "Problem making the HTTP request.", e);
                }return jsonString;

            case Constants.CODE_SINGLE_MOVIE:
            case Constants.CODE_SIMILAR:
                searchMovieObjectUrl = NetworkUtils.createUrl(Constants.CODE_SIMILAR, null, movieId);
                try {
                    jsonString = NetworkUtils.makeHttpRequest(searchMovieObjectUrl);
                } catch (IOException e) {
                    Log.e("Main Activity", "Problem making the HTTP request.", e);
                }
                return jsonString;
        }

        return null;
    }

    @Override
    protected void onPostExecute(String jsonString) {
        if (jsonString == null) {
            Log.e(LOG_TAG, "error in the json string ");
        }
        listener.processFinish(jsonString, code);
    }

}