package org.ucair.text;

import java.util.List;

import org.apache.commons.lang3.tuple.Pair;

public interface TermVectorSearcher {

    List<Pair<Integer, Double>> search(final TermVectorIndex index,
            TermVector query, int topK);
}
