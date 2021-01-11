package com.example.uit_app;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.List;

import Model.CourseItem;

public class CourseItemAdapter extends RecyclerView.Adapter<CourseItemAdapter.MyViewHolder> {

    private List<CourseItem> courseItems;
    private Context context;
    private static String url = "http://149.28.24.98:9000/upload/course_image/";

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public ImageView courseImg;
        public TextView courseName, coursePrice;

        public MyViewHolder(View view) {
            super(view);
            courseImg = (ImageView) view.findViewById(R.id.item_course_img);
            courseName = (TextView) view.findViewById(R.id.item_course_label);
            coursePrice = (TextView) view.findViewById(R.id.item_course_price);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, CourseDetailsActivity.class);
                    intent.putExtra("courseItem", courseItems.get(getAdapterPosition()));
                    context.startActivity(intent);
                }
            });
        }
    }

    public CourseItemAdapter(Context context, List<CourseItem> courseItems) {
        this.context = context;
        this.courseItems = courseItems;
    }

    @NonNull
    @Override
    public CourseItemAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View courseView = inflater.inflate(R.layout.item_course, parent, false);
        return new MyViewHolder(courseView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        CourseItem courseItem = courseItems.get(position);

        TextView courseName = holder.courseName;
        TextView coursePrice = holder.coursePrice;
        ImageView courseImg = holder.courseImg;

        courseName.setText(courseItem.getTitle());

        if (courseItem.getPrice() == 0) {
            coursePrice.setText(R.string.free);
        } else {
            NumberFormat numberFormat = new DecimalFormat("#,###");
            coursePrice.setText(numberFormat.format(courseItem.getPrice()));
        }

        Picasso.get().load(url+courseItem.getUrl())
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
