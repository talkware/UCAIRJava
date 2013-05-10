package org.ucair.text;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.zip.GZIPInputStream;

import javax.inject.Inject;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component("collectionModel")
public class CollectionModelImpl implements CollectionModel {

    private int numTerms;

    private long totalTermCountSum;

    private Map<String, Integer> headTermCounts;

    private long headTermCountSum;

    public CollectionModelImpl(final int numTerms,
            final long totalTermCountSum,
            final Map<String, Integer> headTermCounts) {
        this.numTerms = numTerms;
        this.totalTermCountSum = totalTermCountSum;
        this.headTermCounts = headTermCounts;
        headTermCountSum = getHeadTermCountSum();
    }

    @Inject
    public CollectionModelImpl(
            @Value("#{appConfig.get('collection_model.word_count_file')}") final String fileName) {
        loadTermCounts(new File(fileName));
    }

    private void loadTermCounts(final File file) {
        Scanner scanner = null;
        try {
            InputStream stream = new FileInputStream(file);
            if (file.getName().endsWith(".gz")) {
                stream = new GZIPInputStream(stream);
            }
            scanner = new Scanner(stream);

            numTerms = scanner.nextInt();
            totalTermCountSum = scanner.nextLong();

            headTermCounts = new HashMap<String, Integer>();
            while (scanner.hasNext()) {
                final String term = scanner.next();
                final int count = scanner.nextInt();
                headTermCounts.put(term, count);
            }
            headTermCountSum = getHeadTermCountSum();
        } catch (final IOException e) {
            throw new RuntimeException(e);
        } finally {
            if (scanner != null) {
                scanner.close();
            }
        }
    }

    private long getHeadTermCountSum() {
        long sum = 0;
        for (final int count : headTermCounts.values()) {
            sum += count;
        }
        return sum;
    }

    private double getTailTermProb() {
        return (double) (totalTermCountSum - headTermCountSum + 1)
                / (numTerms - headTermCounts.size() + 1) / totalTermCountSum;
    }

    private double getHeadTermProb(final int count) {
        return (double) count / totalTermCountSum;
    }

    @Override
    public double getTermProb(final String term) {
        final Integer count = headTermCounts.get(term);
        if (count != null) {
            return getHeadTermProb(count);
        }
        return getTailTermProb();
    }
}
