package org.ucair.webSearch;

public class SearchRequest {

    private final String query;

    private final int startPos;

    private final int count;

    public SearchRequest(final String query, final int startPos, final int count) {
        this.query = query;
        this.startPos = startPos;
        this.count = count;
    }

    public String getQuery() {
        return query;
    }

    public int getStartPos() {
        return startPos;
    }

    public int getCount() {
        return count;
    }
}
