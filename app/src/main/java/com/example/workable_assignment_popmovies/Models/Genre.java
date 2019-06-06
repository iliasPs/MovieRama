package com.example.workable_assignment_popmovies.Models;

public class Genre {

    private String genreName;
    private int genreNumber;

    public Genre(String genreName, int genreNumber) {
        this.genreName = genreName;
        this.genreNumber = genreNumber;
    }

    public String getGenreName() {
        return genreName;
    }

    public void setGenreName(String genreName) {
        this.genreName = genreName;
    }

    public int getGenreNumber() {
        return genreNumber;
    }

    public void setGenreNumber(int genreNumber) {
        this.genreNumber = genreNumber;
    }
}
