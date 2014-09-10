package org.melocine.events;

/**
 * Created by IntelliJ IDEA.
 * User: hemanshu.v
 * Date: 9/10/14
 * Time: 4:01 AM
 * To change this template use File | Settings | File Templates.
 */
public class ScrobbleSuccessEvent implements EventDispatcher.Event{
    public final String user;

    public ScrobbleSuccessEvent(String user) {
        this.user = user;
    }
}
