package com.example.newsapp;

import android.content.Context;
import android.content.Intent;
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

public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.NewsViewHolder> {

    Context context;
    List<Article> newsArticlesList;

    public NewsAdapter(Context context, List<Article> newsArticlesList) {
        this.context = context;
        this.newsArticlesList = newsArticlesList;
    }


    @NonNull
    @Override
    public NewsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.news_list_item, null);
        NewsViewHolder dataViewHolder = new NewsViewHolder(view);
        return dataViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull NewsViewHolder holder, final int position) {
        Article article = newsArticlesList.get(position);
        Glide.with(context).load(article.getUrlToImage()).into(holder.newsImage);
        holder.newsTitle.setText(article.getTitle());
        holder.newsSource.setText("Source: "+article.getSource().getName());
        holder.newsDate.setText("Published on:\n"+article.getPublishedAt());
        holder.newsAuthor.setText("By: "+article.getAuthor());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, WebViewActivity.class);
                intent.putExtra("link", newsArticlesList.get(position).getUrl());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return newsArticlesList.size();
    }

    public class NewsViewHolder extends RecyclerView.ViewHolder{
        ImageView newsImage;
        TextView newsTitle, newsSource, newsAuthor, newsDate;

        public NewsViewHolder(@NonNull View itemView) {
            super(itemView);
            newsImage = itemView.findViewById(R.id.news_image);
            newsTitle = itemView.findViewById(R.id.news_title);
            newsSource = itemView.findViewById(R.id.news_source);
            newsAuthor = itemView.findViewById(R.id.news_author);
            newsDate = itemView.findViewById(R.id.news_date);
        }
    }
}
