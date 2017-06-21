package com.ntwired3.flickrclient.models;

import com.google.gson.annotations.SerializedName;

import java.util.Comparator;
import java.util.Date;

import io.realm.RealmModel;
import io.realm.annotations.Ignore;
import io.realm.annotations.RealmClass;

@RealmClass
public class FlickrImage implements RealmModel {


    private String link;
    private String title;
    private Media media;

    @SerializedName("published")
    private Date date_published;
    private Date date_taken;
    private String author;
    private String author_id;
    private String tags;


    /*ORDER THE LIST OF IMAGES BY DATE_TAKEN*/
    @Ignore
    public static Comparator<FlickrImage> COMPARE_BY_DATE_TAKEN = new Comparator<FlickrImage>() {
        public int compare(FlickrImage one, FlickrImage other) {
            return one.getDate_taken().compareTo(other.getDate_taken());
        }
    };

    /*ORDER THE LIST OF IMAGES BY DATE_PUBLISHED*/
    @Ignore
    public static Comparator<FlickrImage> COMPARE_BY_DATE_PUBLISHED = new Comparator<FlickrImage>() {
        public int compare(FlickrImage one, FlickrImage other) {
            return one.getPublished().compareTo(other.getPublished());
        }
    };

    public FlickrImage() {
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Media getMedia() {
        return media;
    }

    public void setMedia(Media media) {
        this.media = media;
    }

    public Date getDate_taken() {
        return date_taken;
    }

    public void setDate_taken(Date date_taken) {
        this.date_taken = date_taken;
    }

    public Date getPublished() {
        return date_published;
    }

    public void setPublished(Date published) {
        this.date_published = published;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getAuthor_id() {
        return author_id;
    }

    public void setAuthor_id(String author_id) {
        this.author_id = author_id;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public String getImageURL() {
        return this.getMedia().getMediaUrl();
    }
}
