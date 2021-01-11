package com.example.uit_app;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import Model.RatingComment;
import de.hdodenhof.circleimageview.CircleImageView;

public class CourseCommentAdapter extends RecyclerView.Adapter<CourseCommentAdapter.MyViewHolder> {

    private ArrayList<RatingComment> ratingComments;
    private Context context;
    private String url = "http://149.28.24.98:9000/upload/user_image/";

    public CourseCommentAdapter(Context context, ArrayList<RatingComment> ratingComments) {
        this.context = context;
        this.ratingComments = ratingComments;
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        CircleImageView userImg;
        TextView userName, userComment;
        RatingBar userStar;
        public MyViewHolder(View view) {
            super(view);
            userImg = view.findViewById(R.id.item_course_comment_avatar);
            userName = view.findViewById(R.id.item_course_comment_name);
            userStar = view.findViewById(R.id.item_course_comment_rating);
            userComment = view.findViewById(R.id.item_course_comment_content);
        }
    }

    @NonNull
    @Override
    public CourseCommentAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View courseCommentView = inflater.inflate(R.layout.item_course_comment, parent, false);
        return new MyViewHolder(courseCommentView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        RatingComment ratingComment = ratingComments.get(position);

        TextView userName, userComment;
        RatingBar userStar;
        CircleImageView userImg;

        userName = holder.userName;
        userComment = holder.userComment;
        userImg = holder.userImg;
        userStar = holder.userStar;

        userName.setText(ratingComment.getUserName());
        userComment.setText(ratingComment.getCommentContent());
        userStar.setRating(ratingComment.getNumStar());

        Picasso.get().load(url + ratingComment.getAvatar())
                .placeholder(R.drawable.avatar)
                .error(R.drawable.avatar)
                .networkPolicy(NetworkPolicy.NO_CACHE)
                .memoryPolicy(MemoryPolicy.NO_CACHE)
                .into(userImg);
    }

    @Override
    public int getItemCount() {
        return ratingComments.size();
    }
}
