package org.melocine.events;

/**
 * Created by IntelliJ IDEA.
 * User: hemanshu.v
 * Date: 8/27/14
 * Time: 4:13 AM
 * To change this template use File | Settings | File Templates.
 */
public class SetFooterEvent implements EventDispatcher.Event{
    private final String footerText;

    public SetFooterEvent(String text) {
        this.footerText = text;
    }

    public String getFooterText() {
        return footerText;
    }
}
