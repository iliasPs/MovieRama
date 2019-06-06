package com.example.workable_assignment_popmovies.Models;


import android.os.Parcel;
import android.os.Parcelable;

public class Similar implements Parcelable {

    private String imagePath;
    private int movieId;
    private String movieTitle;
    private String movieRating;
    private String releaseDate;
    private String moviePlot;


    public Similar(String imagePath, int movieId, String movieTitle, String movieRating, String releaseDate, String moviePlot) {
        this.imagePath = imagePath;
        this.movieId = movieId;
        this.movieTitle = movieTitle;
        this.movieRating = movieRating;
        this.releaseDate = releaseDate;
        this.moviePlot = moviePlot;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public int getMovieId() {
        return movieId;
    }

    public void setMovieId(int movieId) {
        this.movieId = movieId;
    }

    public String getMovieTitle() {
        return movieTitle;
    }

    public void setMovieTitle(String movieTitle) {
        this.movieTitle = movieTitle;
    }

    public String getMovieRating() {
        return movieRating;
    }

    public void setMovieRating(String movieRating) {
        this.movieRating = movieRating;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    public String getMoviePlot() {
        return moviePlot;
    }

    public void setMoviePlot(String moviePlot) {
        this.moviePlot = moviePlot;
    }

    protected Similar(Parcel in) {
        imagePath = in.readString();
        movieId = in.readInt();
        movieTitle = in.readString();
        movieRating = in.readString();
        releaseDate = in.readString();
        moviePlot = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(imagePath);
        dest.writeInt(movieId);
        dest.writeString(movieTitle);
        dest.writeString(movieRating);
        dest.writeString(releaseDate);
        dest.writeString(moviePlot);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<Similar> CREATOR = new Parcelable.Creator<Similar>() {
        @Override
        public Similar createFromParcel(Parcel in) {
            return new Similar(in);
        }

        @Override
        public Similar[] newArray(int size) {
            return new Similar[size];
        }
    };
}
