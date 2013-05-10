package org.ucair.event;

import java.util.Date;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.ucair.core.Search;

public abstract class SearchEvent {

    protected Search search;

    protected Date timestamp;

    protected SearchEvent(final Search search) {
        this.search = search;
        timestamp = new Date();
    }

    public Search getSearch() {
        return search;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("search", search.getSearchId())
                .toString();
    }
}
