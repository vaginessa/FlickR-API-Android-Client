package com.ntwired3.flickrclient.models;

import com.google.gson.annotations.SerializedName;

import io.realm.RealmModel;
import io.realm.annotations.RealmClass;

/**
 * Created by user on 20/06/2017.
 */

@RealmClass
public class Media implements RealmModel {

    @SerializedName("m")
    private String mediaUrl;

    public Media(String mediaUrl) {
        this.mediaUrl = mediaUrl;
    }

    public Media() {
    }

    public String getMediaUrl() {
        return mediaUrl;
    }

    public void setMediaUrl(String mediaUrl) {
        this.mediaUrl = mediaUrl;
    }
}
