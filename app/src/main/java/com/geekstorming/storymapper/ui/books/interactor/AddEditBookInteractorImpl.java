package com.geekstorming.storymapper.ui.books.interactor;

import android.text.TextUtils;

import com.geekstorming.storymapper.data.pojo.Book;
import com.geekstorming.storymapper.data.repos.BookRepository;

/**
 * Interactor implementation for add or edit books
 *  @author Elena Guzman Blanco (Beelzenef) - 3d10Mundos
 */

public class AddEditBookInteractorImpl implements AddEditBookInteractor {

    OnBookAddingListener oBAL;

    public AddEditBookInteractorImpl(OnBookAddingListener listener) {
        this.oBAL = listener;
    }

    @Override
    public void validateBook(String title, String desc, String words) {

        // Check if the new book (params) is valid
        if (TextUtils.isEmpty(title))
            oBAL.onTitleEmpty();
        else if (TextUtils.isEmpty(desc))
            oBAL.onDescEmpty();
        else if (TextUtils.isEmpty(words))
            oBAL.onNWordsEmpty();
        else
            oBAL.onSuccess();
    }

    @Override
    public void addingBook(Book b) {
        BookRepository.getInstance().addBook(b);
    }

    @Override
    public void updateBook(Book b) {
        BookRepository.getInstance().editBook(b);
    }
}
