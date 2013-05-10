package org.ucair.searchPage;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Component;
import org.ucair.core.PaginationUtil;
import org.ucair.core.Search;
import org.ucair.event.OnPageGenerated;
import org.ucair.event.OnResultClicked;
import org.ucair.event.SearchEventManager;

@Component
public class AdaptiveSearchPageGenerator extends BaseSearchPageGenerator {

    private static final Log LOGGER = LogFactory
            .getLog(AdaptiveSearchPageGenerator.class);

    @Inject
    private SearchEventManager searchEventManager;

    @Inject
    private SearchResultRanker ranker;

    public void setSearchEventManager(
            final SearchEventManager searchEventManager) {
        this.searchEventManager = searchEventManager;
    }

    public void setRanker(final SearchResultRanker ranker) {
        this.ranker = ranker;
    }

    @Override
    public List<SearchPageResult> getItems(final Search search,
            final int pageNum) {
        if (search.getTotalPageCountEstimate() == 0) {
            return Collections.emptyList();
        }

        validatePageNum(search, pageNum);

        final Generator generator = new Generator(search, pageNum);

        final List<SearchPageResult> items = new ArrayList<SearchPageResult>();
        for (final int pos : generator.listResults()) {
            final SearchPageResult result = new SearchPageResult(search, pos);
            generator.setCssClass(result);
            items.add(result);
        }
        return items;
    }

    private class Generator {

        private final Search search;

        private final int pageNum;

        private int maxPageNum;

        private Map<Integer, OnPageGenerated> pageEvents;

        private Set<Integer> clickedResults;

        private List<Integer> seenResultsOnLastPage;

        private Set<Integer> allSeenResults;

        private Set<Integer> allUnseenResults;

        private List<Integer> rankedResults;

        public Generator(final Search search, final int pageNum) {
            this.search = search;
            this.pageNum = pageNum;
        }

        public List<Integer> listResults() {
            findGeneratedPages();

            if (pageNum < maxPageNum) {
                return pageEvents.get(pageNum).getPosList();
            }

            findClickedResults();

            findSeenResultsOnLastPage();

            findAllSeenResults();

            findAllUnseenResults();

            return listResultsOnLastPage();
        }

        public void setCssClass(final SearchPageResult result) {
            if (rankedResults != null
                    && rankedResults.contains(result.getPos())) {
                result.setCssClass("adaptive");
            }
        }

        private void findGeneratedPages() {
            maxPageNum = pageNum;
            pageEvents = new HashMap<Integer, OnPageGenerated>();
            for (final OnPageGenerated event : searchEventManager.getEvents(
                    search, OnPageGenerated.class)) {
                pageEvents.put(event.getPageNum(), event);
                if (event.getPageNum() > maxPageNum) {
                    maxPageNum = event.getPageNum();
                }
            }
        }

        private void findClickedResults() {
            clickedResults = new HashSet<Integer>();
            for (final OnResultClicked event : searchEventManager.getEvents(
                    search, OnResultClicked.class)) {
                clickedResults.add(event.getResultPos());
            }
        }

        private void findSeenResultsOnLastPage() {
            final OnPageGenerated event = pageEvents.get(maxPageNum);
            if (event != null) {
                int i;
                for (i = event.getPosList().size() - 1; i >= 0; i--) {
                    if (clickedResults.contains(event.getPosList().get(i))) {
                        break;
                    }
                }
                if (i != -1) {
                    seenResultsOnLastPage = event.getPosList()
                            .subList(0, i + 1);
                    return;
                }
            }
            seenResultsOnLastPage = Collections.emptyList();
        }

        private void findAllSeenResults() {
            allSeenResults = new HashSet<Integer>();
            for (final OnPageGenerated event : pageEvents.values()) {
                if (event.getPageNum() < maxPageNum) {
                    allSeenResults.addAll(event.getPosList());
                }
            }
            allSeenResults.addAll(seenResultsOnLastPage);
        }

        private void findAllUnseenResults() {
            allUnseenResults = new HashSet<Integer>();
            for (int i = 1; i <= search.getDownloadedResultCount(); ++i) {
                if (!allSeenResults.contains(i)) {
                    allUnseenResults.add(i);
                }
            }
        }

        private List<Integer> listResultsOnLastPage() {
            final List<Integer> results = new ArrayList<Integer>(
                    seenResultsOnLastPage);

            rankedResults = ranker.rank(search, allUnseenResults);

            results.addAll(combineNonRankedResults());

            if (results.size() > PaginationUtil.PAGE_SIZE) {
                results.subList(PaginationUtil.PAGE_SIZE, results.size())
                        .clear();
            }

            LOGGER.debug("Seen results: " + seenResultsOnLastPage);
            LOGGER.debug("Ranked results: " + rankedResults);

            return results;
        }

        private List<Integer> combineNonRankedResults() {
            final Set<Integer> results = new LinkedHashSet<Integer>(
                    rankedResults);
            for (int resultPos = 1; resultPos <= search
                    .getDownloadedResultCount(); ++resultPos) {
                if (!allSeenResults.contains(resultPos)) {
                    results.add(resultPos);
                }
            }
            return new ArrayList<Integer>(results);
        }
    }
}
