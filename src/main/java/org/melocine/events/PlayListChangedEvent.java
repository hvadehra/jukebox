package org.melocine.events;

import java.io.File;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: hemanshu.v
 * Date: 9/9/14
 * Time: 11:17 PM
 * To change this template use File | Settings | File Templates.
 */
public class PlayListChangedEvent implements EventDispatcher.Event{
    public final List<File> playlist;

    public PlayListChangedEvent(List<File> playlist) {
        this.playlist = playlist;
    }
}
