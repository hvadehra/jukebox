package org.melocine.events;

/**
 * Created by IntelliJ IDEA.
 * User: hemanshu.v
 * Date: 8/12/14
 * Time: 11:05 PM
 * To change this template use File | Settings | File Templates.
 */
public class PlayTimeChangedEvent implements EventDispatcher.Event {
    public final Double duration;
    public final Double newValue;

    public PlayTimeChangedEvent(Double duration, Double newValue) {
        this.duration = duration;
        this.newValue = newValue;
    }
}
