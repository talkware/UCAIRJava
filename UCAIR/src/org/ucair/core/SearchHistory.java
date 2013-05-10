package org.ucair.core;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Component;

@Component
public class SearchHistory {

    private final Map<String, Search> searchIdToSearch = new HashMap<String, Search>();

    private Search lastSearch;

    public synchronized Search getSearch(final String searchId) {
        return searchIdToSearch.get(searchId);
    }

    public synchronized Search newSearch(final String query) {
        final String searchId = String.format("%d.%d",
                System.currentTimeMillis(), System.nanoTime() % 1000);

        final Search search = new Search(searchId, query);

        if (lastSearch != null) {
            search.setPrevSearch(lastSearch);
            lastSearch.setNextSearch(search);
        }
        lastSearch = search;

        searchIdToSearch.put(searchId, search);
        return search;
    }
}
