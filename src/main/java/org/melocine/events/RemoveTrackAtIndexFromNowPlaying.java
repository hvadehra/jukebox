package org.melocine.events;

/**
 * Created by IntelliJ IDEA.
 * User: hemanshu.v
 * Date: 9/9/14
 * Time: 11:27 PM
 * To change this template use File | Settings | File Templates.
 */
public class RemoveTrackAtIndexFromNowPlaying implements EventDispatcher.Event{
    public final int currentSelectedIndex;

    public RemoveTrackAtIndexFromNowPlaying(int currentSelectedIndex) {
        this.currentSelectedIndex = currentSelectedIndex;
    }
}
