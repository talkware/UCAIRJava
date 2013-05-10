package org.ucair.searchPage;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.ucair.core.Search;
import org.ucair.core.SearchExecutor;
import org.ucair.event.OnPageGenerated;
import org.ucair.event.OnResultClicked;
import org.ucair.event.SearchEventManager;
import org.ucair.webSearch.searchEngines.MockSearchEngine;
import org.ucair.webSearch.searchEngines.SearchEngineConfig;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "file:web/WEB-INF/ucair-app.xml" })
public class AdaptiveSearchPageGeneratorTest {

    @Inject
    private SearchExecutor searchExecutor;

    @Inject
    private SearchEngineConfig searchEngineConfig;

    @Inject
    private SearchEventManager searchEventManager;

    private AdaptiveSearchPageGenerator searchPageGenerator;

    /**
     * A ranker that ranks odd-positioned results before even-positioned
     * results.
     */
    private static class OddFirstSearchResultRanker implements
            SearchResultRanker {

        @Override
        public List<Integer> rank(final Search search,
                final Set<Integer> candidates) {
            final List<Integer> posList = new ArrayList<Integer>();
            for (int i = 1; i <= search.getDownloadedResultCount(); i += 2) {
                if (candidates.contains(i)) {
                    posList.add(i);
                }
            }
            for (int i = 2; i <= search.getDownloadedResultCount(); i += 2) {
                if (candidates.contains(i)) {
                    posList.add(i);
                }
            }
            return posList;
        }
    }

    @Before
    public void setUp() {
        searchEngineConfig.set(new MockSearchEngine());
        searchPageGenerator = new AdaptiveSearchPageGenerator();
        searchPageGenerator.setSearchEventManager(searchEventManager);
        searchPageGenerator.setRanker(new OddFirstSearchResultRanker());
    }

    @Test
    public void testSearchPageGenerator() {
        final Search search = searchExecutor.search("query");

        assertPosList(search, 1,
                Arrays.asList(1, 3, 5, 7, 9, 11, 13, 15, 17, 19));

        searchEventManager.addEvent(new OnPageGenerated(search, 1, Arrays
                .asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)));
        searchEventManager.addEvent(new OnResultClicked(search, 5));
        assertPosList(search, 1, Arrays.asList(1, 2, 3, 4, 5, 7, 9, 11, 13, 15));

        searchEventManager.addEvent(new OnPageGenerated(search, 1, Arrays
                .asList(1, 2, 3, 4, 5, 7, 9, 11, 13, 15)));
        searchEventManager.addEvent(new OnResultClicked(search, 4));
        assertPosList(search, 1, Arrays.asList(1, 2, 3, 4, 5, 7, 9, 11, 13, 15));
        assertPosList(search, 2,
                Arrays.asList(17, 19, 6, 8, 10, 12, 14, 16, 18, 20));

        searchEventManager.addEvent(new OnPageGenerated(search, 2, Arrays
                .asList(6, 8, 10, 12, 14, 16, 18, 17, 19, 20)));
        searchEventManager.addEvent(new OnResultClicked(search, 10));
        assertPosList(search, 1, Arrays.asList(1, 2, 3, 4, 5, 7, 9, 11, 13, 15));
        assertPosList(search, 2,
                Arrays.asList(6, 8, 10, 17, 19, 12, 14, 16, 18, 20));
    }

    private void assertPosList(final Search search, final int pageNum,
            final List<Integer> expectedPosList) {
        final List<SearchPageResult> results = searchPageGenerator.getItems(
                search, pageNum);
        Assert.assertEquals(expectedPosList,
                SearchPageResult.getPosList(results));
    }
}
