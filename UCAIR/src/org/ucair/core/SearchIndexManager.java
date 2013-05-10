package org.ucair.core;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;
import org.ucair.event.EventListener;
import org.ucair.event.SearchEvent;
import org.ucair.event.OnResultsDownloaded;
import org.ucair.text.TermVector;
import org.ucair.text.TermVectorIndex;
import org.ucair.text.TermVectorIndexInMemoryImpl;
import org.ucair.text.TextUtil;
import org.ucair.webSearch.SearchResult;

import com.google.common.base.Preconditions;

@Component
public class SearchIndexManager implements EventListener {

    private void setSearchIndex(final Search search, final TermVectorIndex index) {
        search.setAttr("searchIndex", index);
    }

    private TermVectorIndex getSearchIndex(final Search search) {
        return search.getAttr("searchIndex");
    }

    @Override
    public synchronized void handle(final SearchEvent event) {
        if (event instanceof OnResultsDownloaded) {
            final Search search = event.getSearch();

            TermVectorIndex index = getSearchIndex(search);
            if (index == null) {
                index = new TermVectorIndexInMemoryImpl();
                setSearchIndex(search, index);
            }

            final int indexedResultCount = index.getDocCount();
            for (int pos = indexedResultCount + 1; pos <= search
                    .getDownloadedResultCount(); ++pos) {
                final SearchResult result = search.getResult(pos);
                final TermVector termVector = getTermVector(result);

                index.addDoc(pos, termVector);
            }
        }
    }

    public synchronized TermVectorIndex getIndex(final Search search) {
        final TermVectorIndex index = getSearchIndex(search);
        Preconditions.checkNotNull(index, "No index for search: %s", search);
        return index;
    }

    private TermVector getTermVector(final SearchResult result) {
        final List<String> tokens = new ArrayList<String>();
        tokens.addAll(TextUtil.tokenize(result.getTitle()));
        tokens.addAll(TextUtil.tokenize(result.getSummary()));
        return new TermVector(tokens);
    }
}
