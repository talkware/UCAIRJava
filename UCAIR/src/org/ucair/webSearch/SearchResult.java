package org.ucair.webSearch;

import org.apache.commons.lang.builder.ToStringBuilder;

public class SearchResult {

    private final String url;

    private final String title;

    private final String summary;

    private final String displayedUrl;

    public SearchResult(final String url, final String title,
            final String summary, final String displayedUrl) {
        this.url = url;
        this.title = title;
        this.summary = summary;
        this.displayedUrl = displayedUrl;
    }

    public String getUrl() {
        return url;
    }

    public String getTitle() {
        return title;
    }

    public String getSummary() {
        return summary;
    }

    public String getDisplayUrl() {
        return displayedUrl;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("url", url)
                .append("title", title).append("summary", summary)
                .append("displayUrl", displayedUrl).toString();
    }
}
