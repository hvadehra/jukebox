package org.melocine.events;

/**
 * Created by IntelliJ IDEA.
 * User: hemanshu.v
 * Date: 9/9/14
 * Time: 10:23 PM
 * To change this template use File | Settings | File Templates.
 */
public class VolumeChangedEvent implements EventDispatcher.Event{
    public final Double volume;

    public VolumeChangedEvent(Double volume) {
        this.volume = volume;
    }
}
