/**
 * BaseHttpHandler.java
 * [CopyRight]
 * @author leo [leoyonn@gmail.com]
 * @date Sep 9, 2013 5:48:46 PM
 */
package me.lyso.http.handler;

import me.lyso.http.annotation.Get;
import me.lyso.http.annotation.Path;
import me.lyso.http.annotation.Post;
import me.lyso.http.base.Return.Type;
import me.lyso.http.utils.HttpMethodUtils;
import me.lyso.http.base.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.annotation.Annotation;
import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.util.*;

/**
 * Extends this and mark <code>@Path(path)</code>, root path not supporting "/" or "".
 * <b>
 * Handler's @Path should be not empty("/" or ""), and not same with others in one HttpServer.
 * </b>
 *
 * @author leo [leoyonn@gmail.com]
 */
@WebServlet(asyncSupported = true)
public abstract class BaseHttpHandler extends HttpServlet {
    private static final Logger LOGGER = LoggerFactory.getLogger(BaseHttpHandler.class);
    public static final String TITLE_KEY = "page_title";
    private static final long serialVersionUID = 1L;

    private List<HttpMethodParams> getMethods = new ArrayList<HttpMethodParams>();
    private List<HttpMethodParams> postMethods = new ArrayList<HttpMethodParams>();

    protected final String path;

    /**
     * Constructor.
     * Check @Path annotation and all api-methods with @Get/@Put annotation.
     */
    protected BaseHttpHandler() {
        super();
        Class<? extends BaseHttpHandler> clz = getClass();
        LOGGER.info("^#Blue.init: handler {}", clz.getCanonicalName());
        Path path = clz.getAnnotation(Path.class);
        if (path == null) {
            throw new IllegalArgumentException("invalid class [" + clz + "]: should add @Path annotation!");
        }
        this.path = HttpMethodUtils.removeHeadAndTailSlash(path.value());
        parseHttpMethodParams(clz, HttpMethodUtils.initFilters(path.filters()));
    }

    private void parseHttpMethodParams(Class<?> clz, Filter[] filters) {
        LOGGER.info("^#Blue.init: parse http methods and args of {}, {}.", clz.getCanonicalName(), path);
        for (Method m : clz.getDeclaredMethods()) {
            Annotation a = HttpMethodUtils.getHttpMethodAnnotation(m);
            if (a == null) {
                LOGGER.info("^#Orange.parse-http-methods-args: ignore method {}().", m.getName());
                continue;
            }

            List<MethodParam> params = HttpMethodUtils.parseParamTypes(m);
            if (a instanceof Get) {
                Get geta = (Get) a;
                filters = mergeFilters(geta.overrideFilters(), filters, HttpMethodUtils.initFilters(geta.filters()));
                HttpMethodParams margs = new HttpMethodParams(m, params, "get", new RouterRule(geta.value()), geta.timeout(), filters);
                getMethods.add(margs);
                LOGGER.info("^#Blue.parse-http-methods-args: GET-method {}(): {}.", m.getName(), margs);
            } else if (a instanceof Post) {
                Post posta = (Post) a;
                filters = mergeFilters(posta.overrideFilters(), filters, HttpMethodUtils.initFilters(posta.filters()));
                HttpMethodParams margs = new HttpMethodParams(m, params, "post", new RouterRule(posta.value()), posta.timeout(), filters);
                postMethods.add(margs);
                LOGGER.info("^#Blue.parse-http-methods-args: POST-method {}(): {}.", m.getName(), margs);
            } else {
                throw new IllegalArgumentException("unknown method");
            }
        }
    }

    /**
     * Merge global filters and mine filters.
     *
     * @param override
     * @param global
     * @param mine
     * @return
     * @see Get#overrideFilters
     * @see Post#overrideFilters
     */
    private Filter[] mergeFilters(boolean override, Filter[] global, Filter[] mine) {
        if (override || global.length == 0) {
            return mine;
        } else if (mine.length == 0) {
            return global;
        } else {
            Filter[] filters = new Filter[global.length + mine.length];
            System.arraycopy(global, 0, filters, 0, global.length);
            System.arraycopy(mine, 0, filters, global.length, mine.length);
            return filters;
        }
    }

    public String path() {
        return path;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        handle(new Context(request, response), getMethods);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        handle(new Context(request, response), postMethods);
    }

    /**
     * Handle request using corresponding method.
     *
     * @param context
     * @param methods
     * @throws IOException
     * @throws ServletException
     */
    private void handle(Context context, List<HttpMethodParams> methods) throws IOException, ServletException {
        HttpServletRequest request = context.request();
        request.setAttribute(TITLE_KEY, getClass().getSimpleName());
        String uri = HttpMethodUtils.removeHeadAndTailSlash(request.getRequestURI());
        uri = uri.substring(path.length());
        LOGGER.debug("^#Blue.handle-request: url {}.", uri);
        Return ret = null;
        try {
            ret = matchCall(context, uri, methods);
        } catch (Exception ex) {
            LOGGER.error("^#Red.handle-request: {} got exception.", uri, ex);
            StringWriter errors = new StringWriter();
            ex.printStackTrace(new PrintWriter(errors));
            ret = Return.build(Type.Error, errors.toString(), null, false);
        }
        LOGGER.debug("^#Green.handle-request: done, dispatching with {}...", ret);
        if (!ret.async()) {
            ret.complete(context);
        }
    }

    /**
     * Find which http-method should process this query, and merge queryParams and urlParams and then invoke that method.
     *
     * @param context
     * @param url
     * @param methods
     * @return
     * @throws Exception
     */
    private Return matchCall(Context context, String url, List<HttpMethodParams> methods) throws Exception {
        LOGGER.debug("^#Blue.match-call: url {}.", url);
        Map<String, String> urlParams = null;
        HttpMethodParams methodArgs = null;
        for (HttpMethodParams m : methods) {
            urlParams = m.checkRule(url);
            if (urlParams != null) { // matched
                methodArgs = m;
                break;
            }
        }
        if (urlParams == null) { // no url matched
            context.startAsync(0);
            throw new IllegalArgumentException("Bad request: " + url);
        }
        context.startAsync(methodArgs.timeout());
        // merge queryParams into urlParams
        Map<String, String[]> queryParams = context.request().getParameterMap();
        for (Map.Entry<String, String[]> q : queryParams.entrySet()) {
            String k = q.getKey();
            String[] v = q.getValue();
            if (v != null && v.length > 0 && urlParams.get(k) == null) {
                urlParams.put(k, v[0]);
            }
        }
        Object[] args = HttpMethodUtils.getParamValues(context, urlParams, methodArgs.params());

        Filter[] filters = methodArgs.filters();
        if (filters.length == 0) {
            return call(url, methodArgs, args);
        }
        Return ret = null;
        for (Filter filter : filters) {
            if ((ret = filter.pre(context, args)) != null) {
                return ret;
            }
        }
        ret = call(url, methodArgs, args);
        for (int i = filters.length - 1; i >= 0; i--) {
            filters[i].post(context);
        }
        return ret;
    }

    /**
     * Call the matched method to process this request.
     *
     * @param url
     * @param methodArgs
     * @param args
     * @return
     */
    private Return call(String url, HttpMethodParams methodArgs, Object[] args) {
        LOGGER.debug("^#Blue.match-call: url {} got args {}.", url, args);
        Object result = methodArgs.invoke(this, args);
        Return ret;
        if (result instanceof Return) {
            ret = (Return) result;
        } else {
            ret = Return.build(Type.Html, result == null ? "" : result.toString(), null, false);
        }
        if (methodArgs.modelParamIndex() >= 0) {
            ret.setModel((Model) args[methodArgs.modelParamIndex()]);
        }
        return ret;
    }

    /**
     * Simple wrap for failed json result with an exception.
     *
     * @param ex
     * @return
     */
    public static Return failJson(Exception ex) {
        String msg = ex.getMessage() != null ? ex.getMessage() : ex.toString();
        return Return.build(Type.Json, "{\"error\":\"Fail: " + msg.replaceAll("\"", "'") + "!\"}");
    }

    /**
     * Simple wrap for failed json result with a message.
     *
     * @param msg
     * @return
     */
    public static Return failJson(String msg) {
        return Return.build(Type.Json, "{\"error\":\"Fail: " + msg + "!\"}");
    }

    /**
     * Simple wrap for successful json result.
     *
     * @param result
     * @return
     */
    public static Return simpleJson(String result) {
        return Return.build(Type.Json, "{\"r\":\"" + result.replaceAll("\"", "'") + "\"}");
    }

}
