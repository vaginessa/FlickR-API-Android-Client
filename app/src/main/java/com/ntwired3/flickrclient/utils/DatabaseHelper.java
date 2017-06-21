package com.ntwired3.flickrclient.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;

import java.util.Arrays;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmModel;

/**
 * Created by user on 20/06/2017.
 */

public class DatabaseHelper<T extends RealmModel> {

    private Realm mRealm;
    private static DatabaseHelper mDBHelper;

    public static DatabaseHelper getInstance() {
        if (mDBHelper == null)
            mDBHelper = new DatabaseHelper();

        return mDBHelper;
    }

    public DatabaseHelper() {
        mRealm = Realm.getDefaultInstance();
    }

    /**
     * Process a successful API result
     *
     * @param result   the API's response
     * @param dataType The requested dataType(FlickrImage in this project's case)
     * @return list of the items parsed
     */
    public List<T> processApiResult(JsonArray result, Class<T[]> dataType) {

        // populate
        Gson gson = new GsonBuilder()
                .setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ")
                .create();
        T[] itemsList = gson.fromJson(result, dataType);
        List<T> list = Arrays.asList(itemsList);

        // Open a transaction to store items into the realm
        try {
            getRealm().beginTransaction();
            getRealm().insertOrUpdate(list);
            getRealm().commitTransaction();
        } catch (Exception ex) {
            ex.printStackTrace();
            getRealm().cancelTransaction();
        }
        return list;
    }

    /**
     * Gets all the data by dataType from the database
     *
     * @param dataType - the dataType (FlickrImage in this project's case)
     * @return list of the items
     */
    public List<T> getCache(Class<T> dataType) {
        return this.getRealm().where(dataType).findAll();
    }

    /**
     * used to make Safe Calls to DB Instance
     * @return
     */
    private Realm getRealm() {
        if (this.mRealm == null || this.mRealm.isClosed())
            mRealm = Realm.getDefaultInstance();
        return mRealm;
    }

    /**
     * Closes Realm instance
     */
    public void close() {
        if (mRealm != null && !mRealm.isClosed())
            mRealm.close();
    }
}
