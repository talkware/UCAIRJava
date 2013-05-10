package org.ucair.searchPage;

import java.util.List;
import java.util.Set;

import org.ucair.core.Search;

public interface SearchResultRanker {

    List<Integer> rank(Search search, Set<Integer> candidates);
}
