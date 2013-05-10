package org.ucair.text;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class TermVectorIndexInMemoryImpl implements TermVectorIndex {

    private final Map<Integer, TermVector> docIdToDoc = new HashMap<Integer, TermVector>();

    private final Map<String, Map<Integer, Double>> termToPostings = new HashMap<String, Map<Integer, Double>>();

    @Override
    public void addDoc(final int docId, final TermVector doc) {
        removeDoc(docId);
        doc.setImmutable();
        docIdToDoc.put(docId, doc);

        for (final Map.Entry<String, Double> entry : doc.getValues().entrySet()) {
            final String term = entry.getKey();
            final double value = entry.getValue();
            Map<Integer, Double> postings = termToPostings.get(term);
            if (postings == null) {
                postings = new HashMap<Integer, Double>();
                termToPostings.put(term, postings);
            }
            postings.put(docId, value);
        }
    }

    @Override
    public TermVector getDoc(final int docId) {
        return docIdToDoc.get(docId);
    }

    @Override
    public void removeDoc(final int docId) {
        final TermVector doc = getDoc(docId);
        if (doc == null) {
            return;
        }

        for (final String term : doc.getTerms()) {
            final Map<Integer, Double> postings = termToPostings.get(term);
            postings.remove(docId);
        }
        docIdToDoc.remove(docId);
    }

    @Override
    public int getDocCount() {
        return docIdToDoc.size();
    }

    @Override
    public int getTermCount() {
        return termToPostings.size();
    }

    @Override
    public Map<Integer, Double> getPostings(final String term) {
        final Map<Integer, Double> postings = termToPostings.get(term);
        return postings == null ? Collections.<Integer, Double> emptyMap()
                : Collections.unmodifiableMap(postings);
    }
}
