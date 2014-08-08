package org.melocine.events;

import static org.melocine.events.EventDispatcher.Event;

/**
 * Created by IntelliJ IDEA.
 * User: hemanshu.v
 * Date: 8/9/14
 * Time: 2:05 AM
 * To change this template use File | Settings | File Templates.
 */
public interface EventRunnable<T extends Event> {
    public void run(T event);
}
