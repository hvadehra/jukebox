package org.melocine.events;

import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by IntelliJ IDEA.
 * User: hemanshu.v
 * Date: 8/9/14
 * Time: 1:41 AM
 * To change this template use File | Settings | File Templates.
 */
public class EventDispatcher {

    private Map<String, ArrayList<EventRunnable>> listeners = new ConcurrentHashMap<String, ArrayList<EventRunnable>>();

    public <T extends Event> void register(Class<T> event, EventRunnable eventRunnable){
        String key = event.getCanonicalName();
        if (!listeners.containsKey(key)) {
            listeners.put(key, new ArrayList<EventRunnable>());
        }
        listeners.get(key).add(eventRunnable);
    }

    public <T extends Event> void dispatch(T event){
        String key = event.getClass().getCanonicalName();
        for (EventRunnable<T> eventRunnable : listeners.get(key)) {
            eventRunnable.run(event);
        }
    }

    public interface Event{

    }
}
