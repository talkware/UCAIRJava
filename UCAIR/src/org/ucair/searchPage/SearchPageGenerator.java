package org.ucair.searchPage;

import java.util.List;

import org.ucair.core.Search;

public interface SearchPageGenerator {

    boolean hasPage(Search search, int pageNum);

    List<SearchPageResult> getItems(Search search, int pageNum);
}
