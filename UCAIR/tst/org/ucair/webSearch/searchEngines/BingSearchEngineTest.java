package org.ucair.webSearch.searchEngines;

import javax.annotation.Resource;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.ucair.webSearch.SearchEngine;
import org.ucair.webSearch.SearchRequest;
import org.ucair.webSearch.SearchResponse;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "file:web/WEB-INF/ucair-app.xml" })
public class BingSearchEngineTest {

	@Resource(name = "bingSearchEngine")
	private SearchEngine searchEngine;

	@Test
	public void test() throws Exception {
		testSearch("uiuc", 1, 10, 5, 10);
		testSearch("\u4e2d\u6587", 1, 10, 5, 10);
		testSearch("IDONTEXPECTANYRESULTFORTHISQUERYZZZ", 1, 10, 0, 0);
	}

	private void testSearch(final String query, final int startPos, final int requestedResultCount,
			final int expectedResultCountLow, final int expectedResultCountHigh) throws Exception {
		final SearchResponse response = searchEngine.search(new SearchRequest(query, startPos, requestedResultCount));
		Assert.assertTrue("Incorrect number of results", response.getResults().size() >= expectedResultCountLow
				&& response.getResults().size() <= expectedResultCountHigh);
	}
}
