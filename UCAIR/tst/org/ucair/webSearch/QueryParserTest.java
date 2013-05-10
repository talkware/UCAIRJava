package org.ucair.webSearch;

import org.apache.lucene.search.Query;
import org.junit.Assert;
import org.junit.Test;

public class QueryParserTest {

    @Test
    public void testQueryParser() {
        final String text = "IR \"text mining\" -DB +\"machine learning\" site:www.uiuc.edu";
        final QueryParser parser = new QueryParser();
        final Query query = parser.parse("", text);
        Assert.assertEquals(
                "ir \"text mine\" -db +\"machin learn\" +site:www.uiuc.edu",
                query.toString());
    }
}
