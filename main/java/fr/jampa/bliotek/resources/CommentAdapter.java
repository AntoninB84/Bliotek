package fr.jampa.bliotek.resources;

import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.util.Date;
import java.util.List;

import fr.jampa.bliotek.R;
import fr.jampa.bliotek.classes.Comment;

/**
 * This class is made to display the comments in BookInfos Activity in a custom list
 *
 * Displayed :  userName,
 *              date (comment_timestamp to date),
 *              content (comment_content),
 *              rating (user's rating of this book)
 *
 *
 */

public class CommentAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private Activity activity;
    public List<Comment> comments;

    public CommentAdapter(RecyclerView recyclerView, List<Comment> comments, Activity activity){
        this.comments = comments;
        this.activity = activity;

        final LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(activity).inflate(R.layout.comment_list, parent, false);
        return new CommentViewHolder(view);
    }
    /**
     * Setting views contents
     * **/
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Comment comment = comments.get(position);
        CommentViewHolder commentViewHolder = (CommentViewHolder) holder;
        commentViewHolder.userName.setText(comment.getUser_name());
        commentViewHolder.rating.setRating(comment.getRating());

        // Format  (dd/M/yyyy hh:mm)
        DateFormat shortDateFormat = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT);
        Date date = new Date((long)comment.getComment_timestamp()*1000);

        commentViewHolder.date.setText(String.valueOf(shortDateFormat.format(date)));
        commentViewHolder.content.setText(comment.getComment_content());
    }

    @Override
    public int getItemCount() {
        return comments == null ? 0 : comments.size();
    }

    /**
     * Identifying Comment layouts
     * **/
    private class CommentViewHolder extends RecyclerView.ViewHolder {
        public TextView userName, date, content;
        public RatingBar rating;

        public CommentViewHolder(View view) {
            super(view);

            userName = (TextView) view.findViewById(R.id.comment_tv_name);
            date = (TextView) view.findViewById(R.id.comment_tv_date);
            content = (TextView) view.findViewById(R.id.comment_tv_content);
            rating = (RatingBar) view.findViewById(R.id.comment_rb_rating);
        }
    }
}
