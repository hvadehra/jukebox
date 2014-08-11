package org.melocine.events;

import com.google.common.base.Function;
import com.google.common.collect.MapMaker;

import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Created by IntelliJ IDEA.
 * User: hemanshu.v
 * Date: 8/9/14
 * Time: 1:41 AM
 * To change this template use File | Settings | File Templates.
 */
public class EventDispatcher {

    private final Map<Class<? extends Event>, Queue<Receiver<? extends Event>>> map;

    public EventDispatcher() {
        this.map = new MapMaker().makeComputingMap(new Function<Class<? extends Event>, Queue<Receiver<? extends Event>>>() {
            @Override
            public Queue<Receiver<? extends Event>> apply(Class<? extends Event> from) {
                return new ConcurrentLinkedQueue<Receiver<? extends Event>>();
            }
        });
    }


    public interface Event {
    }

    public <T extends Event> void dispatch(T event) {
        for (Receiver receiver : map.get(event.getClass())) {
            try {
                receiver.receive(event);
            } catch (Exception e) {
                System.err.println("Error During event dispatch: " + e);
            }
        }
    }

    public <T extends Event> void register(Class<T> eventClass, Receiver<T> receiver) {
        map.get(eventClass).add(receiver);
    }

    public interface Receiver<T extends Event> {
        public void receive(T event);
    }
}
