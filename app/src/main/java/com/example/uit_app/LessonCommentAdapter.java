package com.example.uit_app;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.List;

import Model.LessonComment;
import de.hdodenhof.circleimageview.CircleImageView;

public class LessonCommentAdapter extends RecyclerView.Adapter<LessonCommentAdapter.MyViewHolder> {

    private List<LessonComment> lessonComments;
    private Context context;
    private String url = "http://149.28.24.98:9000/upload/user_image/";

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        public TextView commentName, commentDate, commentContent;
        public CircleImageView commentAvatar;

        public MyViewHolder(View view) {
            super(view);
            commentName = view.findViewById(R.id.item_lesson_comment_name);
            commentDate = view.findViewById(R.id.item_lesson_comment_date);
            commentContent = view.findViewById(R.id.item_lesson_comment_content);
            commentAvatar = view.findViewById(R.id.item_lesson_comment_avatar);
        }
    }

    public LessonCommentAdapter(Context context, List<LessonComment> lessonComments) {
        this.context = context;
        this.lessonComments = lessonComments;
    }

    @NonNull
    @Override
    public LessonCommentAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        this.context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View lessonCommentView = inflater.inflate(R.layout.item_lesson_comment, parent, false);
        return new MyViewHolder(lessonCommentView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        LessonComment lessonComment = lessonComments.get(position);

        TextView commentName, commentDate, commentContent;
        CircleImageView commentAvatar;

        commentName = holder.commentName;
        commentDate = holder.commentDate;
        commentContent = holder.commentContent;
        commentAvatar = holder.commentAvatar;

        commentName.setText(lessonComment.getIdUser().getHoten());
        commentDate.setText(lessonComment.getCreatedAt());
        commentContent.setText(lessonComment.getContent());

        Picasso.get().load(url + lessonComment.getIdUser().getAva())
                .placeholder(R.drawable.devices)
                .error(R.drawable.devices)
                .networkPolicy(NetworkPolicy.NO_CACHE)
                .memoryPolicy(MemoryPolicy.NO_CACHE)
                .into(commentAvatar);
    }

    @Override
    public int getItemCount() {
        return lessonComments.size();
    }
}
