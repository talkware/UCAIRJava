package org.ucair.searchPage;

import java.util.List;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.ucair.core.DisplayItem;
import org.ucair.core.Search;
import org.ucair.webSearch.SearchResult;

import com.google.common.base.Function;
import com.google.common.collect.Lists;

public class SearchPageResult implements DisplayItem {

    private static final String IMPORT_JSP_FILE_NAME = "/WEB-INF/search_result.jsp";

    private final String searchId;

    private final SearchResult result;

    private final int pos;

    private String cssClass;

    @Override
    public String getJspResource() {
        return IMPORT_JSP_FILE_NAME;
    }

    public SearchPageResult(final Search search, final int pos) {
        this.searchId = search.getSearchId();
        this.pos = pos;
        this.result = search.getResult(pos);
    }

    public String getUrl() {
        return result.getUrl();
    }

    public String getTitle() {
        return result.getTitle();
    }

    public String getSummary() {
        return result.getSummary();
    }

    public String getDisplayUrl() {
        return result.getDisplayUrl();
    }

    public String getSearchId() {
        return searchId;
    }

    public int getPos() {
        return pos;
    }

    public String getCssClass() {
        return cssClass == null ? "" : cssClass;
    }

    public void setCssClass(final String cssClass) {
        this.cssClass = cssClass;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("searchId", searchId)
                .append("pos", pos).toString();
    }

    public static List<Integer> getPosList(final List<SearchPageResult> results) {
        return Lists.transform(results,
                new Function<SearchPageResult, Integer>() {
                    @Override
                    public Integer apply(final SearchPageResult result) {
                        return result.getPos();
                    }
                });
    }
}
