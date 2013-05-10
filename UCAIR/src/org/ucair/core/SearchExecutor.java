package org.ucair.core;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.inject.Provider;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Component;
import org.ucair.event.OnResultsDownloaded;
import org.ucair.event.SearchEventManager;
import org.ucair.webSearch.SearchEngine;
import org.ucair.webSearch.SearchRequest;
import org.ucair.webSearch.SearchResponse;

import com.google.common.base.Preconditions;

@Component
public class SearchExecutor {

    private static final Log LOGGER = LogFactory.getLog(SearchExecutor.class);

    @Inject
    private SearchHistory searchHistory;

    @Inject
    private SearchEventManager searchEventManager;

    @Resource(name = "searchEngineConfig")
    private Provider<SearchEngine> searchEngineProvider;

    public synchronized Search search(final String query) {
        final Search search = searchHistory.newSearch(query);
        return search(search.getSearchId(), 1);
    }

    public synchronized Search search(final String searchId, final int pageNum) {
        Preconditions.checkArgument(pageNum > 0, "Invalid page num: %s",
                pageNum);

        final Search search = searchHistory.getSearch(searchId);

        if (pageNum > search.getRequestedPageCount()) {
            downloadPages(search);

            search.incRequestedPageCount(1);
        }

        return search;
    }

    private void downloadPages(final Search search) {
        if (search.getDownloadedPageCount() >= search
                .getTotalPageCountEstimate()) {
            return;
        }

        final SearchEngine searchEngine = searchEngineProvider.get();

        final int startPos = PaginationUtil.getStartPos(search
                .getDownloadedPageCount() + 1);
        final int resultCount = searchEngine.getMaxBatchSize();

        LOGGER.info(String.format("Query: %s, start: %d, count: %d",
                search.getQuery(), startPos, resultCount));

        final SearchRequest searchRequest = new SearchRequest(
                search.getQuery(), startPos, resultCount);
        final SearchResponse searchResponse = searchEngine
                .search(searchRequest);

        search.update(searchResponse);

        search.incDownloadedPageCount(PaginationUtil.getPageNum(resultCount));

        searchEventManager.addEvent(new OnResultsDownloaded(search));
    }
}
