package org.ucair.searchModel;

import java.util.Date;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.ucair.text.TermVector;

public class SearchModel {

    private final String name;

    private final TermVector termVector;

    private boolean isAdaptive;

    private final Date timestamp;

    public SearchModel(final String name, final TermVector termVector) {
        this.name = name;
        this.termVector = termVector;
        timestamp = new Date();
    }

    public String getName() {
        return name;
    }

    public TermVector getTermVector() {
        return termVector;
    }

    public boolean isAdaptive() {
        return isAdaptive;
    }

    public void setAdaptive(final boolean isAdaptive) {
        this.isAdaptive = isAdaptive;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("termVector", termVector)
                .append("isAdaptive", isAdaptive).toString();
    }
}
