package org.ucair.webSearch;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang.builder.ToStringBuilder;

public class SearchResponse {

    private final String query;

    private final List<SearchResult> results = new ArrayList<SearchResult>();

    private String spellCorrection;

    private long totalResultCountEstimate = 0;

    public SearchResponse(final String query) {
        this.query = query;
    }

    public String getQuery() {
        return query;
    }

    public List<SearchResult> getResults() {
        return Collections.unmodifiableList(results);
    }

    public void addResult(final SearchResult result) {
        results.add(result);
    }

    public long getTotalResultCountEstimate() {
        return totalResultCountEstimate;
    }

    public void setTotalResultCountEstimate(final long totalResultCountEstimate) {
        this.totalResultCountEstimate = totalResultCountEstimate;
    }

    public String getSpellCorrection() {
        return spellCorrection;
    }

    public void setSpellCorrection(final String spellCorrection) {
        this.spellCorrection = spellCorrection;
    }

    @Override
    public String toString() {
        final ToStringBuilder builder = new ToStringBuilder(this)
                .append("query", query).append("results", results.size())
                .append("totalResultCountEstimate", totalResultCountEstimate);
        if (spellCorrection != null) {
            builder.append("spellCorrection", spellCorrection);
        }
        return builder.toString();
    }
}
