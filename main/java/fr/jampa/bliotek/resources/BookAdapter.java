package fr.jampa.bliotek.resources;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import fr.jampa.bliotek.AddExemplary;
import fr.jampa.bliotek.BookInfos;
import fr.jampa.bliotek.R;
import fr.jampa.bliotek.classes.Book;

/**
 * This class is made to display the books in various BookList Activities in a custom list
 *
 * Displayed :  book_title,
 *              authors_name,
 *              editor_name,
 *              language
 *
 */

public class BookAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final int VIEW_TYPE_ITEM = 0;
    private final int VIEW_TYPE_LOADING = 1;
    private final int INVISIBLE_BEFORE_LOAD = 3;

    private boolean isLoading;
    private Activity activity;
    public List<Book> books;
    private int lastVisibleItem;

    public BookAdapter(RecyclerView recyclerView, List<Book> books, Activity activity) {
        this.books = books;
        this.activity = activity;

        final LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                //Get last visible item position to know when to load more books
                lastVisibleItem = linearLayoutManager.findLastVisibleItemPosition();

                // If less than FINAL invisible items remaining, loadMoreBooks
                if (!isLoading && lastVisibleItem == books.size() -INVISIBLE_BEFORE_LOAD) {
                    if (onLoadMoreListener != null) {
                        onLoadMoreListener.onLoadMore();
                    }
                    isLoading = true;
                }
            }
        });
    }
    @Override
    public int getItemViewType(int position) {
        return books.get(position) == null ? VIEW_TYPE_LOADING : VIEW_TYPE_ITEM;
    }

    /**
     * Inflate BookList OR loadingItem
     * **/
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_ITEM) {
            View view = LayoutInflater.from(activity).inflate(R.layout.book_list, parent, false);
            return new BookViewHolder(view);
        } else if (viewType == VIEW_TYPE_LOADING) {
            View view = LayoutInflater.from(activity).inflate(R.layout.item_loading, parent, false);
            return new LoadingViewHolder(view);
        }
        return null;
    }

    /**
     * Setting layouts content and listeners
     */
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof BookViewHolder) {
            Book book = books.get(position);
            BookViewHolder bookViewHolder = (BookViewHolder) holder;
            bookViewHolder.bookTVid.setText(String.valueOf(book.getBook_id()));
            bookViewHolder.title.setText(book.getTitle());
            bookViewHolder.authors.setText(book.getAuthor());
            bookViewHolder.editor.setText(book.getEditor());
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    // Going to bookInfos activity with selected book_id
                    Intent goNext = new Intent(activity.getApplicationContext(), BookInfos.class);
                    goNext.putExtra("bookID",  String.valueOf(book.getBook_id()));
                    activity.startActivity(goNext);
                }
            });
        } else if (holder instanceof LoadingViewHolder) {
            LoadingViewHolder loadingViewHolder = (LoadingViewHolder) holder;
            loadingViewHolder.progressBar.setIndeterminate(true);
        }
    }

    @Override
    public int getItemCount() {
        return books == null ? 0 : books.size();
    }

    public void setLoaded() {
        isLoading = false;
    }

    private OnLoadMoreListener onLoadMoreListener;
    public void setOnLoadMoreListener(OnLoadMoreListener mOnLoadMoreListener) {
        this.onLoadMoreListener = mOnLoadMoreListener;
    }

    // "Loading item" ViewHolder
    private class LoadingViewHolder extends RecyclerView.ViewHolder {
        public ProgressBar progressBar;

        public LoadingViewHolder(View view) {
            super(view);
            progressBar = (ProgressBar) view.findViewById(R.id.bookList_pb_loading);
        }
    }

    // "Normal item" ViewHolder
    private class BookViewHolder extends RecyclerView.ViewHolder {
        public TextView bookTVid, title, authors, editor;

        public BookViewHolder(View view) {
            super(view);

            bookTVid = (TextView) view.findViewById(R.id.bookList_tv_bookID);
            title = (TextView) view.findViewById(R.id.bookList_tv_bookTitle);
            authors = (TextView) view.findViewById(R.id.bookList_tv_authors);
            editor = (TextView) view.findViewById(R.id.bookList_tv_editor);
        }
    }
}
