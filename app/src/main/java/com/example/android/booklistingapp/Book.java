package com.example.android.booklistingapp;

/**
 * Created by kiwi on 2017-07-13.
 */

public class Book {

    private String mAuthor;
    private String mTitle;
    private String mImage;
    private String mInfo;

    // Book constructor
    public Book(String author, String title, String imageUrl, String infoLink) {
        mTitle = title;
        mAuthor = author;
        mImage = imageUrl;
        mInfo = infoLink;
    }

    public String getAuthor() {
        return mAuthor;
    }

    public String getTitle() {
        return mTitle;
    }

    public String getImageUrl() {
        return mImage;
    }

    public String getLink() {
        return mInfo;
    }
}