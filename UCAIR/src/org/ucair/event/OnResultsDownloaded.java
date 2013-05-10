package org.ucair.event;

import org.ucair.core.Search;

public class OnResultsDownloaded extends SearchEvent {

    public OnResultsDownloaded(final Search search) {
        super(search);
    }
}
