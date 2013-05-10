package org.ucair.searchPage;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.stereotype.Component;
import org.ucair.core.PaginationUtil;
import org.ucair.core.Search;

import com.google.common.base.Preconditions;

@Component
public class BaseSearchPageGenerator implements SearchPageGenerator {

    @Override
    public List<SearchPageResult> getItems(final Search search,
            final int pageNum) {
        if (search.getTotalPageCountEstimate() == 0) {
            return Collections.emptyList();
        }

        validatePageNum(search, pageNum);

        final List<SearchPageResult> items = new ArrayList<SearchPageResult>();
        for (final int pos : listResults(search, pageNum)) {
            items.add(new SearchPageResult(search, pos));
        }
        return items;
    }

    protected void validatePageNum(final Search search, final int pageNum) {
        Preconditions.checkArgument(pageNum > 0, "Invalid page: %s", pageNum);
        Preconditions.checkArgument(pageNum <= search.getDownloadedPageCount(),
                "Page not downloaded yet: %s", pageNum);
    }

    private List<Integer> listResults(final Search search, final int pageNum) {
        final List<Integer> results = new ArrayList<Integer>();
        final int startPos = PaginationUtil.getStartPos(pageNum);
        for (int pos = startPos; pos < startPos + PaginationUtil.PAGE_SIZE
                && pos <= search.getDownloadedResultCount(); ++pos) {
            results.add(pos);
        }
        return results;
    }

    @Override
    public boolean hasPage(final Search search, final int pageNum) {
        if (pageNum < 1) {
            return false;
        }
        if (pageNum > search.getTotalPageCountEstimate()) {
            return false;
        }
        return true;
    }
}
