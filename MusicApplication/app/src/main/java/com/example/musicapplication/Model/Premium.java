package com.example.musicapplication.Model;

public class Premium {
    private int id;
    private String image;

    public Premium() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public Premium(int id, String image) {
        this.id = id;
        this.image = image;
    }
}
