package com.garytech.network;

import com.garytech.model.RssFeed;


import retrofit.Callback;
import retrofit.http.GET;


/**
 * WebService Interface , contains the  endpoints of the webservice
 */
public interface RssService {

    @GET("/movies/?rss=1")
    void getMovies(Callback<RssFeed> rssFeedCallback);
}
