package org.melocine;

import com.googlecode.lanterna.TerminalFacade;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.screen.ScreenWriter;
import com.googlecode.lanterna.terminal.Terminal;
import com.googlecode.lanterna.terminal.TerminalSize;
import org.apache.commons.lang3.StringUtils;
import org.melocine.events.*;

import java.io.File;
import java.util.List;

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
    private static final int TERMINAL_WIDTH = 150;
    private static final int TERMINAL_HEIGHT = 50;

    private final EventDispatcher eventDispatcher;
    private final Screen screen;
    private final ScreenWriter screenWriter;
    private final int playListPos = 5;
    private final int playListDisplaySize = 40;
    private int currentSelectedIndex = 0;
    private List<File> playlist;
    private int currentPlayingIndex;

    public Display(EventDispatcher eventDispatcher) {
        this.eventDispatcher = eventDispatcher;
        Terminal terminal = TerminalFacade.createTerminal();
        this.screen = new Screen(terminal, new TerminalSize(TERMINAL_WIDTH, TERMINAL_HEIGHT));
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
                playlist = event.playlist;
                currentPlayingIndex = event.index;
                updateScreen();
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
                setDefaultStyle();
                screenWriter.drawString(1, 1, "[" + done + "[" + formatTime(event.newValue) + "]" + remaining + "]");
                screen.refresh();
            }
        });
        
        eventDispatcher.register(CursorUpEvent.class, new EventDispatcher.Receiver<CursorUpEvent>() {
            @Override
            public void receive(CursorUpEvent event) {
                currentSelectedIndex = (currentSelectedIndex > 0) ? (currentSelectedIndex - 1) : currentSelectedIndex;
                updateScreen();
            }
        });

        eventDispatcher.register(CursorDownEvent.class, new EventDispatcher.Receiver<CursorDownEvent>() {
            @Override
            public void receive(CursorDownEvent event) {
                currentSelectedIndex = (currentSelectedIndex < playlist.size()-1) ? (currentSelectedIndex + 1) : currentSelectedIndex;
                updateScreen();
            }
        });

        eventDispatcher.register(ReturnKeyPressEvent.class, new EventDispatcher.Receiver<ReturnKeyPressEvent>() {
            @Override
            public void receive(ReturnKeyPressEvent event) {
                eventDispatcher.dispatch(new PlaySelectedTrackEvent(currentSelectedIndex));
            }
        });
    }

    private void updateScreen() {
        drawPlaylist();
        screen.refresh();
    }

    private String formatTime(Double durationSeconds) {
        return formatDuration(1000*Long.valueOf(String.valueOf(durationSeconds.intValue())), "mm:ss");
    }

    private void drawPlaylist(){
        int beginIndex = (currentPlayingIndex > playListDisplaySize/2) ? (currentPlayingIndex - playListDisplaySize/2) : 0;
        int endIndex = (playlist.size() > beginIndex + playListDisplaySize) ? (beginIndex + playListDisplaySize) : playlist.size();
        beginIndex = (currentSelectedIndex > (endIndex-1)) ? (currentSelectedIndex - playListDisplaySize + 1) : beginIndex;
        endIndex = (playlist.size() > beginIndex + playListDisplaySize) ? (beginIndex + playListDisplaySize) : playlist.size();
        for (int i = beginIndex; i < endIndex; i++) {
            int displayPos = playListPos + i - beginIndex;
            File entry = playlist.get(i);
            String entryDisplay = (i+1) + ". " + entry.getAbsolutePath();
            setDefaultStyle();
            if (currentSelectedIndex == i){
                setCurrentSelectedStyle();
            }
            if (i == currentPlayingIndex){
                setCurrentPlayingStyle();
            }
            screenWriter.drawString(1, displayPos, StringUtils.repeat(" ", TERMINAL_WIDTH));
            screenWriter.drawString(1, displayPos, entryDisplay);
        }
    }

    private void setCurrentSelectedStyle() {
        screenWriter.setBackgroundColor(Terminal.Color.WHITE);
        screenWriter.setForegroundColor(Terminal.Color.BLACK);
    }

    private void setDefaultStyle() {
        screenWriter.setForegroundColor(Terminal.Color.WHITE);
        screenWriter.setBackgroundColor(Terminal.Color.BLACK);
    }

    private void setCurrentPlayingStyle() {
        screenWriter.setForegroundColor(Terminal.Color.RED);
    }
}
