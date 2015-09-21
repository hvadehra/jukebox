package org.melocine.types;

import javafx.collections.ObservableMap;

import java.io.Serializable;

/**
 * Created by IntelliJ IDEA.
 * User: hemanshu.v
 * Date: 8/7/14
 * Time: 4:56 AM
 * To change this template use File | Settings | File Templates.
 */
public class MetaData implements Serializable {
    public static final int MAX_RATING = 5;

    private ObservableMap<String, Object> metadata;
    public final String artist;
    public final String title;
    public final String album;
    public Integer rating;
    public final Long duration;

    public MetaData(ObservableMap<String, Object> metadata, Double duration) {
        this.metadata = metadata;
        this.artist = getField("artist");
        this.title = getField("title");
        this.album = getField("album");
        this.duration = duration.longValue();
    }

    public MetaData(String artist, String album, String title, String rating, Integer duration) {
        this.artist = artist;
        this.album = album;
        this.title = title;
        this.rating = convertToStarRating(rating);
        this.duration = duration.longValue();
    }

    private int convertToStarRating(String rating) {
        return rating.isEmpty() ? 0 : Integer.parseInt(rating) * MAX_RATING / 255;
    }


    private String getField(String key) {
        return getField(key, "");
    }

    private String getField(String key, String defaultValue){
        return (metadata.get(key) == null) ? defaultValue : metadata.get(key).toString().trim();
    }

    public Boolean matches(String searchTerm) {
        return artist.toLowerCase().startsWith(searchTerm.toLowerCase())
            || title.toLowerCase().startsWith(searchTerm.toLowerCase())
            || album.toLowerCase().startsWith(searchTerm.toLowerCase());
    }
}
