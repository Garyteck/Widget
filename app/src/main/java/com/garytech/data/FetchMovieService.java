package com.garytech.data;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import com.garytech.model.Film;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;

/**
 * The FetchMovieService fetch the previous or next movie to the currently displayed movie
 * It broadcast the new movie identifier to the widget
 */
public class FetchMovieService extends IntentService {

    /**
     * Extras
     */
    public static final String BROWSE_MOVIE_LIST_SENSE_EXTRA = "com.garytech.data.action.BROWSE_MOVIE_LIST_SENSE_EXTRA";
    public static final String MOVIE_IDENTIFIER_EXTRA = "MOVIE_IDENTIFIER_EXTRA";
    public static final String NEW_MOVIE_EXTRA = "NEW_MOVIE_EXTRA";

    /**
     * Broadcast action
     */
    public static final String NEW_MOVIE_BROADCAST_ACTION = "com.garytech.data.NEW_MOVIE_BROADCAST_ACTION";
    /**
     * Key Id of the widget to update
     */
    public static String WIDGET_ID = "WIDGET_ID";

    /**
     * Id of the widget that triggered this service
     */
    private int mWidgetId;

    /**
     * New Realm instance
     */
    private Realm mRealm;

    /**
     * Currently displayed movie
     */
    private Film mFilm;

    /**
     * Full list of movies in the database
     */
    private RealmResults<Film> mFilms;

    /**
     * Title of the movie currently displayed on the widget
     */
    private String mTitle;


    public FetchMovieService() {
        super("FetchMovieService");
    }


    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {

            RealmConfiguration realmConfiguration = new RealmConfiguration.Builder(this).build();
            mRealm = Realm.getInstance(realmConfiguration);
            mTitle = intent.getStringExtra(MOVIE_IDENTIFIER_EXTRA);
            mFilm = mRealm.where(Film.class).equalTo("mTitle", mTitle).findFirstAsync();
            mFilm.load();
            mFilms = mRealm.where(Film.class).findAllAsync();
            mFilms.load();

            mWidgetId = intent.getIntExtra(WIDGET_ID,-1);
            if (intent.getBooleanExtra(BROWSE_MOVIE_LIST_SENSE_EXTRA,false)) {
                broadcastWidgetUpdate(mFilms.get(mFilms.indexOf(mFilm) - 1 > 0 ? mFilms.indexOf(mFilm) - 1 : mFilms.size() - 1));
            } else {
                broadcastWidgetUpdate(mFilms.get(mFilms.indexOf(mFilm) + 1 < mFilms.size() ? mFilms.indexOf(mFilm) + 1 : 0));
            }
            mRealm.close();
        }
    }

    /**
     * Sends a broadcast to be intercepted by the appwidget
     * The app widget will then display the movie with the title given in parameter
     * @param film title of the movie to display on the widget
     */
    private void broadcastWidgetUpdate(Film film) {

        Intent intent = new Intent();
        intent.putExtra(NEW_MOVIE_EXTRA, film.getTitle());
        intent.setAction(NEW_MOVIE_BROADCAST_ACTION);
        intent.putExtra(WIDGET_ID,mWidgetId);
        getBaseContext().sendBroadcast(intent);
    }

}
