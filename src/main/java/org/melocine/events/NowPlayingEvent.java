package org.melocine.events;

import javafx.util.Duration;
import org.melocine.MetaData;

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

    public NowPlayingEvent(int index, MetaData metaData, Duration duration) {
        this.index = index;
        this.metaData = metaData;
        this.duration = duration;
    }

}
