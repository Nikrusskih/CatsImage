package com.example.cats;

public class CatImage {

    private String id;
    private String url;
    private  int width;
    private int height;

    public CatImage(String id, String url, int width, int height) {
        this.id = id;
        this.url = url;
        this.width = width;
        this.height = height;
    }

    public String getUrl() {
        return url;
    }

    public String getId() {
        return id;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    @Override
    public String toString() {
        return "CatImage{" +
                "id='" + id + '\'' +
                ", url='" + url + '\'' +
                ", width=" + width +
                ", height=" + height +
                '}';
    }
}
