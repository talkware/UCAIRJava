package org.ucair.web;

import javax.inject.Inject;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.ucair.core.Search;
import org.ucair.core.SearchHistory;
import org.ucair.event.OnResultClicked;
import org.ucair.event.SearchEventManager;
import org.ucair.webSearch.SearchResult;

@Controller
public class ClickSearchResultsController {

    @Inject
    private SearchHistory searchHistory;

    @Inject
    private SearchEventManager searchEventManager;

    @RequestMapping("/click")
    public String search(@RequestParam(value = "search") final String searchId,
            @RequestParam(value = "pos") final int resultPos) {

        final Search search = searchHistory.getSearch(searchId);

        final SearchResult result = search.getResult(resultPos);

        searchEventManager.addEvent(new OnResultClicked(search, resultPos));

        return "redirect:" + result.getUrl();
    }
}
