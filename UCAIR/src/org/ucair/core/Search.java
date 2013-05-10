package org.ucair.core;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.ucair.webSearch.SearchResponse;
import org.ucair.webSearch.SearchResult;

public class Search {

    private final String searchId;

    private Search prevSearch;

    private Search nextSearch;

    private final String query;

    private final Date startTime;

    private final List<SearchResult> results = new ArrayList<SearchResult>();

    private String spellCorrection;

    private long totalResultCountEstimate = 1000;

    private int downloadedPageCount = 0;

    private int requestedPageCount = 0;

    private final Map<String, Object> attrs = new HashMap<String, Object>();

    Search(final String searchId, final String query) {
        this.searchId = searchId;
        this.query = query;
        startTime = new Date();
    }

    public String getSearchId() {
        return searchId;
    }

    public String getQuery() {
        return query;
    }

    public Date getStartTime() {
        return startTime;
    }

    public synchronized int getDownloadedResultCount() {
        return results.size();
    }

    public synchronized SearchResult getResult(final int pos) {
        return results.get(pos - 1);
    }

    public synchronized long getTotalResultCountEstimate() {
        return totalResultCountEstimate;
    }

    public synchronized int getTotalPageCountEstimate() {
        return PaginationUtil.getPageNum(totalResultCountEstimate);
    }

    public synchronized String getSpellCorrection() {
        return spellCorrection;
    }

    public synchronized int getDownloadedPageCount() {
        return downloadedPageCount;
    }

    public synchronized void incDownloadedPageCount(final int count) {
        downloadedPageCount += count;
        if (downloadedPageCount > getTotalPageCountEstimate()) {
            downloadedPageCount = getTotalPageCountEstimate();
        }
    }

    public synchronized int getRequestedPageCount() {
        return requestedPageCount;
    }

    public synchronized void incRequestedPageCount(final int count) {
        requestedPageCount += count;
        if (requestedPageCount > getTotalPageCountEstimate()) {
            requestedPageCount = getTotalPageCountEstimate();
        }
    }

    public synchronized void update(final SearchResponse response) {
        totalResultCountEstimate = response.getTotalResultCountEstimate();
        if (response.getSpellCorrection() != null) {
            spellCorrection = response.getSpellCorrection();
        }

        results.addAll(response.getResults());
    }

    public synchronized Search getPrevSearch() {
        return prevSearch;
    }

    public synchronized void setPrevSearch(final Search prevSearch) {
        this.prevSearch = prevSearch;
    }

    public synchronized Search getNextSearch() {
        return nextSearch;
    }

    public synchronized void setNextSearch(final Search nextSearch) {
        this.nextSearch = nextSearch;
    }

    @SuppressWarnings("unchecked")
    public synchronized <E> E getAttr(final String key) {
        return (E) attrs.get(key);
    }

    public synchronized void setAttr(final String key, final Object value) {
        attrs.put(key, value);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("searchId", searchId)
                .append("query", query).toString();
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj == this) {
            return true;
        }
        if (obj.getClass() != getClass()) {
            return false;
        }
        final Search other = (Search) obj;
        return new EqualsBuilder().appendSuper(super.equals(obj))
                .append(searchId, other.searchId).isEquals();
    }

    @Override
    public int hashCode() {
        throw new UnsupportedOperationException("Please hash on search id");
    }
}
