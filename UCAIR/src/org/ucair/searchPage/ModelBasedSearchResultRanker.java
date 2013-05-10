package org.ucair.searchPage;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.inject.Provider;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.ucair.core.Search;
import org.ucair.core.SearchIndexManager;
import org.ucair.searchModel.SearchModel;
import org.ucair.searchModel.SearchModelGenerator;
import org.ucair.text.TermVectorIndex;
import org.ucair.text.TermVectorSearcher;

@Component
public class ModelBasedSearchResultRanker implements SearchResultRanker {

    private static final Log LOGGER = LogFactory
            .getLog(ModelBasedSearchResultRanker.class);

    @Resource(name = "searchModelGeneratorConfig")
    private Provider<SearchModelGenerator> searchModelGeneratorProvider;

    @Inject
    private SearchIndexManager searchIndexManager;

    @Inject
    private TermVectorSearcher searcher;

    @Value("#{appConfig.get('adaptive_search.cutoff')}")
    private int cutoff;

    @Value("#{appConfig.get('adaptive_search.use_non_adaptive_model')}")
    private boolean useNonAdaptiveModel;

    @Override
    public List<Integer> rank(final Search search, final Set<Integer> candidates) {
        final SearchModelGenerator searchModelGenerator = searchModelGeneratorProvider
                .get();
        final SearchModel model = searchModelGenerator.getModel(search);
        LOGGER.info("Search model: " + model);

        final TermVectorIndex index = searchIndexManager.getIndex(search);

        if (!model.isAdaptive() && !useNonAdaptiveModel) {
            return Collections.emptyList();

        }

        final List<Pair<Integer, Double>> scores = searcher.search(index,
                model.getTermVector(), -1);

        final List<Integer> results = new ArrayList<Integer>();

        for (final Pair<Integer, Double> pair : scores) {
            final int pos = pair.getLeft();
            if (candidates.contains(pos)) {
                results.add(pos);
                if (results.size() == cutoff) {
                    break;
                }
            }
        }

        return results;
    }
}
