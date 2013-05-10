package org.ucair.searchModel;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.ucair.core.Search;
import org.ucair.event.OnResultClicked;
import org.ucair.event.SearchEventManager;
import org.ucair.text.CollectionModel;
import org.ucair.text.MixtureEstimator;
import org.ucair.text.TermVector;
import org.ucair.text.TextUtil;
import org.ucair.webSearch.SearchResult;

@Component
public class WeightedClickModelGenerator implements SearchModelGenerator {

    public static final String MODEL_NAME = "weighted_click_model";

    @Inject
    private SearchEventManager searchEventManager;

    @Inject
    private CollectionModel collectionModel;

    @Value("#{appConfig.get('weighted_click_model.query_weight')}")
    private double queryWeight;

    @Value("#{appConfig.get('weighted_click_model.clicked_result_weight')}")
    private double clickedResultWeight;

    @Value("#{appConfig.get('mixture_model.background_weight')}")
    private double bgWeight;

    @Value("#{appConfig.get('language_model.truncate_term_count')}")
    private int truncateMaxTermCount;

    @Value("#{appConfig.get('language_model.truncate_term_prob')}")
    private double truncateMinTermProb;

    @Override
    public SearchModel getModel(final Search search) {
        SearchModel searchModel = search.getAttr(MODEL_NAME);
        if (searchModel == null || isModelOutdated(search, searchModel)) {
            searchModel = generateNewModel(search);
            search.setAttr(MODEL_NAME, searchModel);
        }
        return searchModel;
    }

    private boolean isModelOutdated(final Search search,
            final SearchModel searchModel) {
        for (final OnResultClicked event : searchEventManager.getEvents(search,
                OnResultClicked.class)) {
            if (event.getTimestamp().after(searchModel.getTimestamp())) {
                return true;
            }
        }
        return false;
    }

    private SearchModel generateNewModel(final Search search) {
        final Map<String, Double> termWeights = new HashMap<String, Double>();

        for (final String term : TextUtil.tokenize(search.getQuery())) {
            TextUtil.increment(termWeights, term, queryWeight);
        }

        final Set<Integer> clickedPosList = new HashSet<Integer>();

        for (final OnResultClicked event : searchEventManager.getEvents(search,
                OnResultClicked.class)) {
            clickedPosList.add(event.getResultPos());
        }

        for (final int resultPos : clickedPosList) {
            final SearchResult result = search.getResult(resultPos);
            for (final String term : TextUtil.tokenize(result.getTitle() + " "
                    + result.getSummary())) {
                TextUtil.increment(termWeights, term, clickedResultWeight);
            }
        }

        final TermVector weighted = new TermVector(termWeights);
        final TermVector estimated = MixtureEstimator.estimateMixtureComponent(
                weighted, collectionModel, bgWeight);
        final TermVector truncated = estimated.truncate(truncateMaxTermCount,
                truncateMinTermProb);

        final SearchModel searchModel = new SearchModel(MODEL_NAME, truncated);
        searchModel.setAdaptive(!clickedPosList.isEmpty());
        return searchModel;
    }
}
