/**
 * HttpMethodUtils.java
 * [CopyRight]
 * @author leo [leoyonn@gmail.com]
 * @date Sep 10, 2013 6:49:41 PM
 */
package me.lyso.http.utils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import me.lyso.http.annotation.Get;
import me.lyso.http.annotation.Param;
import me.lyso.http.annotation.Post;
import me.lyso.http.handler.Context;
import me.lyso.http.base.MethodParam;
import me.lyso.http.base.MethodParam.ContextParam;
import me.lyso.http.base.MethodParam.ModelParam;
import me.lyso.http.base.Model;

/**
 * Utilities for Http method and parameter parsing.
 *
 * @author leo
 */
public class HttpMethodUtils {
    public static final Set<Class<?>> SUPPORTED_HTTP_METHODS = new HashSet<Class<?>>() {
        private static final long serialVersionUID = 1L;

        {
            add(Get.class);
            add(Post.class);
        }
    };

    /**
     * Parse Http related annotation of a method.
     *
     * @param m
     * @return
     */
    public static Annotation getHttpMethodAnnotation(Method m) {
        for (Annotation a : m.getAnnotations()) {
            if (SUPPORTED_HTTP_METHODS.contains(a.annotationType())) {
                return a;
            }
        }
        return null;
    }

    /**
     * Parse parameter type of a http method.
     *
     * @param m
     * @return
     */
    public static List<MethodParam> parseParamTypes(Method m) {
        List<MethodParam> result = new ArrayList<MethodParam>();
        Class<?>[] paramTypes = m.getParameterTypes();
        Annotation[][] paramAnnotations = m.getParameterAnnotations();
        boolean hasModelParam = false, hasContextParam = false;

        for (int i = 0; i < paramTypes.length; ++i) {
            if (paramTypes[i] == Model.class) {
                if (hasModelParam) {
                    throw new IllegalArgumentException("Duplicated parameter type of [Model.class] in method: " + m);
                }
                result.add(ModelParam.Instance);
                hasModelParam = true;
            } else if (paramTypes[i] == Context.class) {
                if (hasContextParam) {
                    throw new IllegalArgumentException("Duplicated parameter type of [Context.class] in method: " + m);
                }
                result.add(ContextParam.Instance);
                hasContextParam = true;
            } else {
                Param param = null;
                for (Annotation a : paramAnnotations[i]) {
                    if (a.annotationType().equals(Param.class)) {
                        param = (Param) a;
                        break;
                    }
                }
                if (param == null) {
                    throw new IllegalArgumentException("Missing param annotation in method " + m);
                }
                result.add(new MethodParam(param.value(), paramTypes[i]));
            }
        }
        return result;
    }

    /**
     * Get value of a htt-method parameter.
     *
     * @param context
     * @param map
     * @param params
     * @return
     */
    public static Object[] getParamValues(Context context, Map<String, String> map, List<MethodParam> params) {
        List<Object> result = new ArrayList<Object>(params.size());
        for (MethodParam param : params) {
            if (param == ModelParam.Instance) {
                result.add(new Model());
            } else if (param == ContextParam.Instance) {
                result.add(context);
            } else {
                result.add(Converters.convert(param.clz(), map.get(param.name())));
            }
        }
        return result.toArray();
    }

    /**
     * Remove tailing '/'s of a url.
     *
     * @param url
     * @return
     */
    public static String removeTailSlash(String url) {
        if (url == null || url.length() == 0) {
            return "";
        }
        int i = url.length() - 1;
        for (; i >= 0; i--) {
            if (url.charAt(i) != '/') {
                break;
            }
        }
        if (i != url.length() - 1) {
            url = url.substring(0, i + 1);
        }
        return url;
    }

    /**
     * Remove heading '/'s of a url.
     *
     * @param url
     * @return
     */
    public static String removeHeadSlash(String url) {
        if (url == null || url.length() == 0) {
            return "";
        }
        int i = 0;
        for (; i < url.length(); i ++) {
            if (url.charAt(i) != '/') {
                break;
            }
        }
        if (i != 0) {
            url = url.substring(i);
        }
        return url;
    }

    /**
     * Remove heading and tailing '/'s of a url.
     *
     * @param url
     * @return
     */
    public static String removeHeadAndTailSlash(String url) {
        return removeTailSlash(removeHeadSlash(url));
    }

    public static void main(String[] args) {
        System.out.println(removeTailSlash("/"));
        System.out.println(removeTailSlash("/1"));
        System.out.println(removeTailSlash("/1/"));
        System.out.println(removeTailSlash("/1/1"));
        System.out.println(removeTailSlash("1/1/1"));

        System.out.println(removeHeadSlash("/"));
        System.out.println(removeHeadSlash("/1"));
        System.out.println(removeHeadSlash("/1/"));
        System.out.println(removeHeadSlash("/1/1"));
        System.out.println(removeHeadSlash("1/1/1"));

        System.out.println(removeHeadAndTailSlash("/"));
        System.out.println(removeHeadAndTailSlash("/1"));
        System.out.println(removeHeadAndTailSlash("/1/"));
        System.out.println(removeHeadAndTailSlash("/1/1"));
        System.out.println(removeHeadAndTailSlash("1/1/1"));
    }
}
