package org.ucair.webSearch.searchEngines;

import java.io.IOException;
import java.net.URLEncoder;

import javax.net.ssl.SSLPeerUnverifiedException;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.ucair.util.HttpUtil;
import org.ucair.webSearch.SearchEngine;
import org.ucair.webSearch.SearchRequest;
import org.ucair.webSearch.SearchResponse;
import org.ucair.webSearch.SearchResult;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component("bingSearchEngine")
public class BingSearchEngine implements SearchEngine {

    @Value("#{appConfig.get('bing_search_engine.account_key')}")
    private String bingAccountKey;

    @Value("#{appConfig.get('bing_search_engine.max_attempts')}")
    private int maxAttempts;

    @Override
    public String getName() {
        return "Bing";
    }

    @Override
    public String getSearchUrl(final SearchRequest request) {
        try {
            final String encodedQuery = URLEncoder.encode(request.getQuery(), "UTF-8");
            return String
                    .format("https://api.cognitive.microsoft.com/bing/v5.0/search?q=%s&offset=%d&count=%d&mkt=en-us",
                            encodedQuery, request.getStartPos() - 1, request.getCount());
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public int getMaxBatchSize() {
        return 50;
    }

    @Override
    public SearchResponse search(final SearchRequest request) {
        try {
            final String url = getSearchUrl(request);
            final String pageContent = downloadPage(url);
            return parsePage(request, pageContent);
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
    }

    private String downloadPage(final String url) throws IOException {
        final HttpClient httpClient = new DefaultHttpClient();

        final HttpGet request = new HttpGet(url);
        request.addHeader("Ocp-Apim-Subscription-Key", bingAccountKey);

        return downloadWithRetry(httpClient, request);
    }

    private String downloadWithRetry(final HttpClient httpClient,
            final HttpGet request) throws IOException {
        int attempt = 0;
        while (true) {
            try {
                ++attempt;
                return HttpUtil.downloadPage(httpClient, request);
            } catch (final SSLPeerUnverifiedException e) { // transient error
                if (attempt < maxAttempts) {
                    continue;
                }
                throw new IOException("Failed to connect to Bing", e);
            }
        }
    }
    
    private SearchResponse parsePage(final SearchRequest request,
            final String pageContent) throws IOException {
        final SearchResponse response = new SearchResponse(request.getQuery());

        final ObjectMapper mapper = new ObjectMapper();
        final JsonNode root = mapper.readValue(pageContent, JsonNode.class);

        for (final JsonNode resultNode : root.path("webPages").path("value")) {
            final String title = resultNode.path("name").textValue();
            final String description = resultNode.path("snippet")
                    .textValue();
            final String url = resultNode.path("url").textValue();
            final String displayUrl = resultNode.path("displayUrl").textValue();
            if (url != null) {
                final SearchResult result = new SearchResult(url, title,
                        description, displayUrl);
                response.addResult(result);
            }
        }

        long totalTermCountEstimate = 10000;
        if (root.path("d").path("__next").isMissingNode()) {
            totalTermCountEstimate = request.getStartPos()
                    + response.getResults().size() - 1;
        }
        response.setTotalResultCountEstimate(totalTermCountEstimate);

        return response;
    }
}
