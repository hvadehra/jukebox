package org.melocine.events;

/**
 * Created by IntelliJ IDEA.
 * User: hemanshu.v
 * Date: 8/27/14
 * Time: 4:14 AM
 * To change this template use File | Settings | File Templates.
 */
public class PlaySelectedTrackEvent implements EventDispatcher.Event{
    public final int currentSelectedIndex;

    public PlaySelectedTrackEvent(int currentSelectedIndex) {
        this.currentSelectedIndex = currentSelectedIndex;
    }
}
