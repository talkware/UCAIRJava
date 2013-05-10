package org.ucair.core;

import javax.inject.Inject;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.ucair.webSearch.searchEngines.MockSearchEngine;
import org.ucair.webSearch.searchEngines.SearchEngineConfig;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "file:web/WEB-INF/ucair-app.xml" })
public class SearchExecutorTest {

    @Inject
    private SearchEngineConfig searchEngineConfig;

    @Inject
    private SearchExecutor searchExecutor;

    @Before
    public void setUp() {
        searchEngineConfig.set(new MockSearchEngine());
    }

    @Test
    public void testSearchExecutor() {
        final Search search = searchExecutor.search("query");
        assertSearch(search, 20, 2, 1);

        searchExecutor.search(search.getSearchId(), 1);
        assertSearch(search, 20, 2, 1);

        searchExecutor.search(search.getSearchId(), 3);
        assertSearch(search, 40, 4, 2);

        searchExecutor.search(search.getSearchId(), 2);
        assertSearch(search, 40, 4, 2);

        searchExecutor.search(search.getSearchId(), 3);
        assertSearch(search, 50, 5, 3);

        searchExecutor.search(search.getSearchId(), 4);
        assertSearch(search, 50, 5, 4);

        searchExecutor.search(search.getSearchId(), 5);
        assertSearch(search, 50, 5, 5);

        searchExecutor.search(search.getSearchId(), 6);
        assertSearch(search, 50, 5, 5);
    }

    private void assertSearch(final Search search,
            final int downloadedResultCount, final int downloadedPageCount,
            final int requestedPageCount) {
        Assert.assertEquals(downloadedResultCount,
                search.getDownloadedResultCount());
        Assert.assertEquals(downloadedPageCount,
                search.getDownloadedPageCount());
        Assert.assertEquals(requestedPageCount, search.getRequestedPageCount());
    }
}
