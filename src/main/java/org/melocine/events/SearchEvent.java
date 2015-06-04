package org.melocine.events;

/**
 * Created by IntelliJ IDEA.
 * User: hemanshu.v
 * Date: 8/27/14
 * Time: 4:13 AM
 * To change this template use File | Settings | File Templates.
 */
public class SearchEvent implements EventDispatcher.Event{
    private final String searchTerm;

    public SearchEvent(String searchTerm) {
        this.searchTerm = searchTerm;
    }

    public String getSearchTerm() {
        return searchTerm;
    }
}
