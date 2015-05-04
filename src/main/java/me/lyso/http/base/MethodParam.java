/**
 * MethodParam.java
 * [CopyRight]
 * @author leo [liuy@xiaomi.com]
 * @date Sep 10, 2013 6:41:29 PM
 */
package me.lyso.http.base;

import me.lyso.http.handler.Context;

/**
 * An http method parameter description.
 *
 * @author leo
 */
public class MethodParam {
    private final String name;
    private final Class<?> clz;

    public MethodParam(String name, Class<?> clz) {
        this.name = name;
        this.clz = clz;
    }

    public String name() {
        return name;
    }

    public Class<?> clz() {
        return clz;
    }

    @Override
    public String toString() {
        return "<name:" + name + ", clz:" + clz + ">";
    }

    /**
     * A method can have only 1 parameter of type {@link Model}.
     *
     * @author leo
     */
    public static class ModelParam extends MethodParam {
        private ModelParam() {
            super("model", Model.class);
        }

        public static final ModelParam Instance = new ModelParam();
    }

    /**
     * A method can have only 1 parameter of type {@link Context}.
     *
     * @author leo
     */
    public static class ContextParam extends MethodParam {
        private ContextParam() {
            super("context", Context.class);
        }

        public static final ContextParam Instance = new ContextParam();
    }
}
