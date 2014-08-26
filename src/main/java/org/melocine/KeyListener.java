package org.melocine;

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

    public KeyListener(final EventDispatcher eventDispatcher){
        this.eventDispatcher = eventDispatcher;
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        int tmp = System.in.read();
                        System.err.println("Key pressed: " + tmp);
                        switch (tmp){
                            case 3:
                                eventDispatcher.dispatch(new ShutdownEvent());
                                break;
                            case 10:
                                eventDispatcher.dispatch(new ReturnKeyPressEvent());
                                break;
                            case 32:
                                eventDispatcher.dispatch(new TogglePlayPauseEvent());
                                break;
                            case 98:
                                eventDispatcher.dispatch(new PreviousTrackEvent());
                                break;
                            case 110:
                                eventDispatcher.dispatch(new NextTrackEvent());
                                break;
                            case 97:
                                eventDispatcher.dispatch(new CursorUpEvent());
                                break;
                            case 122:
                                eventDispatcher.dispatch(new CursorDownEvent());
                                break;
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                        System.exit(1);
                    }
                }
            }
        }).start();
    }
}