package com.garytech.network;

import com.garytech.model.RssFeed;

import retrofit2.Call;
import retrofit2.http.GET;

public interface RssService {

    @GET("/movies/?rss=1")
    Call<RssFeed> getMovies();
}
