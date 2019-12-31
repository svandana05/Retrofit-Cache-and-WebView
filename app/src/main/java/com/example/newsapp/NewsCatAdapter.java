package com.example.newsapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.newsapp.ModelClasses.Article;

import java.util.List;

public class NewsCatAdapter extends RecyclerView.Adapter<NewsCatAdapter.NewsViewHolder> {

    MainActivity activity;
    List<String> newsCatList;
    private OnItemClick mCallback;
    String cat_Name;
    int row_index = -1;

    public interface OnItemClick {
        void onClick(String catName);
    }

    public NewsCatAdapter(MainActivity activity, List<String> newsArticlesList, OnItemClick listener) {
        this.activity = activity;
        this.newsCatList = newsArticlesList;
        this.mCallback = listener;
    }


    @NonNull
    @Override
    public NewsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(activity).inflate(R.layout.cat_list_item, null);
        NewsViewHolder dataViewHolder = new NewsViewHolder(view);
        return dataViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final NewsViewHolder holder, final int position) {
        holder.newsCat.setText(newsCatList.get(position));
        holder.newsCat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                row_index = position;
                cat_Name = newsCatList.get(position);
                mCallback.onClick(cat_Name);
                notifyDataSetChanged();
            }
        });
        if(row_index == position){
            holder.newsCat.setTextColor(activity.getResources().getColor(R.color.color_white));
            holder.newsCat.setBackgroundColor(activity.getResources().getColor(R.color.colorAccent));
        }
        else
        {
            holder.newsCat.setTextColor(activity.getResources().getColor(R.color.colorAccent));
            holder.newsCat.setBackgroundColor(activity.getResources().getColor(R.color.color_white));
        }
    }

    @Override
    public int getItemCount() {
        return newsCatList.size();
    }

    public class NewsViewHolder extends RecyclerView.ViewHolder{
        TextView newsCat;

        public NewsViewHolder(@NonNull View itemView) {
            super(itemView);
            newsCat = itemView.findViewById(R.id.tv_cat);
        }
    }
}
