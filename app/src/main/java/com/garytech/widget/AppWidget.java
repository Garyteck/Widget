package com.garytech.widget;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.garytech.model.Film;
import com.garytech.model.RssFeed;
import com.garytech.network.RssService;

import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.simplexml.SimpleXmlConverterFactory;

/**
 * Implementation of App Widget functionality.
 */
public class AppWidget extends AppWidgetProvider {

    private static final String RSS_FEED_URL = "https://kat.cr";

    private Retrofit mRetrofit = new Retrofit.Builder()
            .baseUrl(RSS_FEED_URL)
            .client(new OkHttpClient())
            .addConverterFactory(SimpleXmlConverterFactory.createNonStrict())
            .build();


    private RssService mRssService = mRetrofit.create(RssService.class);


    private List<Film> mFilmList = new ArrayList<>();

    @Override
    public void onUpdate(final Context context, final AppWidgetManager appWidgetManager, final int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them

        Call<RssFeed> call = mRssService.getMovies();
        call.enqueue(new Callback<RssFeed>() {

            @Override
            public void onResponse(Call<RssFeed> call, Response<RssFeed> response) {
                if (response != null) {
                    mFilmList = response.body().getChannel().getFilmList();

                    final int N = appWidgetIds.length;

                    for (int i = 0; i < N; i++) {
                        updateAppWidget(context, appWidgetManager, mFilmList.get(0).getTitle(), appWidgetIds[i]);
                    }

                } else {
                    onFailure(call, new Throwable());
                }
            }

            @Override
            public void onFailure(Call<RssFeed> call, Throwable t) {
                Toast.makeText(context,"Failure to call WS",Toast.LENGTH_LONG).show();
                Log.w(AppWidget.class.getName(), t.getCause());
            }
        });

    }


    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager, String title,
                                int appWidgetId) {

        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.app_widget);
        views.setTextViewText(R.id.appwidget_text, title);

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }
}

