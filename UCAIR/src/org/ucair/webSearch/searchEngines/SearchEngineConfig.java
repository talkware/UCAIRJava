package org.ucair.webSearch.searchEngines;

import javax.inject.Inject;
import javax.inject.Provider;

import org.springframework.stereotype.Component;
import org.ucair.core.UCAIRConfig;
import org.ucair.util.SpringUtil;
import org.ucair.webSearch.SearchEngine;

@Component
public class SearchEngineConfig implements Provider<SearchEngine> {

    @Inject
    private UCAIRConfig config;

    private SearchEngine searchEngine;

    @Override
    public SearchEngine get() {
        if (searchEngine == null) {
            final String beanName = config.get("search_engine");
            searchEngine = SpringUtil.getContext().getBean(beanName,
                    SearchEngine.class);
        }
        return searchEngine;
    }

    public void set(final SearchEngine searchEngine) {
        this.searchEngine = searchEngine;
    }

    public void set(final String beanName) {
        searchEngine = SpringUtil.getContext().getBean(beanName,
                SearchEngine.class);
        config.set("search_engine", beanName);
    }
}
