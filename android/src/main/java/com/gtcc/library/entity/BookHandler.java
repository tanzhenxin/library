package com.gtcc.library.entity;

import android.content.ContentProviderOperation;
import android.content.Context;

import com.alibaba.fastjson.JSONArray;
import com.gtcc.library.provider.LibraryContract;
import com.gtcc.library.util.LogUtils;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by TanZA on 10/29/2014.
 */
public class BookHandler extends JSONHandler {
    private static final String TAG = LogUtils.makeLogTag(BookHandler.class);

    private HashMap<String, Book> mBooks = new HashMap<String, Book>();

    public BookHandler(Context context) { super(context); }

    @Override
    public void makeContentProviderOperations(ArrayList<ContentProviderOperation> list) {
        for (Book book : mBooks.values()) {
            ContentProviderOperation.Builder builder = ContentProviderOperation.newInsert(LibraryContract.Books.CONTENT_URI);
            builder.withValue(LibraryContract.Books.BOOK_ID, book.getObjectId());
            builder.withValue(LibraryContract.Books.BOOK_TAG, book.getTag());
            builder.withValue(LibraryContract.Books.BOOK_TITLE, book.getTitle());
            builder.withValue(LibraryContract.Books.BOOK_AUTHOR, book.getAuthor());
            builder.withValue(LibraryContract.Books.BOOK_DESCRIPTION, book.getDescription());
            builder.withValue(LibraryContract.Books.BOOK_PUBLISHER, book.getPublisher());
            builder.withValue(LibraryContract.Books.BOOK_PUBLISH_DATE, book.getPublishedDate());
            builder.withValue(LibraryContract.Books.BOOK_PRICE, book.getPrice());
            builder.withValue(LibraryContract.Books.BOOK_ISBN, book.getISBN());
            builder.withValue(LibraryContract.Books.BOOK_PRINT_LENGTH, book.getPrintLength());
            builder.withValue(LibraryContract.Books.BOOK_IMAGE_URL, book.getImageUrl());
            builder.withValue(LibraryContract.Books.BOOK_CATEGORY, book.getCategory());

            list.add(builder.build());
        }
    }

    @Override
    public void process(JSONArray jsonArray) {
        for (int i = 0; i < jsonArray.size(); ++i) {
            Book book = jsonArray.getObject(i, Book.class);
            book.setCategory(book.getTag().substring(0, 1));
            mBooks.put(book.getObjectId(), book);
        }
    }
}
