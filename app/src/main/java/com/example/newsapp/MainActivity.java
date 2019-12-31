package com.example.newsapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.newsapp.ModelClasses.Article;
import com.example.newsapp.ModelClasses.News;
import com.google.gson.Gson;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import okhttp3.Cache;
import okhttp3.CacheControl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.example.newsapp.ApiClient.BASE_URL;

public class MainActivity extends AppCompatActivity implements NewsCatAdapter.OnItemClick{
    RecyclerView newsArticleListView, newsCatList;
    NewsAdapter adapter;
    NewsCatAdapter catAdapter;
    String newsCategory;
    Retrofit retrofit;
    TextView tvHeading;

    int cacheSize = 2 * 1024 * 1024; // this is 2MB
    News news;
    List<String> newsCats;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        newsArticleListView = findViewById(R.id.news_list);
        newsCatList = findViewById(R.id.cat_list);
        tvHeading = findViewById(R.id.tv_heading);
        newsArticleListView.setLayoutManager(new LinearLayoutManager(MainActivity.this));
        newsCatList.setLayoutManager(new LinearLayoutManager(MainActivity.this, LinearLayoutManager.HORIZONTAL, false));

        File cacheFile = new File(this.getCacheDir(), "http_cache");
        Cache cache = new Cache(cacheFile, cacheSize);
        OkHttpClient client = new OkHttpClient().newBuilder()
                .cache(cache)
                .addNetworkInterceptor(REWRITE_RESPONSE_INTERCEPTOR)
                .addInterceptor(REWRITE_RESPONSE_INTERCEPTOR_OFFLINE)
                .build();
        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();

        newsCategory = "general";
        getResponseFromAPI(newsCategory);

        newsCats = new ArrayList<>();
        newsCats.add("general");
        newsCats.add("business");
        newsCats.add("entertainment");
        newsCats.add("health");
        newsCats.add("science");
        newsCats.add("sports");
        newsCats.add("technology");

        catAdapter = new NewsCatAdapter(this, newsCats, this);
        newsCatList.setAdapter(catAdapter);
//        Call<News> call = ApiServices.getApiClient().getArticles("in", "as", newsCategory);
//        call.enqueue(new Callback<News>() {
//            @Override
//            public void onResponse(Call<News> call, Response<News> response) {
//                News news = response.body();
//                if (news.getStatus().equals("200")){
//                    List<Article> articleList = news.getArticles();
//                }
//            }
//            @Override
//            public void onFailure(Call<News> call, Throwable t) {
//            }
//        });

    }


    public void getResponseFromAPI(String newsCategory){
        Log.e("MainActivity", " getResponceFromAPI - category:"+newsCategory);
        tvHeading.setText("Showing news from category: "+newsCategory);
        ApiClient client1 = retrofit.create(ApiClient.class);
        Call<News> call =  client1.getArticles("in", "16a86d8cbed648a784a2bc2d99267945", newsCategory);
        call.enqueue(new Callback<News>() {
            @Override
            public void onResponse(Call<News> call, retrofit2.Response<News> response) {
                try {
                    Log.e("MainActivity", "onResponse- "+response.body().getArticles().get(0).getTitle());
                    news = response.body();
                    adapter = new NewsAdapter(MainActivity.this, news.getArticles());
                    newsArticleListView.setAdapter(adapter);
                }catch (NullPointerException e){}
            }

            @Override
            public void onFailure(Call<News> call, Throwable t) {
                Log.e("MainActivity", "onFailure- "+t.getMessage());
            }
        });
    }

    private final Interceptor REWRITE_RESPONSE_INTERCEPTOR = new Interceptor() {
        @Override
        public okhttp3.Response intercept(Chain chain) throws IOException {
            okhttp3.Response originalResponse = chain.proceed(chain.request());
            String cacheControl = originalResponse.header("Cache-Control");
            if (cacheControl == null || cacheControl.contains("no-store") || cacheControl.contains("no-cache") ||
                    cacheControl.contains("must-revalidate") || cacheControl.contains("max-age=0")) {
                return originalResponse.newBuilder()
                        .removeHeader("Pragma")
                        .header("Cache-Control", "public, max-age=" + 5000)
                        .build();
            } else {
                return originalResponse;
            }
        }
    };

    private final Interceptor REWRITE_RESPONSE_INTERCEPTOR_OFFLINE = new Interceptor() {
        @Override
        public okhttp3.Response intercept(Chain chain) throws IOException {
            Request request = chain.request();
            if (!AppUtils.isDataConnected(getApplicationContext())) {
                request = request.newBuilder()
                        .removeHeader("Pragma")
                        .header("Cache-Control", "public, only-if-cached")
                        .build();
            }
            return chain.proceed(request);
        }
    };

    @Override
    public void onClick(String catName) {
        newsCategory = catName;
        getResponseFromAPI(newsCategory);
    }
}
