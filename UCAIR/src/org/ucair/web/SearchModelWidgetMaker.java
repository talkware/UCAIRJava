package org.ucair.web;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.annotation.Resource;
import javax.inject.Provider;

import org.springframework.stereotype.Component;
import org.ucair.core.DisplayItem;
import org.ucair.core.Search;
import org.ucair.searchModel.SearchModel;
import org.ucair.searchModel.SearchModelGenerator;

@Component
public class SearchModelWidgetMaker {

    @Resource(name = "searchModelGeneratorConfig")
    private Provider<SearchModelGenerator> searchModelGeneratorProvider;

    public DisplayItem getWidget(final Search search) {
        final SearchModelGenerator searchModelGenerator = searchModelGeneratorProvider
                .get();
        final SearchModel searchModel = searchModelGenerator.getModel(search);
        return new Widget(searchModel);
    }

    public static class Widget implements DisplayItem {

        private static final String IMPORT_JSP_FILE_NAME = "/WEB-INF/search_model_widget.jsp";

        private SearchModel model;

        private final List<Map.Entry<String, Double>> terms;

        public Widget(final SearchModel model) {
            this.model = model;
            terms = new ArrayList<Map.Entry<String, Double>>(model
                    .getTermVector().getValues().entrySet());

            Collections.sort(terms,
                    new Comparator<Map.Entry<String, Double>>() {

                        @Override
                        public int compare(final Entry<String, Double> o1,
                                final Entry<String, Double> o2) {
                            return -o1.getValue().compareTo(o2.getValue());
                        }
                    });
        }

        @Override
        public String getJspResource() {
            return IMPORT_JSP_FILE_NAME;
        }

        public String getModelName() {
            return model.getName();
        }

        public List<Map.Entry<String, Double>> getModel() {
            return terms;
        }
    }
}
