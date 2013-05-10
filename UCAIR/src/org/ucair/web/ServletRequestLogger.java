package org.ucair.web;

import javax.servlet.ServletRequestEvent;
import javax.servlet.ServletRequestListener;
import javax.servlet.annotation.WebListener;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ucair.util.HttpUtil;

@WebListener
public class ServletRequestLogger implements ServletRequestListener {

    private static final Log LOGGER = LogFactory
            .getLog(ServletRequestLogger.class);

    @Override
    public void requestInitialized(final ServletRequestEvent event) {
        final HttpServletRequest request = (HttpServletRequest) event
                .getServletRequest();
        final String url = HttpUtil.getRequestUrl(request);
        if (!isStaticResource(url)) {
            LOGGER.info("Start: " + url);
        }
    }

    @Override
    public void requestDestroyed(final ServletRequestEvent event) {
        final HttpServletRequest request = (HttpServletRequest) event
                .getServletRequest();
        final String url = HttpUtil.getRequestUrl(request);
        if (!isStaticResource(url)) {
            LOGGER.info("Finish: " + url);
        }
    }

    private boolean isStaticResource(final String url) {
        return url.startsWith("/static/");
    }
}
