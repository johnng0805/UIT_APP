package com.example.uit_app;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.stripe.android.view.IconTextInputLayout;

import java.util.ArrayList;

import Interface.OnItemClick;

public class DocumentItemAdapter extends RecyclerView.Adapter<DocumentItemAdapter.MyViewHolder> {

    private ArrayList<String> documentItems;
    private Context context;
    private OnItemClick onItemClick;

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView documentTitle;
        public MyViewHolder(View view) {
            super(view);
            documentTitle = view.findViewById(R.id.item_document_title);
        }
    }

    public DocumentItemAdapter(Context context, ArrayList<String> documentItems, OnItemClick onItemClick) {
        this.documentItems = documentItems;
        this.context = context;
        this.onItemClick = onItemClick;
    }

    @NonNull
    @Override
    public DocumentItemAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        this.context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View documentView = inflater.inflate(R.layout.item_document, parent, false);
        return new MyViewHolder(documentView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        String documentItem = documentItems.get(position);

        TextView documentTitle = holder.documentTitle;
        documentTitle.setText(documentItem);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemClick.onItemClick("doc", position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return documentItems.size();
    }
}
