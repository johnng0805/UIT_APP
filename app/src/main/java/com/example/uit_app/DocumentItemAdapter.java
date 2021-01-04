package com.example.uit_app;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class DocumentItemAdapter extends RecyclerView.Adapter<DocumentItemAdapter.MyViewHolder> {

    private ArrayList<String> documentItems;
    private Context context;

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView documentTitle;
        public MyViewHolder(View view) {
            super(view);
            documentTitle = view.findViewById(R.id.item_document_title);
        }
    }

    public DocumentItemAdapter(Context context, ArrayList<String> documentItems) {
        this.documentItems = documentItems;
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
    }

    @Override
    public int getItemCount() {
        return documentItems.size();
    }
}
