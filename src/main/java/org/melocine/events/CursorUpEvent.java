package org.melocine.events;

/**
 * Created by IntelliJ IDEA.
 * User: hemanshu.v
 * Date: 8/27/14
 * Time: 3:56 AM
 * To change this template use File | Settings | File Templates.
 */
public class CursorUpEvent implements EventDispatcher.Event{
    public final int numLines;

    public CursorUpEvent(int numLines) {
        this.numLines = numLines;
    }
}
