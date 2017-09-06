package com.example.android.booklistingapp;

import android.content.AsyncTaskLoader;
import android.content.Context;

import java.util.List;

/**
 * Created by kiwi on 2017-07-14.
 */

public class BookLoader extends AsyncTaskLoader<List<Book>> {

    private String mUrl;

    public BookLoader(Context context, String url) {
        super(context);
        mUrl = url;
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    @Override
    public List<Book> loadInBackground() {
        // return early if url was empty
        if (mUrl == null) {
            return null;
        }

        // perform network request, parse the response, extract list of books
        List<Book> books = QueryUtils.fetchBookData(mUrl);
        return books;
    }
}