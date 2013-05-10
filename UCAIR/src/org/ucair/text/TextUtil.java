package org.ucair.text;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.core.LowerCaseFilter;
import org.apache.lucene.analysis.core.StopAnalyzer;
import org.apache.lucene.analysis.core.StopFilter;
import org.apache.lucene.analysis.en.PorterStemFilter;
import org.apache.lucene.analysis.standard.StandardTokenizer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.util.Version;

public class TextUtil {

    public static class DefaultAnalyzer extends
            org.apache.lucene.analysis.Analyzer {

        private final boolean useStemmer;

        private final boolean useStopFilter;

        public DefaultAnalyzer() {
            this(true, true);
        }

        public DefaultAnalyzer(final boolean useStemmer,
                final boolean useStopFilter) {
            this.useStemmer = useStemmer;
            this.useStopFilter = useStopFilter;
        }

        @Override
        protected TokenStreamComponents createComponents(
                final String fieldName, final Reader reader) {
            final Tokenizer source = new StandardTokenizer(Version.LUCENE_40,
                    reader);
            TokenStream filter = new LowerCaseFilter(Version.LUCENE_40, source);
            if (useStopFilter) {
                filter = new StopFilter(Version.LUCENE_40, filter,
                        StopAnalyzer.ENGLISH_STOP_WORDS_SET);
            }
            if (useStemmer) {
                filter = new PorterStemFilter(filter);
            }
            return new TokenStreamComponents(source, filter);
        }
    }

    public static List<String> tokenize(final String text) {
        final Analyzer analyzer = new DefaultAnalyzer();
        try {
            return tokenize(analyzer, text);
        } finally {
            analyzer.close();
        }
    }

    public static List<String> tokenize(final Analyzer analyzer,
            final String text) {
        final List<String> tokens = new ArrayList<String>();
        try {
            final TokenStream tokenStream = analyzer.tokenStream("",
                    new StringReader(text));
            try {
                tokenStream.reset();
                final CharTermAttribute charTermAttr = tokenStream
                        .addAttribute(CharTermAttribute.class);
                while (tokenStream.incrementToken()) {
                    tokens.add(charTermAttr.toString());
                }
                tokenStream.end();
            } finally {
                tokenStream.close();
            }
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
        return tokens;
    }

    public static <E> void increment(final Map<E, Double> map, final E id,
            final double delta) {
        final Double sum = map.get(id);
        map.put(id, sum == null ? delta : sum + delta);
    }
}
