package org.ucair.webSearch;

import java.util.ArrayList;
import java.util.List;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.PhraseQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;
import org.ucair.text.TextUtil;

public class QueryParser {

    public Query parse(final String field, final String text) {
        return parse(field, text, new TextUtil.DefaultAnalyzer());
    }

    public Query parse(final String field, final String text,
            final Analyzer analyzer) {
        final BooleanQuery booleanQuery = new BooleanQuery();

        final List<String> parts = splitQueryText(text);
        for (String part : parts) {
            String prefix = "";
            if (part.startsWith("site:")) {
                prefix = "site:";
            } else if (part.startsWith("-")) {
                prefix = "-";
            } else if (part.startsWith("+")) {
                prefix = "+";
            }

            part = part.substring(prefix.length()); // remove prefix
            part = part.replaceFirst("\"(.*)\"", "$1"); // remove enclosing ""
            if (part.isEmpty()) {
                continue;
            }

            final List<String> tokens = TextUtil.tokenize(part);
            if (tokens.isEmpty()) {
                continue;
            }

            Query subQuery;
            if (tokens.size() == 1) {
                subQuery = new TermQuery(getTerm(field, tokens.get(0), prefix));
            } else {
                final PhraseQuery phraseQuery = new PhraseQuery();
                for (final String token : tokens) {
                    phraseQuery.add(getTerm(field, token, prefix));
                }
                subQuery = phraseQuery;
            }

            BooleanClause.Occur occur;
            if (prefix.equals("site:") || prefix.equals("+")) {
                occur = BooleanClause.Occur.MUST;
            } else if (prefix.equals("-")) {
                occur = BooleanClause.Occur.MUST_NOT;
            } else {
                occur = BooleanClause.Occur.SHOULD;
            }
            booleanQuery.add(new BooleanClause(subQuery, occur));
        }

        return booleanQuery;
    }

    private Term getTerm(String field, final String text, final String prefix) {
        field = prefix.endsWith(":") ? prefix.substring(0, prefix.length() - 1)
                : field;
        return new Term(field, text);
    }

    private List<String> splitQueryText(final String text) {
        final List<String> components = new ArrayList<String>();
        int i = -1;
        int j = 0;
        for (; j < text.length(); ++j) {
            final char ch = text.charAt(j);
            if (ch == ' ') {
                if (i != -1) {
                    components.add(text.substring(i, j));
                    i = -1;
                }
            } else {
                if (i == -1) {
                    i = j;
                }
                if (ch == '\"') {
                    final int k = text.indexOf('\"', j + 1);
                    if (k != -1) {
                        j = k;
                    }
                }
            }
        }
        if (i != -1) {
            components.add(text.substring(i, j));
        }
        return components;
    }
}
