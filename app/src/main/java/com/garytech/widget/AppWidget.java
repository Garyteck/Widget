package com.garytech.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.garytech.data.FetchMovieService;
import com.garytech.model.Film;
import com.garytech.model.RssFeed;
import com.garytech.network.RssService;
import com.squareup.okhttp.OkHttpClient;

import java.util.List;
import java.util.concurrent.Callable;

import bolts.Continuation;
import bolts.Task;
import io.realm.Realm;
import retrofit.RestAdapter;
import retrofit.client.OkClient;
import retrofit.converter.SimpleXMLConverter;


/**
 * Implementation of App Widget functionality.
 */
public class AppWidget extends AppWidgetProvider{

    /** Networking **/
    private static final String RSS_FEED_URL = "https://kat.cr";

    private RestAdapter.Builder mRetrofit = new RestAdapter.Builder()
            .setEndpoint(RSS_FEED_URL)
            .setClient(new OkClient(new OkHttpClient()))
            .setConverter(new SimpleXMLConverter(false));

    private RssService mRssService = mRetrofit.build().create(RssService.class);

    /** Persistence **/
    private Realm mRealm;

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        if (intent.getAction().equals(FetchMovieService.NEW_MOVIE_BROADCAST_ACTION)) {
            updateAppWidget(
                    context,
                    AppWidgetManager.getInstance(context),
                    intent.getIntExtra(FetchMovieService.WIDGET_ID,-1),
                    getMovieById(intent.getStringExtra(FetchMovieService.NEW_MOVIE_EXTRA)));

        }
    }

    /**
     * Callback called periodically ( period defined in app_widget_info.xml
     * The method fetched the same RSS feed and store the items of the feed in database
     * @param context the context
     * @param appWidgetManager the singleton manager for the widget
     * @param appWidgetIds the list of ids for the widget that were set on the screen
     */

    @Override
    public void onUpdate(final Context context, final AppWidgetManager appWidgetManager, final int[] appWidgetIds) {

        Task.callInBackground(new Callable<RssFeed>() {


            @Override
            public RssFeed call() throws Exception {
                return mRssService.getMovies();
            }
        }).continueWith(new Continuation<RssFeed, Object>() {
            @Override
            public Object then(Task<RssFeed> task) throws Exception {

                if (!task.isCancelled() || !task.isFaulted()) {

                    List<Film> filmList = task.getResult().getChannel().getFilmList();
                    saveMovies(filmList);

                    for (int appWidgetId : appWidgetIds) {
                        updateAppWidget(context, appWidgetManager, appWidgetId, filmList.get(filmList.size() - 1));
                    }
                    mRealm.close();

                } else {
                    Toast.makeText(context, " error", Toast.LENGTH_LONG).show();
                    Log.w("Widget", task.getError());
                }
                return null;
            }
        }, Task.UI_THREAD_EXECUTOR);

    }

    /**
     * Save in database the list of movies in paraeter
     * @param filmList the list of movies to save in the database
     */
    private void saveMovies(List<Film> filmList) {
        mRealm = Realm.getDefaultInstance();
        mRealm.beginTransaction();
        mRealm.copyToRealmOrUpdate(filmList);
        mRealm.commitTransaction();
    }

    /**
     * query the database for the movie with the title
     * @param title the title of the movie
     * @return the Film which has the title given in parameter
     */
    private Film getMovieById(String title) {
        mRealm = Realm.getDefaultInstance();
        Film newMovie = mRealm.where(Film.class).equalTo("mTitle",title).findFirst();
        newMovie.load();
        return newMovie;
    }

    private void updateAppWidget(Context context, AppWidgetManager appWidgetManager, int appWidgetId, Film film) {
        Log.w("updateAppWidget","update the  widget");

        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.app_widget);

        views.setTextViewText(R.id.rss_title, film.getTitle());
        views.setOnClickPendingIntent(R.id.previous, browseMoviePendingIntent(context, true, appWidgetId, film));
        views.setOnClickPendingIntent(R.id.next, browseMoviePendingIntent(context, false, appWidgetId, film));
        views.setOnClickPendingIntent(R.id.rss_title, downloadTorrentIntent(context, appWidgetId, film));
        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    /**
     *
     * @param context the context
     * @param previous a flag telling which way we browse the list ( true , backwards, false forward)
     * @param appWidgetId the id of the widget that triggered that call
     * @param film the film that was displayed
     * @return  a pending intent that will triggered an Intent service, the Intent service will update the widget view
     */
    private PendingIntent browseMoviePendingIntent(Context context, boolean previous, int appWidgetId, Film film){
        Log.w("Pending intent", "on pending intentt " + appWidgetId);
        Intent intent = new Intent(context, FetchMovieService.class);
        intent.putExtra(FetchMovieService.BROWSE_MOVIE_LIST_SENSE_EXTRA,previous);
        intent.putExtra(FetchMovieService.MOVIE_IDENTIFIER_EXTRA, film.getTitle());
        intent.putExtra(FetchMovieService.WIDGET_ID, appWidgetId);
        return PendingIntent.getService(context,appWidgetId,intent,PendingIntent.FLAG_UPDATE_CURRENT);
    }

    /**
     *
     * @param context the context
     * @param appWidgetId the id of the widget that triggered this call
     * @param film the film that was displayed on the widget
     * @return a pending intent that will trigger an activity that can handle a torrent file
     */
    private PendingIntent downloadTorrentIntent(Context context, int appWidgetId, Film film){
        Log.w("Pending intent", "on pending intentt " + film.getmMagnetUri());
        Intent intent = new Intent();
        intent.setData(Uri.parse(film.getmMagnetUri()));
        return PendingIntent.getActivity(context, appWidgetId, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

}

