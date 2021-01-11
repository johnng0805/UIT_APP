package com.example.uit_app;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.stripe.android.model.Card;

import java.util.List;

import Interface.OnItemClick;
import Model.LessonItem;

public class LessonItemAdapter extends RecyclerView.Adapter<LessonItemAdapter.MyViewHolder> {

    private List<LessonItem> lessonItems;
    private Context context;
    private OnItemClick onItemClick;

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView lessonOrder, lessonName;
        public CardView lessonBackground;

        public MyViewHolder(View view) {
            super(view);
            lessonOrder = view.findViewById(R.id.item_lesson_number);
            lessonName = view.findViewById(R.id.item_lesson_name);
            lessonBackground = view.findViewById(R.id.item_lesson_background);
        }
    }

    public LessonItemAdapter(Context context, List<LessonItem> lessonItems, OnItemClick itemClick) {
        this.context = context;
        this.lessonItems = lessonItems;
        this.onItemClick = itemClick;
    }

    @NonNull
    @Override
    public LessonItemAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View lessonItemView = inflater.inflate(R.layout.item_lesson, parent, false);
        return new MyViewHolder(lessonItemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        LessonItem lessonItem = lessonItems.get(position);

        TextView lessonOrder = holder.lessonOrder;
        TextView lessonName = holder.lessonName;
        CardView lessonBackground = holder.lessonBackground;

        lessonOrder.setText(String.valueOf(lessonItem.getOrder()));
        lessonName.setText(lessonItem.getTitle());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemClick.onItemClick("lesson", position);
                //lessonBackground.setBackgroundColor(context.getColor(R.color.colorBackgroundTop));
            }

        });
    }

    @Override
    public int getItemCount() {
        return lessonItems.size();
    }
}
