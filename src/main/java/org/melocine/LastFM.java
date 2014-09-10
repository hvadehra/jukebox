package org.melocine;

import de.umass.lastfm.Authenticator;
import de.umass.lastfm.Caller;
import de.umass.lastfm.Session;
import de.umass.lastfm.Track;
import de.umass.lastfm.scrobble.ScrobbleData;
import de.umass.lastfm.scrobble.ScrobbleResult;
import org.melocine.events.EventDispatcher;
import org.melocine.events.NowPlayingEvent;
import org.melocine.events.ScrobbleSuccessEvent;
import org.melocine.events.ScrobbleTrackEvent;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by IntelliJ IDEA.
 * User: hemanshu.v
 * Date: 8/7/14
 * Time: 3:24 AM
 * To change this template use File | Settings | File Templates.
 */
public class LastFM {

    private final String user;
    private final String password;
    private final String key;
    private final String secret;
    private final ExecutorService threadPool;
    private final EventDispatcher eventDispatcher;
    private Session session;

    public LastFM(EventDispatcher eventDispatcher, String secret, String key, String password, String user) {
        this.eventDispatcher = eventDispatcher;
        Caller.getInstance().setUserAgent("melocine-jukebox");
        this.secret = secret;
        this.key = key;
        this.password = password;
        this.user = user;
        this.threadPool = Executors.newFixedThreadPool(5);
        this.session = Authenticator.getMobileSession(user, password, key, secret);
        registerEvents();
    }

    private void registerEvents() {
        eventDispatcher.register(NowPlayingEvent.class, new EventDispatcher.Receiver<NowPlayingEvent>() {
            @Override
            public void receive(NowPlayingEvent event) {
                setNowPlaying(event.metaData);
            }
        });
        eventDispatcher.register(ScrobbleTrackEvent.class, new EventDispatcher.Receiver<ScrobbleTrackEvent>() {
            @Override
            public void receive(ScrobbleTrackEvent event) {
                scrobble(event.getMetaData());
            }
        });
    }

    public void setNowPlaying(final MetaData metaData) {
        threadPool.submit(new Runnable() {
            @Override
            public void run() {
                System.err.println("Setting last.fm now playing for: " + metaData.artist + " - " + metaData.title + " - " + metaData.duration.intValue());
                Integer timestamp = (int) (System.currentTimeMillis() / 1000);
                ScrobbleData scrobbleData = new ScrobbleData(
                        metaData.artist,
                        metaData.title,
                        timestamp,
                        metaData.duration.intValue(),
                        metaData.album,
                        metaData.artist,
                        "",
                        0,
                        "");
                ScrobbleResult result = Track.updateNowPlaying(scrobbleData, session);
                System.err.println("last.fm was successful: " + result.isSuccessful());
                if (result.isSuccessful()){
                    eventDispatcher.dispatch(new ScrobbleSuccessEvent(user));
                }
                else{
                    session = Authenticator.getMobileSession(user, password, key, secret);
                }
            }
        });
    }

    public void scrobble(final MetaData metaData) {
        threadPool.submit(new Runnable() {
            @Override
            public void run() {
                System.err.println("Scrobbling to last.fm: " + metaData.artist + " - " + metaData.title + " - " + metaData.duration);
                int now = (int) (System.currentTimeMillis() / 1000);
                ScrobbleResult result = Track.scrobble(metaData.artist, metaData.title, now, session);
                System.err.println("Scrobble was successful: " + (result.isSuccessful() && !result.isIgnored()));
                if (!result.isSuccessful())
                    session = Authenticator.getMobileSession(user, password, key, secret);
            }
        });
    }
}
