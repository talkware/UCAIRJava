package org.ucair.event;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Component;
import org.ucair.core.Search;

import com.google.common.collect.ImmutableList;

@Component
public class SearchEventManager {

    private static final Log LOGGER = LogFactory
            .getLog(SearchEventManager.class);

    @Resource(name = "listeners")
    private List<EventListener> listeners;

    public void setListeners(final List<EventListener> listeners) {
        this.listeners = listeners;
    }

    private void setSearchEvents(final Search search,
            final List<SearchEvent> events) {
        search.setAttr("searchEvents", events);
    }

    private List<SearchEvent> getSearchEvents(final Search search) {
        return search.getAttr("searchEvents");
    }

    public synchronized void addEvent(final SearchEvent event) {
        LOGGER.info(event);

        List<SearchEvent> events = getSearchEvents(event.getSearch());
        if (events == null) {
            events = new ArrayList<SearchEvent>();
            setSearchEvents(event.getSearch(), events);
        }
        events.add(event);

        for (final EventListener listener : listeners) {
            listener.handle(event);
        }
    }

    public synchronized List<SearchEvent> getEvents(final Search search) {
        final List<SearchEvent> events = getSearchEvents(search);
        if (events == null) {
            return Collections.emptyList();
        }
        return ImmutableList.copyOf(events);
    }

    public synchronized <E extends SearchEvent> List<E> getEvents(
            final Search search, final Class<E> eventType) {
        final List<SearchEvent> events = getSearchEvents(search);
        if (events == null) {
            return Collections.emptyList();
        }
        final List<E> results = new ArrayList<E>();
        for (final SearchEvent event : events) {
            if (eventType.isInstance(event)) {
                results.add(eventType.cast(event));
            }
        }
        return results;
    }
}
