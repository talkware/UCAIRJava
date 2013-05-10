package org.ucair.webSearch.searchEngines;

import java.io.IOException;
import java.net.URLEncoder;

import javax.net.ssl.SSLPeerUnverifiedException;

import org.apache.http.auth.AuthenticationException;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
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
            final String encodedQuery = URLEncoder.encode(
                    "\'" + request.getQuery() + "\'", "UTF-8");
            return String
                    .format("https://api.datamarket.azure.com/Bing/Search/Web?Query=%s&$format=json&$skip=%d",
                            encodedQuery, (request.getStartPos() - 1));
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
        final HttpContext localContext = new BasicHttpContext();

        final HttpGet request = new HttpGet(url);
        try {
            request.addHeader(new BasicScheme().authenticate(
                    new UsernamePasswordCredentials(bingAccountKey,
                            bingAccountKey), request, localContext));
        } catch (final AuthenticationException e) {
            throw new IOException("Failed to authenticate", e);
        }

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

        for (final JsonNode resultNode : root.path("d").path("results")) {
            final String title = resultNode.path("Title").textValue();
            final String description = resultNode.path("Description")
                    .textValue();
            final String url = resultNode.path("Url").textValue();
            final String displayUrl = resultNode.path("DisplayUrl").textValue();
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
