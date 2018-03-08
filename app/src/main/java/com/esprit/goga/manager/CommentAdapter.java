package com.esprit.goga.manager;

/**
 * Created by khmai on 06/03/2018.
 */

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.esprit.goga.R;
import com.esprit.goga.bean.Comments;

import java.util.List;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.MyViewHolder> {



    private List<Comments> commentsList;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView userName, commentText, commentDate;
        public ImageView userImage;

        public MyViewHolder(View view) {
            super(view);
            userName = (TextView) view.findViewById(R.id.userName);
            commentText = (TextView) view.findViewById(R.id.commentText);
            commentDate = (TextView) view.findViewById(R.id.commentDate);
            userImage = (ImageView) view.findViewById(R.id.userImage);
        }
    }


    public CommentAdapter(List<Comments> commentsList) {
        this.commentsList = commentsList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.comment_list_row, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Comments comment = commentsList.get(position);
        holder.userName.setText(comment.getText());
        holder.commentDate.setText(comment.getCreatedDate());
        holder.commentText.setText(comment.getText());
    }

    @Override
    public int getItemCount() {
        return commentsList.size();
    }
}
