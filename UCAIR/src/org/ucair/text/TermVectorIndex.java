package org.ucair.text;

import java.util.Map;

public interface TermVectorIndex {

    void addDoc(int docId, TermVector doc);

    TermVector getDoc(int docId);

    void removeDoc(int docId);

    int getDocCount();

    int getTermCount();

    Map<Integer, Double> getPostings(final String term);
}
