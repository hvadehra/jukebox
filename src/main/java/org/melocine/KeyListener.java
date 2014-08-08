package org.melocine;

import org.melocine.events.EventDispatcher;
import org.melocine.events.NextTrackEvent;
import org.melocine.events.PreviousTrackEvent;
import org.melocine.events.TogglePlayPauseEvent;

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
                        switch (tmp){
                            case 3:
                                System.exit(1);
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