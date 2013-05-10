package org.ucair.searchPage;

import javax.inject.Inject;
import javax.inject.Provider;

import org.springframework.stereotype.Component;
import org.ucair.core.UCAIRConfig;
import org.ucair.util.SpringUtil;

@Component
public class SearchPageGeneratorConfig implements Provider<SearchPageGenerator> {

    @Inject
    private UCAIRConfig config;

    private SearchPageGenerator searchPageGenerator;

    @Override
    public SearchPageGenerator get() {
        if (searchPageGenerator == null) {
            final String beanName = config.get("search_page_generator");
            searchPageGenerator = SpringUtil.getContext().getBean(beanName,
                    SearchPageGenerator.class);
        }
        return searchPageGenerator;
    }

    public void set(final SearchPageGenerator searchPageGenerator) {
        this.searchPageGenerator = searchPageGenerator;
    }

    public void set(final String beanName) {
        searchPageGenerator = SpringUtil.getContext().getBean(beanName,
                SearchPageGenerator.class);
        config.set("search_page_generator", beanName);
    }
}
