package org.melocine;

import javafx.collections.ObservableMap;

/**
 * Created by IntelliJ IDEA.
 * User: hemanshu.v
 * Date: 8/7/14
 * Time: 4:56 AM
 * To change this template use File | Settings | File Templates.
 */
public class MetaData {
    private final ObservableMap<String, Object> metadata;
    public final String artist;
    public final String title;
    public final String album;
    public final Double duration;

    public MetaData(ObservableMap<String, Object> metadata, Double duration) {
        this.metadata = metadata;
        this.artist = getField("artist");
        this.title = getField("title");
        this.album = getField("album");
        this.duration = duration;
    }

    private String getField(String key) {
        return getField(key, "");
    }

    private String getField(String key, String defaultValue){
        return (metadata.get(key) == null) ? defaultValue : metadata.get(key).toString().trim();
    }
}
