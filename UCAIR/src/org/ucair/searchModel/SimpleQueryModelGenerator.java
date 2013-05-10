package org.ucair.searchModel;

import java.util.List;

import org.springframework.stereotype.Component;
import org.ucair.core.Search;
import org.ucair.text.TermVector;
import org.ucair.text.TextUtil;

@Component
public class SimpleQueryModelGenerator implements SearchModelGenerator {

    public static final String MODEL_NAME = "simple_query_model";

    @Override
    public SearchModel getModel(final Search search) {
        SearchModel searchModel = search.getAttr(MODEL_NAME);
        if (searchModel == null) {
            searchModel = generateNewModel(search);
            search.setAttr(MODEL_NAME, searchModel);
        }
        return searchModel;
    }

    private SearchModel generateNewModel(final Search search) {
        final List<String> tokens = TextUtil.tokenize(search.getQuery());
        final SearchModel model = new SearchModel(MODEL_NAME, new TermVector(
                tokens));
        model.setAdaptive(false);
        return model;
    }
}
