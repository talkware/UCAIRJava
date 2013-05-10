package org.ucair.searchModel;

import javax.inject.Inject;
import javax.inject.Provider;

import org.springframework.stereotype.Component;
import org.ucair.core.UCAIRConfig;
import org.ucair.util.SpringUtil;

@Component
public class SearchModelGeneratorConfig implements
        Provider<SearchModelGenerator> {

    @Inject
    private UCAIRConfig config;

    private SearchModelGenerator searchModelGenerator;

    @Override
    public SearchModelGenerator get() {
        if (searchModelGenerator == null) {
            final String beanName = config.get("search_model_generator");
            searchModelGenerator = SpringUtil.getContext().getBean(beanName,
                    SearchModelGenerator.class);
        }
        return searchModelGenerator;
    }

    public void set(final SearchModelGenerator searchPageGenerator) {
        this.searchModelGenerator = searchPageGenerator;
    }

    public void set(final String beanName) {
        searchModelGenerator = SpringUtil.getContext().getBean(beanName,
                SearchModelGenerator.class);
        config.set("search_model_generator", beanName);
    }
}
