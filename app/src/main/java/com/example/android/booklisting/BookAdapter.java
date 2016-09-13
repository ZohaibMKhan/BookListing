package com.example.android.booklisting;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Zohaib on 12/09/2016.
 */
public class BookAdapter extends ArrayAdapter<Book> {


    public BookAdapter(Context context, List<Book> objects) {
        super(context, 0, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.book_list_layout, parent, false);
        }

        Book currentBook = getItem(position);

        ViewHolder holder = new ViewHolder();

        holder.bookTitle = (TextView) listItemView.findViewById(R.id.book_title);
        holder.author = (TextView) listItemView.findViewById(R.id.book_author);
        listItemView.setTag(holder);

        holder.bookTitle.setText(currentBook.getBookName());
        holder.author.setText(currentBook.getAuthor());


        return listItemView;
    }

    static class ViewHolder {
        TextView bookTitle;
        TextView author;
    }
}
