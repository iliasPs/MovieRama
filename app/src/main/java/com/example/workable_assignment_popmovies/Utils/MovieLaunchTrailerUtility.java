package com.example.workable_assignment_popmovies.Utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;

//helper for the trailers in movie details activity
public class MovieLaunchTrailerUtility {

    public final static String THUMΒ_PATH = "http://image.tmdb.org/t/p/w500";
    private static final String BASE_URL_YOUTUBE = "https://www.youtube.com/watch?v=";

    public static void launchTrailerVideoInYoutubeApp(@NonNull Context context, String videoID) {
        Intent launchTrailerVideoInYoutube = new Intent(Intent.ACTION_VIEW, Uri.parse(("vnd.youtube://" + videoID)));
        context.startActivity(launchTrailerVideoInYoutube);
    }

    public static void launchTrailerVideoInYoutubeBrowser(@NonNull Context context, String videoID) {
        Intent launchTrailerVideoInYoutube = new Intent(Intent.ACTION_VIEW, Uri.parse((BASE_URL_YOUTUBE + videoID)));
        context.startActivity(launchTrailerVideoInYoutube);
    }

}
