package org.melocine;

import org.apache.commons.lang3.StringUtils;
import org.fusesource.jansi.AnsiConsole;
import org.melocine.events.EventDispatcher;
import org.melocine.events.NowPlayingEvent;
import org.melocine.events.PlayTimeChangedEvent;

import static org.apache.commons.lang3.time.DurationFormatUtils.formatDuration;
import static org.fusesource.jansi.Ansi.ansi;

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
    private int playListPos = 3;

    public Display(EventDispatcher eventDispatcher) {
        this.eventDispatcher = eventDispatcher;
        AnsiConsole.systemInstall();
        System.out.print(ansi().eraseScreen().cursor(playListPos, 1));
        registerForEvents();
    }

    private void registerForEvents() {
        eventDispatcher.register(NowPlayingEvent.class, new EventDispatcher.Receiver<NowPlayingEvent>() {
            @Override
            public void receive(NowPlayingEvent event) {
                MetaData metaData = event.metaData;
                System.out.println(ansi().cursor(2, 1).eraseLine());
                System.out.print(ansi().cursor(++playListPos, 1));
                System.out.print(ansi().eraseLine());
                System.out.println((1 + event.index) + ". " + metaData.artist + " - " + metaData.title + "  [" + metaData.album + "] [" + formatTime(event.duration.toSeconds()) + "]");
            }
        });

        eventDispatcher.register(PlayTimeChangedEvent.class, new EventDispatcher.Receiver<PlayTimeChangedEvent>() {
            @Override
            public void receive(PlayTimeChangedEvent event) {
                String done = StringUtils.repeat("=", (int) ((event.newValue / event.duration) * PROGRESS_WIDTH));
                String remaining = StringUtils.repeat("-", (int) (((event.duration - event.newValue) / event.duration) * PROGRESS_WIDTH));
                System.out.print(ansi().cursor(1, 1));
                System.out.print("[" + done + "[" + formatTime(event.newValue) + "]" + remaining + "]");
                System.out.print(ansi().eraseLine());
                System.out.print(ansi().cursor(2, 1));
                System.out.print(ansi().eraseLine());
            }
        });
    }

    private String formatTime(Double durationSeconds) {
        return formatDuration(1000*Long.valueOf(String.valueOf(durationSeconds.intValue())), "mm:ss");
    }


}
