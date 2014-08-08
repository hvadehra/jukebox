package org.melocine.events;

import org.melocine.MetaData;

/**
 * Created by IntelliJ IDEA.
 * User: hemanshu.v
 * Date: 8/9/14
 * Time: 1:51 AM
 * To change this template use File | Settings | File Templates.
 */
public class ScrobbleTrackEvent implements EventDispatcher.Event {
    private final MetaData metaData;

    public ScrobbleTrackEvent(MetaData metaData) {

        this.metaData = metaData;
    }

    public MetaData getMetaData() {
        return metaData;
    }
}
