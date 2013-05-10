package org.ucair.webSearch.searchEngines;

import org.springframework.stereotype.Component;
import org.ucair.webSearch.SearchEngine;
import org.ucair.webSearch.SearchRequest;
import org.ucair.webSearch.SearchResponse;
import org.ucair.webSearch.SearchResult;

@Component("mockSearchEngine")
public class MockSearchEngine implements SearchEngine {

    public static final int BATCH_SIZE = 20;

    public static final int TOTAL_RESULT_COUNT = 50;

    public static final String NO_RESULT_QUERY = "expect no result";

    @Override
    public String getName() {
        return "Mock";
    }

    @Override
    public int getMaxBatchSize() {
        return BATCH_SIZE;
    }

    @Override
    public String getSearchUrl(final SearchRequest request) {
        return "about:blank";
    }

    @Override
    public SearchResponse search(final SearchRequest request) {
        final SearchResponse response = new SearchResponse(request.getQuery());

        if (request.getQuery().equals(NO_RESULT_QUERY)) {
            response.setTotalResultCountEstimate(0);
            return response;
        }

        response.setTotalResultCountEstimate(TOTAL_RESULT_COUNT);
        for (int pos = request.getStartPos(); pos < request.getStartPos()
                + request.getCount()
                && pos <= TOTAL_RESULT_COUNT; ++pos) {
            final SearchResult result = new SearchResult("http://www.uiuc.edu",
                    "title-" + pos, "summary-" + pos, "url-" + pos);
            response.addResult(result);
        }
        return response;
    }
}
