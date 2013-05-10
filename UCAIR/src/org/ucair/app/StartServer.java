package org.ucair.app;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.nio.SelectChannelConnector;
import org.eclipse.jetty.webapp.WebAppContext;
import org.ucair.core.UCAIRConfig;

public class StartServer {

    private static final Log LOGGER = LogFactory.getLog(StartServer.class);

    public static void main(final String[] args) throws Exception {
        System.setProperty("org.apache.jasper.compiler.disablejsr199", "true");

        try {
            final Server server = new Server();

            final UCAIRConfig config = new UCAIRConfig();
            final int port = Integer.parseInt(config.get("port"));

            final SelectChannelConnector connector = new SelectChannelConnector();
            connector.setReuseAddress(false);
            connector.setPort(port);
            server.setConnectors(new Connector[] { connector });

            final WebAppContext context = new WebAppContext();
            context.setDescriptor("web/WEB-INF/web.xml");
            context.setResourceBase("web");
            context.setContextPath("/");
            context.setParentLoaderPriority(true);

            server.setHandler(context);

            server.start();

            server.join();
        } catch (final Exception e) {
            e.printStackTrace(System.err);
            LOGGER.fatal("Server terminated", e);
        }
    }
}
