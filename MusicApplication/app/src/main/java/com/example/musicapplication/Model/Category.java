package com.example.musicapplication.Model;

import java.util.ArrayList;

public class Category {

    private int id;
    private String name;
    private ArrayList<Albums> albums;

    public Category(String name, ArrayList<Albums> albums) {
        this.name = name;
        this.albums = albums;
    }


    public Category(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public Category() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<Albums> getAlbums() {
        return albums;
    }

    public void setAlbums(ArrayList<Albums> albums) {
        this.albums = albums;
    }
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}