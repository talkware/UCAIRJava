package org.ucair.core;

import org.junit.Assert;
import org.junit.Test;

public class SearchHistoryTest {

    @Test
    public void test() throws Exception {
        final SearchHistory history = new SearchHistory();

        final Search s1 = history.newSearch("s1");
        final Search s2 = history.newSearch("s2");

        Assert.assertEquals(s1, history.getSearch(s1.getSearchId()));
        Assert.assertEquals(s2, history.getSearch(s2.getSearchId()));

        Assert.assertNull(s1.getPrevSearch());
        Assert.assertEquals(s2, s1.getNextSearch());
        Assert.assertEquals(s1, s2.getPrevSearch());
        Assert.assertNull(s2.getNextSearch());
    }
}
