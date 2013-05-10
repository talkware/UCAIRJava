package org.ucair.webSearch;


public interface SearchEngine {

    String getName();

    int getMaxBatchSize();

    String getSearchUrl(SearchRequest request);

    SearchResponse search(SearchRequest request);
}
