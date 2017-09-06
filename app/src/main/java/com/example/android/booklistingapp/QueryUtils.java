package com.example.android.booklistingapp;

import android.text.TextUtils;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by kiwi on 2017-07-14.
 */

public class QueryUtils {

    private static final String LOG_TAG = QueryUtils.class.getSimpleName();

    public static List<Book> fetchBookData(String requestUrl) {
        // create url object
        URL url = createUrl(requestUrl);

        // perform HTTP request to the URL and receive JSON response
        String jsonResponse = null;
        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem with HTTP request");
        }
        // extract relevant fields from JSON response to list
        List<Book> books = extractItemFromJson(jsonResponse);

        return books;
    }

    private static URL createUrl(String stringUrl) {
        URL url = null;
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "error with creating url", e);
        }
        return url;
    }

    private static String makeHttpRequest(URL url) throws IOException {
        String jsonResponse = "";

        // If the URL is null, then return early.
        if (url == null) {
            return jsonResponse;
        }

        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(10000);
            urlConnection.setConnectTimeout(15000);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            if (urlConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
                Log.e(LOG_TAG, "Error response code: " + urlConnection.getResponseCode());
            }

        } catch (IOException e) {
            Log.e(LOG_TAG, "problem retrieving json result", e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                inputStream.close();
            }
        }
        return jsonResponse;
    }

    // set InputStreamReader and BufferReader
    private static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line != null) {
                output.append(line);
                line = reader.readLine();
            }
        }
        return output.toString();
    }

    private static List<Book> extractItemFromJson (String bookJSON) {
        // return early if the JSON string is empty or null
        if (TextUtils.isEmpty(bookJSON)) {
            return null;
        }

        // create empty ArrayList
        List<Book> books = new ArrayList<>();

        // try to parse JSON
        try {
            // create variables before parsing
            String authors;
            String title;
            String imageUrl = "";
            String infoLink = "";

            JSONObject baseJsonResponse;
            JSONArray bookArray = null;

            // create JSONObject from JSON response
            baseJsonResponse = new JSONObject(bookJSON);

            if (baseJsonResponse.has("items")) {
                // extract JSONArray associated with the key "item"
                bookArray = baseJsonResponse.getJSONArray("items");
            }

            // loop for creating Book objects in array
            for (int i = 0; i < bookArray.length(); i++) {

                // get book from current position from book list
                JSONObject currentBook = bookArray.getJSONObject(i);

                // get next object called "volumeInfo"
                JSONObject volumeInfo = currentBook.getJSONObject("volumeInfo");

                // extract value called "authors"
                if (volumeInfo.has("authors")) {
                    authors = volumeInfo.getString("authors");
                } else {
                    authors = "Author N/A";
                }

                // extract value called "title"
                if (volumeInfo.has("title")) {
                    title = volumeInfo.getString("title");
                } else {
                    title = "Title Unknown";
                }

                // extract value called "infoLink"
                if (volumeInfo.has("infoLink")) {
                    infoLink = volumeInfo.getString("infoLink");
                }

                // get new object called "imageLinks"
                // extract value called "smallThumbnail"
                JSONObject imageLinks = volumeInfo.getJSONObject("imageLinks");
                if (imageLinks.has("smallThumbnail")) {
                    imageUrl = imageLinks.getString("smallThumbnail");
                }

                // create new Book object with data from JSON response
                Book book = new Book(authors, title, imageUrl, infoLink);
                // add new Book object to list of books
                books.add(book);

            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return books;
    }
}