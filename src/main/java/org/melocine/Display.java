package org.melocine;

import com.googlecode.lanterna.TerminalFacade;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.screen.ScreenWriter;
import com.googlecode.lanterna.terminal.Terminal;
import com.googlecode.lanterna.terminal.TerminalSize;
import org.apache.commons.lang3.StringUtils;
import org.melocine.events.EventDispatcher;
import org.melocine.events.NowPlayingEvent;
import org.melocine.events.PlayTimeChangedEvent;
import org.melocine.events.ShutdownEvent;

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
    private final Screen screen;
    private final ScreenWriter screenWriter;
    private int playListPos = 0;

    public Display(EventDispatcher eventDispatcher) {
        this.eventDispatcher = eventDispatcher;
        Terminal terminal = TerminalFacade.createTerminal();
        this.screen = new Screen(terminal, new TerminalSize(150, 50));
        new Thread(new Runnable() {
            @Override
            public void run() {
                screen.startScreen();
            }
        }).start();
        this.screenWriter = new ScreenWriter(screen);
        registerForEvents();
    }

    private void registerForEvents() {
        eventDispatcher.register(NowPlayingEvent.class, new EventDispatcher.Receiver<NowPlayingEvent>() {
            @Override
            public void receive(NowPlayingEvent event) {
                MetaData metaData = event.metaData;
                screenWriter.drawString(1, 3 + (++playListPos)%45, (1 + event.index) + ". " + metaData.artist + " - " + metaData.title + "  [" + metaData.album + "] [" + formatTime(event.duration.toSeconds()) + "]");
                screenWriter.drawString(1, 4 + (playListPos)%45, StringUtils.repeat(" ", PROGRESS_WIDTH));
                screen.refresh();
            }
        });

        eventDispatcher.register(ShutdownEvent.class, new EventDispatcher.Receiver<ShutdownEvent>() {
            @Override
            public void receive(ShutdownEvent event) {
                screen.stopScreen();
                System.exit(1);
            }
        });

        eventDispatcher.register(PlayTimeChangedEvent.class, new EventDispatcher.Receiver<PlayTimeChangedEvent>() {
            @Override
            public void receive(PlayTimeChangedEvent event) {
                String done = StringUtils.repeat("=", (int) ((event.newValue / event.duration) * PROGRESS_WIDTH));
                String remaining = StringUtils.repeat("-", (int) (((event.duration - event.newValue) / event.duration) * PROGRESS_WIDTH));
                screenWriter.drawString(1, 1, "[" + done + "[" + formatTime(event.newValue) + "]" + remaining + "]");
                screen.refresh();
            }
        });
    }

    private String formatTime(Double durationSeconds) {
        return formatDuration(1000*Long.valueOf(String.valueOf(durationSeconds.intValue())), "mm:ss");
    }


}
