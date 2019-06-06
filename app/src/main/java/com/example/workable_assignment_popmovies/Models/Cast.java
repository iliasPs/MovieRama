package com.example.workable_assignment_popmovies.Models;

import java.util.ArrayList;

public class Cast {

    private String actors;
    private String director;

    public Cast(String actors, String director) {
        this.actors = actors;
        this.director = director;
    }

    public String getActors() {
        return actors;
    }

    public void setActors(String actors) {
        this.actors = actors;
    }

    public String getDirector() {
        return director;
    }

    public void setDirector(String director) {
        this.director = director;
    }
}
