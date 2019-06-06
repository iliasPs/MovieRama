package com.example.workable_assignment_popmovies.NetworkUtils;

import android.net.Uri;
import android.util.Log;

import com.example.workable_assignment_popmovies.Helpers.Constants;
import com.example.workable_assignment_popmovies.Utils.JsonUtils;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class NetworkUtils {

    final static private String VIDEOS_URL = "videos";
    final static private String REVIEWS_URL = "reviews";


    /**
     * Tag for the log messages
     */
    private static final String LOG_TAG = NetworkUtils.class.getSimpleName();

    private NetworkUtils() {
    }


    /**
     * Returns new URL object from the given string URL.
     */
    public static URL createUrl(String searchCode, String query, String movieID) {
        String baseUrl = "";
        Uri builtUri = null;
        URL url = null;
        switch (searchCode) {

            case Constants.CODE_POPULAR:
                baseUrl = Constants.POPULAR_URL;
                builtUri = Uri.parse(baseUrl).buildUpon()
                        .appendQueryParameter("api_key", Constants.API_KEY)
                        .build();
                try {
                    url = new URL(builtUri.toString());
                } catch (MalformedURLException e) {
                    Log.e(LOG_TAG, "Problem building the URL ", e);
                }
                return url;
            case Constants.CODE_SEARCH:
                baseUrl = Constants.SEARCH_BASE_URL;
                builtUri = Uri.parse(baseUrl).buildUpon()
                        .appendQueryParameter("api_key", Constants.API_KEY)
                        .appendQueryParameter("query", query)
                        .build();
                url = null;
                try {
                    url = new URL(builtUri.toString());
                    Log.d(LOG_TAG, "url is " + url.toString());
                } catch (MalformedURLException e) {
                    Log.e(LOG_TAG, "Problem building the URL ", e);
                }
                return url;


            case Constants.CODE_SIMILAR:
                baseUrl = Constants.BASE_MOVIE_URL;
                builtUri = Uri.parse(baseUrl).buildUpon()
                        .appendPath(movieID)
                        .appendEncodedPath(Constants.SIMILAR_URL)
                        .appendQueryParameter("api_key",Constants.API_KEY)
                        .build();
                Log.d(LOG_TAG, "similars url " + builtUri.toString());

                try {
                    url = new URL(builtUri.toString());

                } catch (MalformedURLException e) {
                    Log.e(LOG_TAG, "Problem building the URL ", e);
                }
                return url;
            case Constants.REVIEWS_URL:

                baseUrl = Constants.BASE_MOVIE_URL;
                builtUri = Uri.parse(baseUrl).buildUpon()
                        .appendPath(movieID)
                        .appendEncodedPath(REVIEWS_URL)
                        .appendQueryParameter("api_key",Constants.API_KEY)
                        .build();
                try {
                    url = new URL(builtUri.toString());
                    Log.d(LOG_TAG, "url is " + url.toString());
                } catch (MalformedURLException e) {
                    Log.e(LOG_TAG, "Problem building the URL ", e);
                }
                return url;
            case Constants.CODE_CAST:
                Log.d(LOG_TAG, "cde " + Constants.CODE_CAST);
                Log.d(LOG_TAG, "id " + movieID);

                baseUrl = Constants.BASE_MOVIE_URL;
                builtUri = Uri.parse(baseUrl).buildUpon()
                        .appendPath(movieID)
                        .appendQueryParameter("api_key",Constants.API_KEY)
                        .appendQueryParameter(Constants.CREDITS_URL, "credits")
                        .build();
                Log.d(LOG_TAG, "url cast is " + builtUri.toString());
                try {
                    url = new URL(builtUri.toString());

                } catch (MalformedURLException e) {
                    Log.e(LOG_TAG, "Problem building the URL ", e);
                }
                return url;

            case Constants.CODE_TRAILERS:
                baseUrl = Constants.BASE_MOVIE_URL;
                builtUri = Uri.parse(baseUrl).buildUpon()
                        .appendPath(movieID)
                        .appendEncodedPath(VIDEOS_URL)
                        .appendQueryParameter("api_key",Constants.API_KEY)
                        .build();

                try {
                    url = new URL(builtUri.toString());

                } catch (MalformedURLException e) {
                    Log.e(LOG_TAG, "Problem building the URL ", e);
                }
                return url;
            case Constants.CODE_SINGLE_MOVIE:
                baseUrl = Constants.BASE_MOVIE_URL;
                builtUri = Uri.parse(baseUrl).buildUpon()
                        .appendPath(movieID)
                        .appendQueryParameter("api_key",Constants.API_KEY)
                        .build();
                try {
                    url = new URL(builtUri.toString());

                } catch (MalformedURLException e) {
                    Log.e(LOG_TAG, "Problem building the URL ", e);
                }
                return url;
        }

        return null;
    }



    /**
     * Make an HTTP request to the given URL and return a String as the response.
     */
    public static String makeHttpRequest(URL url) throws IOException {
        Log.d(LOG_TAG, "http request...");
        String jsonResponse = "";

        // If the URL is null, then return early.
        if (url == null) {
            return jsonResponse;
        }

        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(10000 /* milliseconds */);
            urlConnection.setConnectTimeout(15000 /* milliseconds */);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // If the request was successful (response code 200),
            // then read the input stream and parse the response.
            if (urlConnection.getResponseCode() == 200) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = JsonUtils.readFromStream(inputStream);
            } else {
                Log.e(LOG_TAG, "Error response code: " + urlConnection.getResponseCode());
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem retrieving the earthquake JSON results.", e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                // Closing the input stream could throw an IOException, which is why
                // the makeHttpRequest(URL url) method signature specifies than an IOException
                // could be thrown.
                inputStream.close();
            }
        }
        return jsonResponse;
    }

}
