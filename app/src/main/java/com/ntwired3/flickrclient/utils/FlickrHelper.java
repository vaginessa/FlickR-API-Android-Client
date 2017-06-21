package com.ntwired3.flickrclient.utils;

import android.content.Context;

import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.ntwired3.flickrclient.models.APIHelper;

/**
 * A FlickR API class to integrate API calls with FlickR
 */
public final class FlickrHelper extends APIHelper {

    private static FlickrHelper mFlickrHelper;

    private FlickrHelper() {
    }

    /**
     * Build the url for the next request. Uses Flickr's API
     * <p>
     * https://www.flickr.com/services/feeds/docs/photos_public/
     *
     * @param searchText Search text
     * @return Url for the next request
     */
    @Override
    public String getApiUrl(String searchText) {
        String base = "https://api.flickr.com/services/feeds/photos_public.gne?format=json&nojsoncallback=?";
        if (searchText != null && !searchText.isEmpty()) {
            base += "&tags=" + searchText;
        }
        return base;
    }

    /**
     * Creates the request and sets the callback for the received data
     * @param url url for api
     * @param callback callback when results are returned
     * @param mContext context
     */
    @Override
    public void loadData(String url, final FutureCallback callback,Context mContext) {
        if (loading != null && !loading.isDone() && !loading.isCancelled())
            return;

        // query googles image search api
        loading = Ion.with(mContext)
                .load(url)
                // get the results as json
                .asJsonObject()
                .setCallback(callback);
    }

    public static FlickrHelper getInstance() {
        if (mFlickrHelper == null)
            mFlickrHelper = new FlickrHelper();
        return mFlickrHelper;
    }
}
