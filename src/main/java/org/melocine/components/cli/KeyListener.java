package org.melocine.components.cli;

import org.melocine.events.*;

import java.io.IOException;

/**
 * Created by IntelliJ IDEA.
 * User: hemanshu.v
 * Date: 8/7/14
 * Time: 2:39 AM
 * To change this template use File | Settings | File Templates.
 */

public class KeyListener{

    private final EventDispatcher eventDispatcher;
    private Mode mode;
    private String searchTerm;
    private String prevSearchTerm;
    public KeyListener(final EventDispatcher eventDispatcher){
        this.eventDispatcher = eventDispatcher;
        mode = Mode.control;
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        int tmp = System.in.read();
                        if (mode.equals(Mode.search) && tmp != 10){
                            char searchChar = (char) tmp;
                            System.out.print(searchChar);
                            searchTerm = searchTerm + searchChar;
                            continue;
                        }
                        switch (tmp){
                            case 47:
                                mode = Mode.search;
                                prevSearchTerm = searchTerm;
                                searchTerm = "";
                                System.out.print("/");
                                break;
                            case 3:
                                eventDispatcher.dispatch(new ShutdownEvent());
                                break;
                            case 10:
                                if (mode.equals(Mode.search)){
                                    mode = Mode.control;
                                    if (searchTerm.isEmpty()) searchTerm = prevSearchTerm;
                                    eventDispatcher.dispatch(new SearchEvent(searchTerm));
                                }
                                else
                                    eventDispatcher.dispatch(new ReturnKeyPressEvent());
                                break;
                            case 32:
                                eventDispatcher.dispatch(new TogglePlayPauseEvent());
                                break;
                            case 45:
                                eventDispatcher.dispatch(new VolumeDownEvent());
                                break;
                            case 61:
                                eventDispatcher.dispatch(new VolumeUpEvent());
                                break;
                            case 98:
                                eventDispatcher.dispatch(new PreviousTrackEvent());
                                break;
                            case 110:
                                eventDispatcher.dispatch(new NextTrackEvent());
                                break;
                            case 97:
                                eventDispatcher.dispatch(new CursorUpEvent(1));
                                break;
                            case 122:
                                eventDispatcher.dispatch(new CursorDownEvent(1));
                                break;
                            case 82:
                                eventDispatcher.dispatch(new RandomizePlaylistAfterCurrent());
                                break;
                            case 65:
                                eventDispatcher.dispatch(new CursorUpEvent(Display.PLAYLIST_DISPLAY_SIZE));
                                break;
                            case 90:
                                eventDispatcher.dispatch(new CursorDownEvent(Display.PLAYLIST_DISPLAY_SIZE));
                                break;
                            case 127:
                                eventDispatcher.dispatch(new RemoveSelectedTrackEvent());
                                break;
                            case 91:
                                eventDispatcher.dispatch(new SeekBackwardEvent());
                                break;
                            case 93:
                                eventDispatcher.dispatch(new SeekForwardEvent());
                                break;
                            default:
                                System.err.println("Unknown key pressed: " + tmp);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                        System.exit(1);
                    }
                }
            }
        }).start();
    }
    
    private static enum Mode{
        control, search;
    }
}