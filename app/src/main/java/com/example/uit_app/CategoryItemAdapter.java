package com.example.uit_app;

import android.content.Context;
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

import java.util.List;

import Model.CategoryItem;
import Model.CourseItem;

public class CategoryItemAdapter extends RecyclerView.Adapter<CategoryItemAdapter.MyViewHolder> {

    private List<CategoryItem> categoryItems;
    private Context context;
    private String url = "http://149.28.24.98:9000/upload/category/";

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        public ImageView categoryImg;
        public TextView categoryName;
        public MyViewHolder(View view) {
            super(view);
            categoryImg = (ImageView) view.findViewById(R.id.item_categories_img);
            categoryName = (TextView) view.findViewById(R.id.item_categories_name);
        }
    }

    public CategoryItemAdapter(Context context, List<CategoryItem> categoryItems) {
        this.context = context;
        this.categoryItems = categoryItems;
    }

    @NonNull
    @Override
    public CategoryItemAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        LayoutInflater layoutInflater = LayoutInflater.from(context);

        View categoryView = layoutInflater.inflate(R.layout.item_categories, parent, false);
        return new MyViewHolder(categoryView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        CategoryItem categoryItem = categoryItems.get(position);

        TextView catName = holder.categoryName;
        ImageView catImg = holder.categoryImg;

        catName.setText(categoryItem.getName());
        Picasso.get().load(url+categoryItem.getImage())
                .placeholder(R.drawable.devices)
                .error(R.drawable.devices)
                .networkPolicy(NetworkPolicy.NO_CACHE)
                .memoryPolicy(MemoryPolicy.NO_CACHE)
                .into(catImg);
    }

    @Override
    public int getItemCount() {
        return categoryItems.size();
    }
}
