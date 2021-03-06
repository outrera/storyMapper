package com.geekstorming.storymapper.ui.books.fragments;

import android.app.Activity;
import android.app.ProgressDialog;
import android.support.v4.app.ListFragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.Toolbar;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.geekstorming.storymapper.R;
import com.geekstorming.storymapper.adapters.BooksAdapter;
import com.geekstorming.storymapper.base.BasePresenter;
import com.geekstorming.storymapper.data.pojo.Book;
import com.geekstorming.storymapper.data.pojo.BookComponents;
import com.geekstorming.storymapper.data.pojo.User;
import com.geekstorming.storymapper.ui.books.contracts.ListBookContract;
import com.geekstorming.storymapper.utils.CommonUIUtils;

import java.util.List;

/**
 * Book list fragment, listing and selecting books for removal
 *  @author Elena Guzman Blanco (Beelzenef) - 3d10Mundos
 */

public class BookList_Fragment extends ListFragment implements ListBookContract.View {

    public static final String TAG = "listBook";
    ListBookContract.Presenter presenter;
    private ListBookListener callback;

    private ProgressDialog progressDialog;

    BooksAdapter adapter;

    private static User loggedUser;

    public static BookList_Fragment newInstance(Bundle args) {

        BookList_Fragment fragment = new BookList_Fragment();

        if (args != null) {
            fragment.setArguments(args);
            loggedUser = (User) args.getParcelable(User.TAG);
        }

        return fragment;
    }

    public interface ListBookListener {
        void addNewBook(Bundle b);
        void viewSelectedBook(Bundle b);
    }

    @Override
    public void setPresenter(BasePresenter presenter) {
        this.presenter = (ListBookContract.Presenter) presenter;
    }

    @Override
    public void showBooks(final List<BookComponents> bookList) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                adapter.clear();
                adapter.addAll(bookList);
            }
        });
    }

    // Overriding ListFragment code

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.adapter = new BooksAdapter(getActivity());
        setRetainInstance(true);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_book_list, container, false);

        FloatingActionButton fab_AddNewBook = (FloatingActionButton) rootView.findViewById(R.id.fab_Books);

        fab_AddNewBook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callback.addNewBook(new Bundle());
            }
        });
        presenter.loadBooks(loggedUser);

        return rootView;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {
            callback = (ListBookListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(getActivity().getLocalClassName() + " must be implemented");
        }
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setListAdapter(adapter);
        ListView listV_listBooks = (ListView) getListView().findViewById(android.R.id.list);
        listV_listBooks.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Bundle b = new Bundle();
                b.putParcelable(Book.TAG, (Book)parent.getItemAtPosition(position));
                callback.viewSelectedBook(b);
            }
        });

        registerForContextMenu(getListView());

    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        getActivity().getMenuInflater().inflate(R.menu.contextmenu_delete_book, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {

        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();

        switch (item.getItemId())
        {
            case R.id.action_delete_book:
                Bundle bundle = new Bundle();
                bundle.putParcelable(Book.TAG, adapter.getItem(info.position));
                bundle.putString(CommonUIUtils.MSG, "¿Quieres eliminar el libro \'" +
                        adapter.getItem(info.position).getBookTitle() + "\' ?");
                bundle.putString(CommonUIUtils.TITLE, "Eliminar libro");

                CommonUIUtils.showDeleteBookDialog(bundle, getActivity(), presenter).show();
                break;
        }

        return super.onContextItemSelected(item);
    }

    /*
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_activity_projects, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId())
        {
            case R.id.action_settings:
                startActivity(new Intent(getActivity(), SettingsActivity.class));
                break;
        }

        return super.onOptionsItemSelected(item);
    }
    */

    @Override
    public void onDatabaseError(Error error) {
        Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_LONG).show();
    }

    @Override
    public void onDatabaseError(Exception exception) {
        Toast.makeText(getContext(), exception.getMessage(), Toast.LENGTH_LONG).show();
    }

    @Override
    public void showProgressDialog(String message) {
        progressDialog = CommonUIUtils.makeProgressDialog(getContext(), message);
        progressDialog.show();
    }

    @Override
    public void dismissProgressDialog() {
        if (progressDialog != null) {
            progressDialog.dismiss();
            progressDialog = null;
        }
    }

}