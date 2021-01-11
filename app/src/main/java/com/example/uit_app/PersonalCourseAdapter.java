package com.example.uit_app;

import android.content.Context;
import android.content.Intent;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.List;

import Model.CourseItem;

public class PersonalCourseAdapter extends RecyclerView.Adapter<PersonalCourseAdapter.MyViewHolder> {

    private List<CourseItem> courseItems;
    private Context context;
    private static String url = "http://149.28.24.98:9000/upload/course_image/";

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public ImageView courseImg;
        public TextView courseName, coursePercent;
        public MyViewHolder(View view) {
            super(view);
            courseName = (TextView) view.findViewById(R.id.item_created_course_name);
            coursePercent = (TextView) view.findViewById(R.id.item_created_percent);
            courseImg = (ImageView) view.findViewById(R.id.item_created_course_img);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, LearnCourseActivity.class);
                    intent.putExtra("courseItem", courseItems.get(getAdapterPosition()));
                    context.startActivity(intent);
                }
            });
        }
    }

    public PersonalCourseAdapter(List<CourseItem> courseItems) {
        this.courseItems = courseItems;
    }

    @NonNull
    @Override
    public PersonalCourseAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View personalCourseView = inflater.inflate(R.layout.item_created_course, parent, false);
        return new MyViewHolder(personalCourseView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        CourseItem item = courseItems.get(position);

        TextView courseName = holder.courseName;
        TextView coursePercent = holder.coursePercent;
        ImageView courseImg = holder.courseImg;

        courseName.setText(item.getTitle());

        NumberFormat formatPrice = new DecimalFormat("#,###");
        NumberFormat formatPercent = new DecimalFormat("#%");

        coursePercent.setText(formatPercent.format(item.getPercent()));

        Picasso.get().load(url+item.getUrl())
                .placeholder(R.drawable.devices)
                .error(R.drawable.devices)
                .networkPolicy(NetworkPolicy.NO_CACHE)
                .memoryPolicy(MemoryPolicy.NO_CACHE)
                .into(courseImg);
    }

    @Override
    public int getItemCount() {
        return courseItems.size();
    }
}
