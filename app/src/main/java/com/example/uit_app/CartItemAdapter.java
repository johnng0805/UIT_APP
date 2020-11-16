package com.example.uit_app;

import android.content.Context;
import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.List;

import Model.CourseItem;

public class CartItemAdapter extends RecyclerView.Adapter<CartItemAdapter.MyViewHolder> {

    private Context context;
    private List<CourseItem> courseItems;
    private static String url = "http://149.28.24.98:9000/upload/course_image/";

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView courseName, coursePrice;
        ImageView courseImg;
        Button removeButton;
        public MyViewHolder(View view) {
            super(view);
            courseName = view.findViewById(R.id.item_cart_name);
            coursePrice = view.findViewById(R.id.item_cart_price);
            courseImg = view.findViewById(R.id.item_cart_img);
        }
    }

    public CartItemAdapter(Context context, List<CourseItem> courseItems) {
        this.context = context;
        this.courseItems = courseItems;
    }

    @NonNull
    @Override
    public CartItemAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View cartView = inflater.inflate(R.layout.item_cart, parent, false);
        return new MyViewHolder(cartView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        CourseItem courseItem = courseItems.get(position);

        TextView courseName, coursePrice;
        ImageView courseImg;
        Button removeButton;

        courseName = holder.courseName;
        coursePrice = holder.coursePrice;
        courseImg = holder.courseImg;
        removeButton = holder.removeButton;

        courseName.setText(courseItem.getTitle());
        coursePrice.setText((int)courseItem.getPrice());

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
