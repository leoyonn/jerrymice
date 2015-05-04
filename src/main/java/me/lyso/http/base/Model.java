/**
 * Model.java
 * [CopyRight]
 * @author leo [liuy@xiaomi.com]
 * @date Sep 10, 2013 11:53:09 AM
 */
package me.lyso.http.base;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

/**
 * A model(data map) for dumping variables into request for web page.
 *
 * @author leo
 */
public class Model {
    Map<String, Object> map = new HashMap<String, Object>();

    public Model add(String k, Object v) {
        return put(k, v);
    }

    public Model put(String k, Object v) {
        map.put(k, v);
        return this;
    }

    public void dumpTo(HttpServletRequest request) {
        for (Map.Entry<String, Object> e : map.entrySet()) {
            request.setAttribute(e.getKey(), e.getValue());
        }
    }

    @Override
    public String toString() {
        return map.toString();
    }
}
