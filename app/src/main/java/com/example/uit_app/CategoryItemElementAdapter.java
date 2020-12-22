package com.example.uit_app;

import android.content.Context;
import android.content.Intent;
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

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.List;

import Model.CourseItem;

public class CategoryItemElementAdapter extends RecyclerView.Adapter<CategoryItemElementAdapter.MyViewHolder> {
    private Context context;
    private List<CourseItem> courseItems;
    private static String url = "http://149.28.24.98:9000/upload/course_image/";

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView catItemName, catItemPrice;
        ImageView catItemImg;
        public MyViewHolder(View view) {
            super(view);
            catItemName = view.findViewById(R.id.item_category_name);
            catItemPrice = view.findViewById(R.id.item_category_price);
            catItemImg = view.findViewById(R.id.item_category_img);

            view.setOnClickListener(v -> {
                Intent intent = new Intent(context, CourseDetailsActivity.class);
                intent.putExtra("courseItem", courseItems.get(getAdapterPosition()));
                context.startActivity(intent);
            });
        }
    }

    public CategoryItemElementAdapter(Context context, List<CourseItem> courseItems) {
        this.context = context;
        this.courseItems = courseItems;
    }

    @NonNull
    @Override
    public CategoryItemElementAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View categoryElementView = inflater.inflate(R.layout.item_category_element, parent, false);
        return new MyViewHolder(categoryElementView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        CourseItem courseItem = courseItems.get(position);

        TextView catName, catPrice;
        ImageView catImg;
        NumberFormat numberFormat = new DecimalFormat("#,###");

        catName = holder.catItemName;
        catPrice = holder.catItemPrice;
        catImg = holder.catItemImg;

        catName.setText(courseItem.getTitle());
        double price = (double) courseItem.getPrice();
        String formattedPrice = numberFormat.format(price);
        catPrice.setText(formattedPrice);

        Picasso.get().load(url+courseItem.getUrl())
                .placeholder(R.drawable.devices)
                .error(R.drawable.devices)
                .networkPolicy(NetworkPolicy.NO_CACHE)
                .memoryPolicy(MemoryPolicy.NO_CACHE)
                .into(catImg);
    }

    @Override
    public int getItemCount() {
        return courseItems.size();
    }
}
