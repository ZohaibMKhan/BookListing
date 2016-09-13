package com.example.android.booklisting;

/**
 * Created by Zohaib on 12/09/2016.
 */
public class Book {

    private String bookName, author;

    public Book(String bookName, String author) {
        this.bookName = bookName;
        this.author = author;
    }

    public String getBookName() {
        return bookName;
    }

    public String getAuthor() {
        return author;
    }
}
