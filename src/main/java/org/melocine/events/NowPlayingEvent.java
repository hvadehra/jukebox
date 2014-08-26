package org.melocine.events;

import javafx.util.Duration;
import org.melocine.MetaData;

import java.io.File;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: hemanshu.v
 * Date: 8/9/14
 * Time: 1:50 AM
 * To change this template use File | Settings | File Templates.
 */
public class NowPlayingEvent implements EventDispatcher.Event {
    public final int index;
    public final MetaData metaData;
    public final Duration duration;
    public final List<File> playlist;

    public NowPlayingEvent(int index, MetaData metaData, Duration duration, List<File> playlist) {
        this.index = index;
        this.metaData = metaData;
        this.duration = duration;
        this.playlist = playlist;
    }

}
