package org.ucair.searchPage;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.ucair.core.PaginationUtil;
import org.ucair.core.Search;
import org.ucair.core.SearchExecutor;
import org.ucair.webSearch.searchEngines.MockSearchEngine;
import org.ucair.webSearch.searchEngines.SearchEngineConfig;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "file:web/WEB-INF/ucair-app.xml" })
public class BaseSearchPageGeneratorTest {

    @Inject
    private SearchExecutor searchExecutor;

    @Inject
    private SearchEngineConfig searchEngineConfig;

    private final SearchPageGenerator pageGenerator = new BaseSearchPageGenerator();

    @Before
    public void setUp() {
        searchEngineConfig.set(new MockSearchEngine());
    }

    @Test
    public void testValidPageNum() {
        final Search search = searchExecutor.search("query");

        for (int pageNum = 1; pageNum < search.getDownloadedPageCount(); ++pageNum) {
            testValidPageNum(search, pageNum);
        }
    }

    private void testValidPageNum(final Search search, final int pageNum) {
        Assert.assertTrue(pageGenerator.hasPage(search, pageNum));

        final int startPos = PaginationUtil.getStartPos(pageNum);
        final List<SearchPageResult> results = pageGenerator.getItems(search,
                startPos);
        Assert.assertEquals(getPosList(startPos, PaginationUtil.PAGE_SIZE),
                SearchPageResult.getPosList(results));
    }

    @Test
    public void testInvalidPageNum() {
        final Search search = searchExecutor.search("query");

        testInvalidPageNum(search, 0);
        testInvalidPageNum(search, search.getDownloadedPageCount() + 1);
    }

    private void testInvalidPageNum(final Search search, final int pageNum) {
        if (pageNum > 0) {
            Assert.assertTrue(pageGenerator.hasPage(search, pageNum));
        } else {
            Assert.assertFalse(pageGenerator.hasPage(search, pageNum));
        }

        final int startPos = PaginationUtil.getStartPos(pageNum);
        try {
            pageGenerator.getItems(search, startPos);
            Assert.fail();
        } catch (final Exception e) {
            // expected
        }
    }

    private static List<Integer> getPosList(final int start, final int count) {
        final List<Integer> posList = new ArrayList<Integer>();
        for (int i = start; i < start + count; ++i) {
            posList.add(i);
        }
        return posList;
    }
}
