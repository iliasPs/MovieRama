package com.example.workable_assignment_popmovies.Utils;

import android.text.TextUtils;
import android.util.Log;

import com.example.workable_assignment_popmovies.Helpers.Constants;
import com.example.workable_assignment_popmovies.Models.Cast;
import com.example.workable_assignment_popmovies.Models.Movie;
import com.example.workable_assignment_popmovies.Models.Review;
import com.example.workable_assignment_popmovies.Models.Similar;
import com.example.workable_assignment_popmovies.Models.Trailer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;

public class JsonUtils {

    private JsonUtils() {
    }

    private static String mCurrentImagePath;


    public static ArrayList<Movie> extractFeatureFromJson(String movieJson, int numberOfItemsParsed) {
        if (TextUtils.isEmpty(movieJson)) {
            return null;
        }
        ArrayList<Movie> movies = new ArrayList<>();
        try {

            JSONObject baseJsonResponse = new JSONObject(movieJson);
            JSONArray moviesArray = baseJsonResponse.getJSONArray("results");
            for (int i = numberOfItemsParsed; i < (numberOfItemsParsed + 5); i++) {


                JSONObject currentMovie = moviesArray.getJSONObject(i);
                String currentTitle = currentMovie.getString("original_title");
                String currentReleaseDate = currentMovie.getString("release_date");
                if (currentMovie.getString("poster_path") != null) {
                    mCurrentImagePath = Constants.IMAGE_BASE_URL + Constants.IMAGE_SIZE_POSTER + currentMovie.getString("backdrop_path");
                } else {
                    mCurrentImagePath = null;
                }
                String currentAvg = currentMovie.getString("vote_average");
                String currentPlot = currentMovie.getString("overview");
                int currentID = currentMovie.getInt("id");

                JSONArray genreArray = currentMovie.getJSONArray("genre_ids");
                ArrayList currentGenre = new ArrayList();
                for (int y = 0; y < genreArray.length(); y++) {

                    currentGenre.add(genreArray.get(y));
                }

                movies.add(new Movie(currentTitle, currentReleaseDate, mCurrentImagePath, currentAvg, currentPlot, currentID, currentGenre));
            }
        } catch (JSONException e) {
            Log.e("QueryUtils", "Problem parsing the news JSON results", e);
        }
        return movies;
    }

    public static ArrayList<Review> extractReviewsFromJson(String reviewJson) {
        if (TextUtils.isEmpty(reviewJson)) {
            return null;
        }
        ArrayList<Review> reviews = new ArrayList<>();
        try {

            JSONObject baseJsonResponse = new JSONObject(reviewJson);
            JSONArray reviewsArray = baseJsonResponse.getJSONArray("results");
            for (int i = 0; i < 2; i++) {

                JSONObject currentReview = reviewsArray.getJSONObject(i);
                String currentAuthor = currentReview.getString("author");
                String currentReviewContent = currentReview.getString("content");
                String currentReviewID = currentReview.getString("id");
                String currentReviewUrl = currentReview.getString("url");

                reviews.add(new Review(currentAuthor, currentReviewContent, currentReviewID, currentReviewUrl));
            }
        } catch (JSONException e) {
            Log.e("QueryUtils", "Problem parsing the news JSON results", e);
        }
        return reviews;
    }

    public static ArrayList<Trailer> extractTrailersFromJson(String trailersJson) {
        if (TextUtils.isEmpty(trailersJson)) {
            return null;
        }
        ArrayList<Trailer> trailers = new ArrayList<>();
        try {

            JSONObject baseJsonResponse = new JSONObject(trailersJson);
            JSONArray trailersArray = baseJsonResponse.getJSONArray("results");
            for (int i = 0; i < 3; i++) {

                JSONObject currentTrailer = trailersArray.getJSONObject(i);
                String currentTrailerID = currentTrailer.getString("id");
                String currentTrailerKey = currentTrailer.getString("key");
                String currentTrailerName = currentTrailer.getString("name");


                trailers.add(new Trailer(currentTrailerID, currentTrailerKey, currentTrailerName));
            }
        } catch (JSONException e) {
            Log.e("QueryUtils", "Problem parsing the news JSON results", e);
        }
        return trailers;
    }


    public static ArrayList<Similar> extractSimilarsFromJson(String similarsJson) {
        if (TextUtils.isEmpty(similarsJson)) {
            return null;
        }
        ArrayList<Similar> similars = new ArrayList<>();
        try {

            JSONObject baseJsonResponse = new JSONObject(similarsJson);
            JSONArray similarssArray = baseJsonResponse.getJSONArray("results");
            for (int i = 0; i < similarssArray.length(); i++) {

                JSONObject currentSimilar = similarssArray.getJSONObject(i);

                if (currentSimilar.getString("poster_path") != null) {
                    mCurrentImagePath = Constants.IMAGE_BASE_URL + Constants.IMAGE_SIZE_POSTER_MICRO + currentSimilar.getString("poster_path");
                } else {
                    mCurrentImagePath = null;
                }
                String currentSimilarTitle = currentSimilar.getString("title");
                String currentSimilarReleaseDate = currentSimilar.getString("release_date");
                String currentAvg = currentSimilar.getString("vote_average");
                String currentPlot = currentSimilar.getString("overview");
                int currentId = currentSimilar.getInt("id");


                similars.add(new Similar(mCurrentImagePath, currentId, currentSimilarTitle, currentAvg, currentSimilarReleaseDate, currentPlot));
            }
        } catch (JSONException e) {
            Log.e("QueryUtils", "Problem parsing the news JSON results", e);
        }
        return similars;
    }

    public static Cast extractCastFromJson(String castJson) {
        if (TextUtils.isEmpty(castJson)) {
            return null;
        }
        Cast cast = null;
        try {
            JSONObject baseJsonResponse = new JSONObject(castJson);
            JSONObject creditsObj = baseJsonResponse.getJSONObject("credits");
            JSONArray castArray = creditsObj.getJSONArray("cast");
            String actors;
            StringBuilder stringBuilder = new StringBuilder();
            String comma = "";
            for (int i = 0; i < 3; i++) {
                JSONObject currentCast = castArray.getJSONObject(i);

                stringBuilder.append(comma);
                stringBuilder.append(currentCast.getString("name"));
                comma = ", ";

            }
            JSONArray crewArray = creditsObj.getJSONArray("crew");
            for (int i = 0; i < crewArray.length(); i++) {
                JSONObject currentCrew = crewArray.getJSONObject(i);
                if (currentCrew.getString("job").contentEquals("Director")) {
                    String director = currentCrew.getString("name");
                    cast = new Cast(stringBuilder.toString(), director);
                }
            }

        } catch (JSONException e) {
            Log.e("QueryUtils", "Problem parsing the news JSON results", e);
        }
        return cast;

    }
    /**
     * Convert the {@link InputStream} into a String which contains the
     * whole JSON response from the server.
     */
    public static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line != null) {
                output.append(line);
                line = reader.readLine();
            }
        }
        return output.toString();
    }
}

