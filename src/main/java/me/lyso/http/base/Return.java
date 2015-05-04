/**
 * Return.java
 * [CopyRight]
 * @author leo [liuy@xiaomi.com]
 * @date Sep 10, 2013 2:57:16 PM
 */
package me.lyso.http.base;

import me.lyso.http.handler.Context;

import java.io.IOException;

import javax.servlet.ServletException;

/**
 * Defines return type and value with {@link Model} of an http-request.
 *
 * @author leo
 */
public class Return {
    private final Type type;
    private final boolean async;
    private String value;
    private Model model;

    private Return(Type type, String value, Model model, boolean async) {
        this.type = type;
        this.value = value;
        this.async = async;
        this.model = model;
    }

    public Return setModel(Model model) {
        this.model = model;
        return this;
    }

    public Return setValue(String value) {
        this.value = value;
        return this;
    }

    public Type type() {
        return type;
    }

    public String value() {
        return value;
    }
    
    public Model model() {
        return model;
    }

    public boolean async() {
        return async;
    }

    @Override
    public String toString() {
        return "<type:" + type + ", value:" + value + ", async:" + async + ", model:" + model + ">";
    }

    /**
     * Build synchronized returning result with #type and #value, for which the framework will call {@link #complete(Context)}.
     *
     * @param type
     * @param value
     * @return
     */
    public static Return build(Type type, String value) {
        return build(type, value, null, false);
    }

    public static Return build(Type type, String value, boolean async) {
        return build(type, value, null, async);
    }

    /**
     * Build returning result with #type and #value.
     * If async == false, the framework will call {@link #complete(Context)}.
     * Or else, the user-defined-api calls {@link #complete(Context)}.
     *
     * @param type
     * @param value
     * @param model
     * @param async
     * @return
     */
    public static Return build(Type type, String value, Model model, boolean async) {
        if (type == Type.WebPage && value.charAt(0) != '/') {
            value = "/" + value;
        }
        return new Return(type, value, model, async);
    }

    /**
     * Process returning result into request and response, such as write value into response or use request to dispatch.
     *
     * @param context
     * @throws IOException
     * @throws ServletException
     */
    public void complete(Context context) throws IOException, ServletException {
        if (model != null) {
            model.dumpTo(context.request());
        }
        context.response().setContentType(type.contentType);
        context.response().setCharacterEncoding("UTF-8");
        switch (type) {
            case Text: // fall through
            case Plain:
            case Html:
            case Json:
            case Xml: {
                context.ok(value);
                break;
            }
            case Forward: // fall through
            case Redirect: {
                context.redirect(value);
                return;
            }
            case WebPage: {
                context.web(value);
                return;
            }
            case Error: {
                context.error(value);
                break;
            }
        }
    }

    /**
     * Return type.
     *
     * @author leo
     */
    public static enum Type {
        Text("text/plain"),
        Plain   ("text/plain"),
        Json    ("application/json"),
        Xml     ("text/xml"),
        Html    ("text/html"),
        WebPage ("text/html"),
        Redirect("text/html"),
        Forward ("text/html"),
        Error   ("text/html");

        private final String contentType;

        Type(String contentType) {
            this.contentType = contentType;
        }

        public String contentType() {
            return contentType;
        }
    }
}
