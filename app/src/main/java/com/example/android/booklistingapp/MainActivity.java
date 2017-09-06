package com.example.android.booklistingapp;

import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static android.R.attr.onClick;
import static android.media.CamcorderProfile.get;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<Book>> {

    private BookAdapter mBookAdapter;
    private ProgressBar mProgressBar;
    private TextView mEmptyTextView;
    private EditText mEditText;
    private ImageButton mSearchButton;

    private String search_url = "https://www.googleapis.com/books/v1/volumes?q=";
    public String startURL = "https://www.googleapis.com/books/v1/volumes?q=java&maxResults=30";

    private static final int BOOK_LOADER_ID = 1;

    @Override
    public Loader<List<Book>> onCreateLoader(int id, Bundle bundle) {

        return new BookLoader(this, startURL);
    }

    @Override
    public void onLoadFinished(Loader<List<Book>> loader, List<Book> books) {
        // clear adapter form prevois data
        mBookAdapter.clear();

        // if valid list of books was found then add adapter's data
        if (books != null && !books.isEmpty()) {
            mBookAdapter.addAll(books);
        }

        mProgressBar.setVisibility(View.GONE);
    }

    @Override
    public void onLoaderReset(Loader<List<Book>> loader) {

        mBookAdapter.clear();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getLoaderManager().initLoader(BOOK_LOADER_ID, null, this);

        // find ListView in the layout
        ListView booksListView = (ListView) findViewById(R.id.books_list);

        // create empty textView for displaying 'connection error' and 'no books found' messages
        mEmptyTextView = (TextView) findViewById(R.id.empty_view);
        booksListView.setEmptyView(mEmptyTextView);

        // create Loading indicator
        mProgressBar = (ProgressBar) findViewById(R.id.loading_spinner);
        mProgressBar.setVisibility(View.VISIBLE);

        // create new adapter to fill list with books
        mBookAdapter = new BookAdapter(this, new ArrayList<Book>());
        booksListView.setAdapter(mBookAdapter);

        // find EditText
        mEditText = (EditText)findViewById(R.id.search_view);

        // Get a reference to the ConnectivityManager to check state of network connectivity
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        // Get details on the currently active default data network
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        if (networkInfo != null && networkInfo.isConnected()) {
            // is connected
            if (mBookAdapter.isEmpty()) {

                mEmptyTextView.setVisibility(View.GONE);
                mEmptyTextView.setText(R.string.no_books);
            }
        } else {

            mEmptyTextView.setText(R.string.no_internet);
        }

        // onClickListener for search button
        mSearchButton = (ImageButton)findViewById(R.id.search_button);
        mSearchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // perform search
                String userQuery = mEditText.getText().toString();
                startURL = search_url + userQuery;
                getLoaderManager().restartLoader(BOOK_LOADER_ID, null, MainActivity.this);

                // hide virtual keyboard on pressing the button
                InputMethodManager inputManager = (InputMethodManager)
                        getSystemService(Context.INPUT_METHOD_SERVICE);
                inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS);

                // Get a reference to the ConnectivityManager to check state of network connectivity
                ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                // Get details on the currently active default data network
                NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
                if (networkInfo != null && networkInfo.isConnected()) {
                    // if connection was found fetch data
                    LoaderManager loaderManager = getLoaderManager();
                    loaderManager.initLoader(BOOK_LOADER_ID, null, MainActivity.this);

                    if (mBookAdapter.isEmpty()) {

                        mEmptyTextView.setText(R.string.no_books);
                    }
                } else {
                    // otherwise display error in empty textView and hide loading spinner
                    mProgressBar.setVisibility(View.GONE);
                    mEmptyTextView.setText(R.string.no_internet);
                }

            }
        });

        // onclick item for showing more info about book
        booksListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Book clickArea = mBookAdapter.getItem(position);

                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(clickArea.getLink()));
                startActivity(i);

            }
        });
    }

}