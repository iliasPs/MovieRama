package com.example.workable_assignment_popmovies.Models;


import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;

import java.io.Serializable;
import java.util.ArrayList;

@Entity(tableName = "moviesTable")
public class Movie implements Parcelable {


    @PrimaryKey(autoGenerate = true)
    private int movieId;
    @Expose
    private String title;
    @Expose
    private String release_date;
    @Expose
    private String poster_path;
    @Expose
    private String vote_average;
    @Expose
    private String plot;
    @Expose
    private int id;
    @Expose
    private ArrayList<String> genre_ids;


    @Ignore
    public Movie(String title, String release_date, String poster_path, String vote_average, String plot, int id, ArrayList<String> genre_ids) {

        this.title = title;
        this.release_date = release_date;
        this.poster_path = poster_path;
        this.vote_average = vote_average;
        this.plot = plot;
        this.id = id;
        this.genre_ids = genre_ids;
    }


    public Movie(int movieId, String title, String release_date, String poster_path, String vote_average, String plot, int id, ArrayList<String> genre_ids) {

        this.movieId = movieId;
        this.title = title;
        this.release_date = release_date;
        this.poster_path = poster_path;
        this.vote_average = vote_average;
        this.plot = plot;
        this.id = id;
        this.genre_ids = genre_ids;

    }

    public int getMovieId() {
        return movieId;
    }

    public void setMovieId(int movieId) {
        this.movieId = movieId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getRelease_date() {
        return release_date;
    }

    public void setRelease_date(String release_date) {
        this.release_date = release_date;
    }

    public String getPoster_path() {
        return poster_path;
    }

    public void setPoster_path(String poster_path) {
        this.poster_path = poster_path;
    }

    public String getVote_average() {
        return vote_average;
    }

    public void setVote_average(String vote_average) {
        this.vote_average = vote_average;
    }

    public String getPlot() {
        return plot;
    }

    public void setPlot(String plot) {
        this.plot = plot;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public ArrayList getGenre_ids() {
        return genre_ids;
    }

    public void setGenre_ids(ArrayList genre_ids) {
        this.genre_ids = genre_ids;
    }


    protected Movie(Parcel in) {
        title = in.readString();
        release_date = in.readString();
        poster_path = in.readString();
        vote_average = in.readString();
        plot = in.readString();
        id = in.readInt();
        if (in.readByte() == 0x01) {
            genre_ids = new ArrayList<String>();
            in.readList(genre_ids, String.class.getClassLoader());
        } else {
            genre_ids = null;
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeString(release_date);
        dest.writeString(poster_path);
        dest.writeString(vote_average);
        dest.writeString(plot);
        dest.writeInt(id);
        if (genre_ids == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeList(genre_ids);
        }
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<Movie> CREATOR = new Parcelable.Creator<Movie>() {
        @Override
        public Movie createFromParcel(Parcel in) {
            return new Movie(in);
        }

        @Override
        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };
}