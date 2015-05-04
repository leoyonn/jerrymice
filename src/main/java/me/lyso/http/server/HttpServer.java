/**
 * HttpServer.java
 * [CopyRight]
 * @author leo [liuy@xiaomi.com]
 * @date Sep 9, 2013 5:50:12 PM
 */
package me.lyso.http.server;

import me.lyso.http.handler.BaseHttpHandler;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.ContextHandlerCollection;
import org.eclipse.jetty.server.handler.HandlerCollection;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.webapp.WebAppContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.MultipartConfigElement;
import java.util.HashMap;
import java.util.Map;

/**
 * The Http Server Entrance.
 * Usage: <code>
 * new HttpServer(port).addHandler(YourHandler1.class).addHandler(YourHandler2.class).start();
 * </code>
 *
 * @author leo
 */
public class HttpServer {
    protected static final Logger LOGGER = LoggerFactory.getLogger(HttpServer.class);
    private static final MultipartConfigElement MultipartConfEle = new MultipartConfigElement("tmpfiles", 1 << 24, 1 << 24, 1 << 20);
    private Map<String, BaseHttpHandler> handlers = new HashMap<String, BaseHttpHandler>();
    private Server jettyServer;
    private ServletContextHandler context;

    /**
     * Constructor.
     *
     * @param port
     */
    public HttpServer(int port) {
        LOGGER.info("Init: init server by port {}", port);
        jettyServer = new Server(port);

        ContextHandlerCollection contexts = new ContextHandlerCollection();
        HandlerCollection handlers = new HandlerCollection();
        handlers.setHandlers(new Handler[]{
                contexts,
        });
        //noinspection ConstantConditions
        String webRoot = HttpServer.class.getClassLoader().getResource("webapp").toExternalForm();
        context = new WebAppContext(contexts, webRoot, "/");
        jettyServer.setHandler(handlers);
        LOGGER.info("Init-success: with web-root: {}", webRoot);
    }

    /**
     * Add a user defined handler class into the server.
     * <b>
     * Handler's @Path should be not empty("/" or ""), and not same with others.
     * </b>
     *
     * @param clz
     * @return
     */
    public HttpServer addHandler(Class<? extends BaseHttpHandler> clz) {
        BaseHttpHandler handler = null;
        try {
            handler = clz.newInstance();
        } catch (Exception ex) { // InstantiationException IllegalAccessException
            throw new IllegalArgumentException("Creating instance of class [" + clz + "]: failed!", ex);
        }
        if (handlers.containsKey(handler.path())) {
            throw new IllegalArgumentException("Invalid class [" + clz + "]: @Path's value duplicated!");
        }
        return addHandler(handler);
    }

    /**
     * Add a user defined handler into the server.
     * <b>
     * Handler's @Path should be not empty("/" or ""), and not same with others.
     * </b>
     *
     * @param handler
     * @return
     */
    public HttpServer addHandler(BaseHttpHandler handler) {
        LOGGER.info("Add-handler: {}", handler.getClass().getCanonicalName());
        handlers.put(handler.path(), handler);
        ServletHolder holder = new ServletHolder(handler);
        holder.getRegistration().setMultipartConfig(MultipartConfEle);
        context.addServlet(holder, handler.path().length() == 0 ? "/*" : "/" + handler.path() + "/*");
        LOGGER.info("Init: got handler [{}] of path [{}]", handler.getClass().getCanonicalName(), handler.path());
        return this;
    }

    /**
     * Start this server.
     *
     * @throws Exception
     */
    public void start() throws Exception {
        jettyServer.start();
        LOGGER.info("server started.");
    }

    /**
     * Block until the server is stopped.
     *
     * @throws InterruptedException
     */
    public void join() throws InterruptedException {
        jettyServer.join();
    }
}
