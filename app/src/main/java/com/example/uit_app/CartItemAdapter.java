package com.example.uit_app;

import android.content.Context;
import android.content.SharedPreferences;
import android.icu.number.NumberFormatter;
import android.media.Image;
import android.preference.PreferenceManager;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.NumberFormat;
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
            removeButton = view.findViewById(R.id.item_cart_remove);
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

        NumberFormat numberFormat = new DecimalFormat("#,###");

        courseName = holder.courseName;
        coursePrice = holder.coursePrice;
        courseImg = holder.courseImg;
        removeButton = holder.removeButton;

        courseName.setText(courseItem.getTitle());
        double price = (double)courseItem.getPrice();
        String formattedPrice = numberFormat.format(price);
        coursePrice.setText(formattedPrice);

        Picasso.get().load(url+courseItem.getUrl())
                .placeholder(R.drawable.devices)
                .error(R.drawable.devices)
                .networkPolicy(NetworkPolicy.NO_CACHE)
                .memoryPolicy(MemoryPolicy.NO_CACHE)
                .into(courseImg);

        removeButton.setVisibility(View.VISIBLE);

        removeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Remove(position);
            }
        });
    }

    private void Remove(int position) {
        courseItems.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, courseItems.size());

        JSONArray cartArray = new JSONArray();
        for (int i = 0; i < courseItems.size(); i++) {

            JSONObject jo = new JSONObject();
            try {
                jo.put("courseImage", courseItems.get(i).getUrl());
                jo.put("author", courseItems.get(i).getAuthor());
                jo.put("courseID", courseItems.get(i).getID());
                jo.put("title", courseItems.get(i).getTitle());
                jo.put("price", courseItems.get(i).getPrice());
                jo.put("discount", courseItems.get(i).getDiscount());
            } catch (JSONException jx) {
                jx.printStackTrace();
            }

            cartArray.put(jo);
        }

        SharedPreferences sharedPreferences;
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("cartArray", cartArray.toString());
        editor.apply();
    }

    @Override
    public int getItemCount() {
        return courseItems.size();
    }
}
