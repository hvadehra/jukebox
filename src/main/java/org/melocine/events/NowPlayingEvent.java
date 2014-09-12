package org.melocine.events;

import javafx.util.Duration;
import org.melocine.types.MetaData;

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
    public final File current;
    public final MetaData metaData;
    public final Duration duration;
    public final List<File> playlist;

    public NowPlayingEvent(File current, MetaData metaData, Duration duration, List<File> playlist) {
        this.current = current;
        this.metaData = metaData;
        this.duration = duration;
        this.playlist = playlist;
    }

}
