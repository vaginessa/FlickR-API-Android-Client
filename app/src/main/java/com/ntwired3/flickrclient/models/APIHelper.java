package com.ntwired3.flickrclient.models;

import android.content.Context;

import com.koushikdutta.async.future.Future;
import com.koushikdutta.async.future.FutureCallback;

/**
 * Created by user on 20/06/2017.
 */

/**
 * Base class to all API helpers
 */
public abstract class APIHelper {

    protected Future loading;

    public abstract String getApiUrl(String text);
    public abstract void loadData(String url, final FutureCallback callback,Context context);
}
