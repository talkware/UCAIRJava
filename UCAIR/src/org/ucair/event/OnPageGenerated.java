package org.ucair.event;

import java.util.List;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.ucair.core.Search;

public class OnPageGenerated extends SearchEvent {

    private final int pageNum;

    private final List<Integer> posList;

    public OnPageGenerated(final Search search, final int pageNum,
            final List<Integer> posList) {
        super(search);
        this.pageNum = pageNum;
        this.posList = posList;
    }

    public int getPageNum() {
        return pageNum;
    }

    public List<Integer> getPosList() {
        return posList;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).appendSuper(super.toString())
                .append("pageNum", pageNum).toString();
    }
}
