package org.ucair.event;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.ucair.core.Search;

public class OnResultClicked extends SearchEvent {

    private final int resultPos;

    public OnResultClicked(final Search search, final int resultPos) {
        super(search);
        this.resultPos = resultPos;
    }

    public int getResultPos() {
        return resultPos;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).appendSuper(super.toString())
                .append("resultPos", resultPos).toString();
    }
}
