package org.ucair.text;

import java.util.Collections;
import java.util.List;

import org.apache.commons.lang3.tuple.Pair;
import org.junit.Assert;
import org.junit.Test;

public class TermVectorSearcherTest {

    private final String[] lines = { "RIVER SNOW",
            "A hundred mountains and no bird,",
            "A thousand paths without a footprint;",
            "A little boat, a bamboo cloak,",
            "An old man fishing in the cold river snow.", "" };

    private TermVectorIndex index;

    @Test
    public void testInMemoryIndexAndDirichletPriorSearcher() {
        index = new TermVectorIndexInMemoryImpl();
        testIndex(index);

        final CollectionModel mockCollectionModel = new CollectionModelImpl(
                1000, 1000, Collections.<String, Integer> emptyMap());
        final TermVectorSearcherDirichletPriorImpl searcher = new TermVectorSearcherDirichletPriorImpl();
        searcher.setCollectionModel(mockCollectionModel);
        testSearcher(searcher);
    }

    private void testIndex(final TermVectorIndex index) {
        int id = 1;
        for (final String line : lines) {
            final List<String> tokens = TextUtil.tokenize(line);
            final TermVector termVector = new TermVector(tokens);
            index.addDoc(id++, termVector);
        }

        Assert.assertEquals(6, index.getDocCount());
    }

    private void testSearcher(final TermVectorSearcher searcher) {
        assertResultCount(searcher, "snow", 10, 2);
        assertResultCount(searcher, "snow", 1, 1);
        assertResultCount(searcher, "mountain", 0, 1);
        assertResultCount(searcher, "bamboo cloak", 0, 1);
        assertResultCount(searcher, "ice", 0, 0);
    }

    private void assertResultCount(final TermVectorSearcher searcher,
            final String queryStr, final int topK, final int expectedResultCount) {
        final List<String> tokens = TextUtil.tokenize(queryStr);
        final TermVector query = new TermVector(tokens);
        final List<Pair<Integer, Double>> results = searcher.search(index,
                query, topK);
        Assert.assertEquals("Wrong result count", expectedResultCount,
                results.size());
    }
}
