package org.ucair.webSearch.searchEngines;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;
import org.ucair.util.HttpUtil;
import org.ucair.webSearch.SearchEngine;
import org.ucair.webSearch.SearchRequest;
import org.ucair.webSearch.SearchResponse;
import org.ucair.webSearch.SearchResult;

@Component("aolSearchEngine")
public class AOLSearchEngine implements SearchEngine {

    @Override
    public String getName() {
        return "AOL";
    }

    @Override
    public String getSearchUrl(final SearchRequest request) {
        try {
            final String encodedQuery = URLEncoder.encode(request.getQuery(),
                    "UTF-8");
            return String
                    .format("http://search.aol.com/aol/search?q=%s&page=%d&count_override=%d",
                            encodedQuery, request.getStartPos(),
                            request.getCount());
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public int getMaxBatchSize() {
        return 20;
    }

    private Element first(final Elements elements) {
        if (elements.isEmpty()) {
            return null;
        }
        return elements.get(0);
    }

    private Document getParsedDoc(final String html, final String baseUrl) {
        try {
            final InputStream stream = new ByteArrayInputStream(html.getBytes());
            try {
                return Jsoup.parse(stream, "UTF-8", baseUrl);
            } finally {
                stream.close();
            }
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void parseSearchPage(final SearchResponse response,
            final String html, final int startPos) {
        final Document doc = getParsedDoc(html, "http://search.aol.com/aol/");

        final Element countElement = first(doc.select(".MSR"));
        if (countElement != null) {
            final Pattern pattern = Pattern.compile("(\\d[\\d,]*).+results");
            final Matcher matcher = pattern.matcher(countElement.text());
            if (matcher.find()) {
                response.setTotalResultCountEstimate(Long.parseLong(matcher
                        .group(1).replace(",", "")));
            }
        }

        final Element spellElement = first(doc.select(".spell > a"));
        if (spellElement != null) {
            response.setSpellCorrection(spellElement.text());
        }

        final Elements resultElements = doc.select("[content*=MSL] > li");
        for (final Element resultElement : resultElements) {
            final Element titleElement = first(resultElement.select("a"));
            if (titleElement == null) {
                continue;
            }
            final String url = titleElement.attr("href");
            final String title = titleElement.text();

            final Element displayUrlElement = first(resultElement
                    .select("[property=f:durl]"));
            if (displayUrlElement == null) {
                continue;
            }
            final String displayUrl = displayUrlElement.text();

            final Element summaryElement = first(resultElement
                    .select("[property=f:desc]"));
            if (summaryElement == null) {
                continue;
            }
            final String summary = summaryElement.text();

            final SearchResult result = new SearchResult(url, title, summary,
                    displayUrl);
            response.addResult(result);
        }
    }

    @Override
    public SearchResponse search(final SearchRequest request) {
        final String url = getSearchUrl(request);

        final String html;
        try {
            html = HttpUtil.downloadPage(url);
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }

        final SearchResponse response = new SearchResponse(request.getQuery());
        parseSearchPage(response, html, request.getStartPos());
        return response;
    }
}
