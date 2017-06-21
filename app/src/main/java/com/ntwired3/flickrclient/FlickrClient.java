package com.ntwired3.flickrclient;

import android.app.Application;
import android.os.StrictMode;

import io.realm.Realm;


public class FlickrClient extends Application {


    public FlickrClient() {
        super();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Realm.init(this);
        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                .detectAll()
                .penaltyDeath()
                .build());
    }
}
