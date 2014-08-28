package org.melocine;

import com.googlecode.lanterna.TerminalFacade;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.screen.ScreenCharacterStyle;
import com.googlecode.lanterna.screen.ScreenWriter;
import com.googlecode.lanterna.terminal.Terminal;
import com.googlecode.lanterna.terminal.TerminalSize;
import com.googlecode.lanterna.terminal.text.UnixTerminal;
import org.apache.commons.lang3.StringUtils;
import org.melocine.events.*;

import java.io.File;
import java.util.List;

import static org.apache.commons.lang3.time.DurationFormatUtils.formatDuration;
import static org.melocine.MetaData.MAX_RATING;

/**
 * Created by IntelliJ IDEA.
 * User: hemanshu.v
 * Date: 8/12/14
 * Time: 10:13 PM
 * To change this template use File | Settings | File Templates.
 */
public class Display {
    private static int TERMINAL_WIDTH = 200;
    private static int TERMINAL_HEIGHT = 70;
    private static int PROGRESS_WIDTH = TERMINAL_WIDTH;
    public static int PLAYLIST_DISPLAY_SIZE = TERMINAL_HEIGHT - 10;
    private static final int PLAYLIST_YPOS = 5;
    private static final int PROGRESS_BAR_YPOS = 1;
    private static final int NOW_PLAYING_YPOS = 2;
    private static int PLAYLIST_INDEX_WIDTH = 4;
    private static int PLAYLIST_TITLE_WIDTH = 40;
    private static int PLAYLIST_ARTIST_WIDTH = 35;
    private static int PLAYLIST_ALBUM_WIDTH = 55;
    private static int PLAYLIST_RATING_WIDTH = 10;

    private final EventDispatcher eventDispatcher;
    private final MetaDataStore metaDataStore;
    private final Screen screen;
    private final ScreenWriter screenWriter;
    private int currentPlayingIndex;
    private List<File> playlist;
    private int currentSelectedIndex = 0;
    private int displayBeginIndex = 0;
    private int displayEndIndex = PLAYLIST_DISPLAY_SIZE;

    public Display(EventDispatcher eventDispatcher, MetaDataStore metaDataStore, int width, int height) {
        this.eventDispatcher = eventDispatcher;
        this.metaDataStore = metaDataStore;
        UnixTerminal terminal = TerminalFacade.createUnixTerminal();
        terminal.addResizeListener(terminalResizeListener());
        screen = new Screen(terminal, width, height);
        new Thread(new Runnable() {
            @Override
            public void run() {
                screen.startScreen();
            }
        }).start();
        this.screenWriter = new ScreenWriter(screen);
        clearScreen();
        onTerminalResize(new TerminalSize(width, height));
        registerForEvents();
    }

    private void clearScreen() {
        screenWriter.setBackgroundColor(Terminal.Color.BLACK);
        screenWriter.setForegroundColor(Terminal.Color.BLACK);
        screenWriter.fillScreen(' ');
        screen.refresh();
    }

    private Terminal.ResizeListener terminalResizeListener() {
        return new Terminal.ResizeListener() {
            @Override
            public void onResized(TerminalSize newSize) {
                onTerminalResize(newSize);
            }
        };
    }

    private void onTerminalResize(TerminalSize newSize) {
        TERMINAL_WIDTH = newSize.getColumns();
        TERMINAL_HEIGHT = newSize.getRows();
        PROGRESS_WIDTH = TERMINAL_WIDTH;
        PLAYLIST_DISPLAY_SIZE = TERMINAL_HEIGHT - 8;
        PLAYLIST_INDEX_WIDTH = 4;
        PLAYLIST_RATING_WIDTH = 10;
        PLAYLIST_TITLE_WIDTH = (TERMINAL_WIDTH - PLAYLIST_INDEX_WIDTH - PLAYLIST_RATING_WIDTH - 2) / 3;
        PLAYLIST_ARTIST_WIDTH = (TERMINAL_WIDTH - PLAYLIST_INDEX_WIDTH - PLAYLIST_RATING_WIDTH - 2) / 4;
        PLAYLIST_ALBUM_WIDTH = (TERMINAL_WIDTH - PLAYLIST_ARTIST_WIDTH - PLAYLIST_TITLE_WIDTH - PLAYLIST_INDEX_WIDTH - PLAYLIST_RATING_WIDTH - 8);
        displayEndIndex = displayBeginIndex + PLAYLIST_DISPLAY_SIZE;
        screen.refresh();
    }

    private void registerForEvents() {
        eventDispatcher.register(NowPlayingEvent.class, new EventDispatcher.Receiver<NowPlayingEvent>() {
            @Override
            public void receive(NowPlayingEvent event) {
                playlist = event.playlist;
                currentPlayingIndex = event.index;
                if (displayBeginIndex < currentPlayingIndex && currentPlayingIndex < displayEndIndex) {
                    displayBeginIndex = (displayEndIndex - currentPlayingIndex) < 5 && displayEndIndex < playlist.size() ? displayBeginIndex + 1 : displayBeginIndex;
                    displayEndIndex = displayBeginIndex + PLAYLIST_DISPLAY_SIZE;
                }
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
                String done = StringUtils.repeat("=", (int) ((event.newValue / event.duration) * (PROGRESS_WIDTH - 11)));
                String remaining = StringUtils.repeat("-", PROGRESS_WIDTH - done.length() - 11);
                setDefaultEntryColors();
                clearLine(PROGRESS_BAR_YPOS);
                screenWriter.drawString(1, PROGRESS_BAR_YPOS, "[" + done + "[" + formatTime(event.newValue) + "]" + remaining + "]");
                screen.refresh();
            }
        });
        
        eventDispatcher.register(CursorUpEvent.class, new EventDispatcher.Receiver<CursorUpEvent>() {
            @Override
            public void receive(CursorUpEvent event) {
                int scrollPosition = currentSelectedIndex - displayBeginIndex;
                currentSelectedIndex = (currentSelectedIndex > event.numLines) ? (currentSelectedIndex - event.numLines) : 0;
                displayBeginIndex = ((displayBeginIndex > (currentSelectedIndex - scrollPosition)) && (scrollPosition - event.numLines < 5)) ? currentSelectedIndex - scrollPosition : displayBeginIndex;
                displayBeginIndex = (displayBeginIndex < 0) ? 0 : displayBeginIndex;
                displayEndIndex = displayBeginIndex + PLAYLIST_DISPLAY_SIZE;
                updateScreen();
            }
        });

        eventDispatcher.register(CursorDownEvent.class, new EventDispatcher.Receiver<CursorDownEvent>() {
            @Override
            public void receive(CursorDownEvent event) {
                int scrollPosition = currentSelectedIndex - displayBeginIndex;
                currentSelectedIndex = (currentSelectedIndex < playlist.size() - event.numLines - 1) ? (currentSelectedIndex + event.numLines) : playlist.size() - 1;
                displayBeginIndex = ((displayBeginIndex < (currentSelectedIndex - scrollPosition)) && ((scrollPosition + event.numLines) > (PLAYLIST_DISPLAY_SIZE - 5))) ? currentSelectedIndex - scrollPosition : displayBeginIndex;
                displayBeginIndex = (displayBeginIndex + PLAYLIST_DISPLAY_SIZE > playlist.size()) ? playlist.size() - PLAYLIST_DISPLAY_SIZE : displayBeginIndex;
                displayBeginIndex = (displayBeginIndex < 0) ? 0 : displayBeginIndex;
                displayEndIndex = displayBeginIndex + PLAYLIST_DISPLAY_SIZE;
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
        drawNowPlaying();
        drawPlaylist();
        screen.refresh();
    }

    private void drawNowPlaying() {
        MetaData metaData = metaDataStore.get(playlist.get(currentPlayingIndex).getAbsolutePath());
        drawNowPlayingTrackInfo(metaData);
        drawNowPlayingRating(metaData);
    }

    private void drawNowPlayingTrackInfo(MetaData metaData) {
        setNowPlayingColors();
        clearLine(NOW_PLAYING_YPOS);
        String trackInfo = metaData.artist + " - " + metaData.title + " [" + metaData.album + "]" + " (" + formatTime(metaData.duration) + ")";
        screenWriter.drawString(1, NOW_PLAYING_YPOS, alignCentre(trackInfo, TERMINAL_WIDTH));
    }

    private String alignCentre(String string, int width) {
        if (string.length() > width) return string;
        int padding = width - string.length();
        return StringUtils.repeat(" ", padding/2) + string + StringUtils.repeat(" ", padding/2);
    }

    private void drawNowPlayingRating(MetaData metaData) {
        screenWriter.setBackgroundColor(Terminal.Color.BLACK);
        screenWriter.setForegroundColor(Terminal.Color.YELLOW);
        clearLine(NOW_PLAYING_YPOS + 1);
        screenWriter.drawString(1, NOW_PLAYING_YPOS + 1, alignCentre(getRatingAsString(metaData.rating), TERMINAL_WIDTH));
    }

    private String getRatingAsString(Integer rating) {
        String stars = StringUtils.repeat("\u2605 ", rating);
        return (rating < MAX_RATING) ? stars.concat(StringUtils.repeat("\u2606 ", MAX_RATING - rating)) : stars;
    }

    private String formatTime(Long durationSeconds) {
        return formatDuration(1000 * durationSeconds, "mm:ss");
    }

    private String formatTime(Double durationSeconds) {
        return formatTime(durationSeconds.longValue());
    }

    private void drawPlaylist(){
        displayEndIndex = (displayEndIndex < playlist.size()) ? displayEndIndex : playlist.size();
        for (int i = displayBeginIndex; i < displayEndIndex; i++) {
            int displayPos = PLAYLIST_YPOS + i - displayBeginIndex;
            File entry = playlist.get(i);
            MetaData metaData = metaDataStore.get(entry.getAbsolutePath());
            String entryDisplay = createEntryDisplay(i, metaData);
            ScreenCharacterStyle[] charStyle = setDefaultEntryColors();
            if (currentSelectedIndex == i){
                charStyle = setCurrentSelectedEntryColors();
            }
            if (i == currentPlayingIndex){
                charStyle = setCurrentPlayingEntryColors();
            }
            clearLine(displayPos);
            screenWriter.drawString(1, displayPos, entryDisplay, charStyle);
        }
    }

    private void clearLine(int y) {
        screenWriter.drawString(0, y, StringUtils.repeat(" ", TERMINAL_WIDTH));
    }

    private String createEntryDisplay(int index, MetaData metaData) {
        String format =
                "%" + PLAYLIST_INDEX_WIDTH + "d  " +
                "%-" + PLAYLIST_TITLE_WIDTH + "s " +
                "%-" + PLAYLIST_ARTIST_WIDTH + "s " +
                "%-" + PLAYLIST_ALBUM_WIDTH + "s " +
                "%-" + PLAYLIST_RATING_WIDTH + "s";
        return String.format(
                format,
                index+1,
                truncate(metaData.title, PLAYLIST_TITLE_WIDTH),
                truncate(metaData.artist, PLAYLIST_ARTIST_WIDTH),
                truncate(metaData.album, PLAYLIST_ALBUM_WIDTH),
                getRatingAsString(metaData.rating)
        );
    }

    private String truncate(String string, int maxWidth) {
        if (string.length() > maxWidth)
            return string.substring(0, maxWidth);
        return string;
    }

    private ScreenCharacterStyle[] setNowPlayingColors() {
        screenWriter.setForegroundColor(Terminal.Color.WHITE);
        screenWriter.setBackgroundColor(Terminal.Color.BLACK);
        return new ScreenCharacterStyle[]{ScreenCharacterStyle.Bold};
    }

    private ScreenCharacterStyle[] setCurrentSelectedEntryColors() {
        screenWriter.setBackgroundColor(Terminal.Color.WHITE);
        screenWriter.setForegroundColor(Terminal.Color.BLACK);
        return new ScreenCharacterStyle[]{};
    }

    private ScreenCharacterStyle[] setDefaultEntryColors() {
        screenWriter.setForegroundColor(Terminal.Color.WHITE);
        screenWriter.setBackgroundColor(Terminal.Color.BLACK);
        return new ScreenCharacterStyle[]{};
    }

    private ScreenCharacterStyle[] setCurrentPlayingEntryColors() {
        screenWriter.setForegroundColor(Terminal.Color.WHITE);
        return new ScreenCharacterStyle[]{ScreenCharacterStyle.Bold};
    }
}
