package org.melocine.events;

import java.io.File;
import java.util.Collection;

/**
 * Created by IntelliJ IDEA.
 * User: hemanshu.v
 * Date: 8/12/14
 * Time: 10:16 PM
 * To change this template use File | Settings | File Templates.
 */
public class PlayAllEvent implements EventDispatcher.Event {
    public final Collection<File> files;

    public PlayAllEvent(Collection<File> files) {
        this.files = files;
    }
}
