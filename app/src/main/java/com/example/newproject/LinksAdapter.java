package com.example.newproject;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class LinksAdapter extends RecyclerView.Adapter<LinksAdapter.LinksViewHolder>{

    private List<LinksModel> linksModelList;
    private Context context;

    public LinksAdapter(List<LinksModel> linksModelList, Context context) {
        this.linksModelList = linksModelList;
        this.context = context;
    }

    @NonNull
    @Override
    public LinksAdapter.LinksViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_links, parent, false);
        return new LinksAdapter.LinksViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LinksAdapter.LinksViewHolder holder, int position) {
        LinksModel linksModel = linksModelList.get(position);

        holder.titleTV.setText(linksModel.getLinkDesc());
        holder.linkTV.setText(linksModel.getImpLinks());
       holder.linkTV.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(linksModel.getImpLinks()));
              context.startActivity(intent);
           }
       });
    }

    @Override
    public int getItemCount() {
        return linksModelList.size();
    }

    public class LinksViewHolder extends RecyclerView.ViewHolder {
        TextView titleTV,linkTV;
        public LinksViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTV=itemView.findViewById(R.id.textViewLinkDesc);
            linkTV=itemView.findViewById(R.id.textViewLink);
        }
    }
}
