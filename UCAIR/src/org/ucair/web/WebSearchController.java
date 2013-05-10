package org.ucair.web;

import java.util.List;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.inject.Provider;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.ucair.core.PaginationUtil;
import org.ucair.core.Search;
import org.ucair.core.SearchExecutor;
import org.ucair.event.OnPageGenerated;
import org.ucair.event.SearchEventManager;
import org.ucair.searchPage.SearchPageGenerator;
import org.ucair.searchPage.SearchPageResult;

@Controller
public class WebSearchController {

    @Inject
    private SearchExecutor searchExecutor;

    @Inject
    private SearchEventManager searchEventManager;

    @Resource(name = "searchPageGeneratorConfig")
    private Provider<SearchPageGenerator> searchPageGeneratorProvider;

    @Inject
    private SearchModelWidgetMaker searchModelWidgetMaker;

    @RequestMapping("/search")
    public String search(
            @RequestParam(value = "search", required = false) final String searchId,
            @RequestParam(value = "query", required = false) final String query,
            @RequestParam(value = "page", required = false, defaultValue = "1") int pageNum,
            final Model model) {

        if (searchId == null && query != null) {
            final Search search = searchExecutor.search(query);
            return "redirect:/search?search=" + search.getSearchId();
        }

        if (searchId != null) {
            final Search search = searchExecutor.search(searchId, pageNum);

            if (pageNum > search.getRequestedPageCount()) {
                pageNum = search.getRequestedPageCount();
            }

            final SearchPageGenerator pageGenerator = searchPageGeneratorProvider
                    .get();
            final List<SearchPageResult> results = pageGenerator.getItems(
                    search, pageNum);

            searchEventManager.addEvent(new OnPageGenerated(search, pageNum,
                    SearchPageResult.getPosList(results)));

            final int startPos = PaginationUtil.getStartPos(pageNum);
            final int endPos = startPos + results.size() - 1;

            model.addAttribute(search);

            model.addAttribute("searchResults", results);

            model.addAttribute("startPos", startPos);
            model.addAttribute("endPos", endPos);

            if (pageGenerator.hasPage(search, pageNum - 1)) {
                model.addAttribute("prevPage", pageNum - 1);
            }
            if (pageGenerator.hasPage(search, pageNum + 1)) {
                model.addAttribute("nextPage", pageNum + 1);
            }

            model.addAttribute("searchModelWidget",
                    searchModelWidgetMaker.getWidget(search));
        }

        return "search";
    }

}
