package com.garytech.widget;

import android.app.Application;

import io.realm.Realm;
import io.realm.RealmConfiguration;

/**
 * Instance of the app
 * We set the database configuration here
 */
public class AppInstance extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        RealmConfiguration realmConfiguration = new RealmConfiguration.Builder(this).build();
        Realm.setDefaultConfiguration(realmConfiguration);
    }
}
