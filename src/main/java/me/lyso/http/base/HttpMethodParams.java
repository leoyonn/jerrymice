/**
 * HttpMethodParams.java
 * [CopyRight]
 * @author leo [liuy@xiaomi.com]
 * @date Sep 10, 2013 6:40:42 PM
 */
package me.lyso.http.base;

import me.lyso.http.base.MethodParam.ContextParam;
import me.lyso.http.base.MethodParam.ModelParam;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

/**
 * An http method with parameters and {@link RouterRule}s.
 *
 * @author leo
 */
public class HttpMethodParams {
    private final Method method;
    private final List<MethodParam> params;
    private final int modelParamIndex;
    private final int contextParamIndex;
    private final String httpType;
    private final RouterRule rule;
    private final long timeout;

    public HttpMethodParams(Method method, List<MethodParam> params, String httpType, RouterRule rule, long timeout) {
        this.method = method;
        this.params = params;
        this.httpType = httpType;
        this.rule = rule;
        int midx = -1, cidx = -1;
        for (int i = 0; i < params.size(); i++) {
            if (midx >= 0 && cidx >= 0) {
                break;
            } else if (midx < 0 && params.get(i) == ModelParam.Instance) {
                midx = i;
            } else if (cidx < 0 && params.get(i) == ContextParam.Instance) {
                cidx = i;
            }
        }
        this.modelParamIndex = midx;
        this.contextParamIndex = cidx;
        this.timeout = timeout;
    }

    public Map<String, String> checkRule(String url) {
        return rule.check(url);
    }

    public Object invoke(Object obj, Object... args) {
        try {
            return method.invoke(obj, args);
        } catch (Exception ex) { // throws IllegalArgumentException, IllegalAccessException, InvocationTargetException 
            throw new IllegalArgumentException("Bad input of argument", ex);
        }
    }

    public Method method() {
        return method;
    }

    public List<MethodParam> params() {
        return params;
    }

    public String httpType() {
        return httpType;
    }

    public RouterRule rule() {
        return rule;
    }

    public int modelParamIndex() {
        return modelParamIndex;
    }

    public int contextParamIndex() {
        return contextParamIndex;
    }

    public long timeout() {
        return timeout;
    }

    @Override
    public String toString() {
        return "<m:" + method.getName() + "(...), params:" + params + ", type:" + httpType + ", rule:" + rule + ">";
    }
}
