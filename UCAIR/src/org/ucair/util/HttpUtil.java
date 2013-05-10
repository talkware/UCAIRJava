package org.ucair.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

public class HttpUtil {

    private static final Log LOGGER = LogFactory.getLog(HttpUtil.class);

    public static String downloadPage(final String url) throws IOException {
        final HttpClient httpClient = new DefaultHttpClient();

        final HttpGet request = new HttpGet(url);

        return downloadPage(httpClient, request);
    }

    public static String downloadPage(final HttpClient httpClient,
            final HttpGet request) throws IOException {
        LOGGER.info("Downloading " + request.getURI());

        final HttpResponse response = httpClient.execute(request);

        LOGGER.info(response.getStatusLine());

        final HttpEntity entity = response.getEntity();

        if (entity != null) {
            final InputStream in = entity.getContent();
            try {
                final List<String> lines = IOUtils.readLines(in);
                return StringUtils.join(lines, '\n');
            } catch (final IOException e) {
                throw e;
            } catch (final RuntimeException e) {
                request.abort();
                throw e;
            } finally {
                in.close();
            }
        }

        return null;
    }

    public static String getRequestUrl(final HttpServletRequest request) {
        final StringBuilder builder = new StringBuilder();
        builder.append(request.getRequestURI());
        if (request.getQueryString() != null) {
            builder.append('?');
            builder.append(request.getQueryString());
        }
        return builder.toString();
    }

    public static HttpServletRequest getServletRequest() {
        return ((ServletRequestAttributes) RequestContextHolder
                .currentRequestAttributes()).getRequest();
    }
}
