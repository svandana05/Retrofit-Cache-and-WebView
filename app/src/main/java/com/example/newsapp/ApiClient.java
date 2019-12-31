package com.example.newsapp;

import io.reactivex.Observable;
import com.example.newsapp.ModelClasses.News;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ApiClient {

    String BASE_URL = "https://newsapi.org/v2/";

    @GET("top-headlines")
    Call<News> getArticles(@Query("country") String country, @Query("apiKey") String apiKey, @Query("category") String category);
}
