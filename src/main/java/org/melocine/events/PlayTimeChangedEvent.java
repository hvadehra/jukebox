package org.melocine.events;

import javafx.util.Duration;

/**
 * Created by IntelliJ IDEA.
 * User: hemanshu.v
 * Date: 8/12/14
 * Time: 11:05 PM
 * To change this template use File | Settings | File Templates.
 */
public class PlayTimeChangedEvent implements EventDispatcher.Event {
    public final Double duration;
    public final Duration newValue;

    public PlayTimeChangedEvent(Double duration, Duration newValue) {
        this.duration = duration;
        this.newValue = newValue;
    }
}
