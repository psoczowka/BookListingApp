package com.example.android.booklistingapp;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Locale;

import static android.R.attr.filter;
import static android.icu.lang.UCharacter.GraphemeClusterBreak.L;

/**
 * Created by kiwi on 2017-07-13.
 */

public class BookAdapter extends ArrayAdapter<Book> {

    public BookAdapter(Context context, ArrayList<Book> books) {

        super(context, 0, books);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View listView = convertView;
        if(listView == null) {
            listView = LayoutInflater.from(getContext()).inflate(
                    R.layout.list, parent, false);
        }
        // get current book object
        Book currentBook = getItem(position);

        // remove brackets from author's name
        TextView authorTextView = (TextView) listView.findViewById(R.id.author);
        String removeBrackets = new String(currentBook.getAuthor());
        if (removeBrackets.contains("[") || removeBrackets.contains("]")) {
            removeBrackets = removeBrackets.replaceAll(",", ", ").replaceAll("[\\[\"\\]]", "");

            authorTextView.setText(removeBrackets);
        } else {

            authorTextView.setText(currentBook.getAuthor());
        }

        // set title
        TextView titleTextView = (TextView) listView.findViewById(R.id.title);
        titleTextView.setText(currentBook.getTitle());

        // load image to ImageView using Picasso
        ImageView imageBookView = (ImageView) listView.findViewById(R.id.book_image);
        if (imageBookView != null) {
            Picasso.with(getContext()).load(currentBook.getImageUrl()).into(imageBookView);
        } else {
            Picasso.with(getContext()).load(R.drawable.placeholder).into(imageBookView);
        }

        return listView;
    }
}