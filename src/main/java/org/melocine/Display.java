package org.melocine;

import javafx.util.Duration;
import org.apache.commons.lang3.StringUtils;
import org.melocine.events.EventDispatcher;
import org.melocine.events.NowPlayingEvent;
import org.melocine.events.PlayAllEvent;
import org.melocine.events.PlayTimeChangedEvent;

import static org.apache.commons.lang3.time.DurationFormatUtils.formatDuration;

/**
 * Created by IntelliJ IDEA.
 * User: hemanshu.v
 * Date: 8/12/14
 * Time: 10:13 PM
 * To change this template use File | Settings | File Templates.
 */
public class Display {

    private static final int PROGRESS_WIDTH = 100;

    private final EventDispatcher eventDispatcher;
    public Display(EventDispatcher eventDispatcher) {
        this.eventDispatcher = eventDispatcher;
        registerForEvents();
    }

    private void registerForEvents() {
        eventDispatcher.register(PlayAllEvent.class, new EventDispatcher.Receiver<PlayAllEvent>() {
            @Override
            public void receive(PlayAllEvent event) {
                System.err.println("Playing " + event.files.size());
            }
        });

        eventDispatcher.register(NowPlayingEvent.class, new EventDispatcher.Receiver<NowPlayingEvent>() {
            @Override
            public void receive(NowPlayingEvent event) {
                MetaData metaData = event.metaData;
                System.out.println("\r" + (1 + event.index) + ". " + metaData.artist + " - " + metaData.title + "  [" + metaData.album + "] [" + formatTime(event.duration) + "]");
            }
        });

        eventDispatcher.register(PlayTimeChangedEvent.class, new EventDispatcher.Receiver<PlayTimeChangedEvent>() {
            @Override
            public void receive(PlayTimeChangedEvent event) {
                String done = StringUtils.repeat("=", (int) ((event.newValue.toSeconds() / event.duration) * PROGRESS_WIDTH));
                String remaining = StringUtils.repeat("-", (int)(((event.duration- event.newValue.toSeconds())/event.duration) * PROGRESS_WIDTH));
                System.out.print("\r[" + done + "[" + formatTime(event.newValue) + "]" + remaining + "]");
            }
        });
    }

    private String formatTime(Duration duration) {
        return formatDuration(Long.valueOf(String.valueOf(Double.valueOf(duration.toMillis()).intValue())), "mm:ss");
    }


}
