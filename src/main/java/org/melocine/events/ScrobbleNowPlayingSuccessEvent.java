package org.melocine.events;

/**
 * Created by IntelliJ IDEA.
 * User: hemanshu.v
 * Date: 9/10/14
 * Time: 4:01 AM
 * To change this template use File | Settings | File Templates.
 */
public class ScrobbleNowPlayingSuccessEvent implements EventDispatcher.Event{
    public final String user;
    public final String artist;
    public final String track;

    public ScrobbleNowPlayingSuccessEvent(String user, String artist, String track) {
        this.user = user;
        this.artist = artist;
        this.track = track;
    }
}
