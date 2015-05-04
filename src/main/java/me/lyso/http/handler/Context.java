/**
 *
 * Context.java
 * @date 14-11-20 下午4:19
 * @author leo [liuy@xiaomi.com]
 * [CopyRight] All Rights Reserved.
 */

package me.lyso.http.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicReference;

import static me.lyso.http.handler.Context.Status.*;

/**
 * @author leo
 */
public final class Context {
    private static final Logger logger = LoggerFactory.getLogger(Context.class);
    private final HttpServletRequest request;
    private final HttpServletResponse response;
    private AsyncContext async;
    private AtomicReference<Status> status = new AtomicReference<Status>(Start);

    public static enum Status {
        Start, Timeout, Error, Completed,
    }

    public Context(HttpServletRequest request, HttpServletResponse response) {
        this.request = request;
        this.response = response;
    }

    public HttpServletRequest request() {
        return async == null ? this.request : (HttpServletRequest) async.getRequest();
    }

    public HttpServletResponse response() {
        return async == null ? this.response : (HttpServletResponse) async.getResponse();
    }

    public ServletInputStream inputStream() throws IOException {
        return request.getInputStream();
    }

    /**
     * Mark this context into async mode.
     *
     * @param timeout the timeout in milliseconds
     * @return
     */
    protected Context startAsync(long timeout) {
        this.async = request.startAsync(request, response);
        async.setTimeout(timeout);
        async.addListener(new AsyncListener() {
            @Override
            public void onComplete(AsyncEvent event) throws IOException {
                logger.debug("Async context of {} completed.", request.getPathInfo());
                status.compareAndSet(Start, Completed); // Timeout / Error don't change.
            }

            @Override
            public void onTimeout(AsyncEvent event) throws IOException {
                logger.warn("Async context of {} got timeout.", request.getPathInfo());
                response.sendError(504, "User defined timeout");
                status.set(Timeout);
            }

            @Override
            public void onError(AsyncEvent event) throws IOException {
                logger.warn("Async context of {} got error.", request.getPathInfo());
                response.sendError(504, "User defined error");
                status.set(Error);
            }

            @Override
            public void onStartAsync(AsyncEvent event) throws IOException {
                logger.debug("Async context of {} started.", request.getPathInfo());
                status.set(Start);
            }
        });
        return this;
    }

    public Context error(String reason) throws IOException {
        if (status.get() != Start) {
            logger.warn("Illegal call [error] on status [{}]", status.get());
            return this;
        }

        response.sendError(504, reason);
        async.complete();
        return this;
    }

    public Context ok(String data) throws IOException {
        if (status.get() != Start) {
            logger.warn("Illegal call [ok] on status [{}]", status.get());
            return this;
        }

        response.setStatus(HttpServletResponse.SC_OK);
        response.getWriter().println(data);

        async.complete();
        return this;
    }

    public Context web(String path) throws ServletException, IOException {
        if (status.get() != Start) {
            logger.warn("Illegal call [web] on status [{}]", status.get());
            return this;
        }

        response.setStatus(HttpServletResponse.SC_OK);
        System.out.println(request + ": " + path + ", dispatcher: " + request.getRequestDispatcher(path) + ", async?:" + request.isAsyncStarted());

        if (async != null) {
            async.dispatch(path);
        } else {
            request.getRequestDispatcher(path).forward(request, response);
        }
        return this;
    }

    public Context redirect(String path) throws ServletException, IOException {
        if (status.get() != Start) {
            logger.warn("Illegal call [dispatch] on status [{}]", status.get());
            return this;
        }
        response.setStatus(HttpServletResponse.SC_MOVED_TEMPORARILY);
        if (async != null) {
            async.dispatch(path);
        } else {
            request.getRequestDispatcher(path).forward(request, response);
        }
        return this;
    }

    public Status status() {
        return status.get();
    }
}
