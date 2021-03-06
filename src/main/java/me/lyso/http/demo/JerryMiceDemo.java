/**
 * HttpServerTest.java
 * [CopyRight]
 * @author leo [leoyonn@gmail.com]
 * @date Sep 9, 2013 5:47:32 PM
 */
package me.lyso.http.demo;

import com.google.gson.Gson;
import me.lyso.http.annotation.*;
import me.lyso.http.base.Model;
import me.lyso.http.base.Return;
import me.lyso.http.base.Return.Type;
import me.lyso.http.demo.DemoFilters.CookieCheckFilter;
import me.lyso.http.demo.DemoFilters.LogFilter;
import me.lyso.http.handler.BaseHttpHandler;
import me.lyso.http.handler.Context;
import me.lyso.http.server.HttpServer;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.Part;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.Date;

/**
 * Typical using of jerrymice.
 *
 * @author leo [leoyonn@gmail.com]
 */
@Path(value = "/demo", filters = LogFilter.class)
@WebServlet(asyncSupported = true)
public class JerryMiceDemo extends BaseHttpHandler {
    private static final long serialVersionUID = 1L;

    @Get(value = "/hello/{name}")
    public Return hello(@Param("name") String name) {
        return Return.build(Type.Plain, "Hello, your name in url is: " + name);
    }

    @Get("hello")
    public String hello2(@Param("name") String name) {
        return "Hello, your name in query string is: " + name;
    }

    @Get("/hello/{<\\d+>id}/{any}")
    public String getId(@Param("id") long id, @Param("any") Object any) {
        return String.format("Hello, got your [%d] and any string [%s]", id, any);
    }

    static class AnyObject {
        public String name;
        public long value;
        public transient String desc;

        @Override
        public String toString() {
            return new Gson().toJson(this);
        }
    }

    @Get("hellojson")
    public String helloJson(@Param(value = "json", json = true) AnyObject any) {
        return "Hello, your json data in query string is: " + any;
    }

    @Get("/")
    public Return index() {
        return Return.build(Type.WebPage, "/webroot/index.jsp");
    }

    @Get(value = "/jsp")
    public Return jsp(final Model model, final Context context) {
        model.add("v", new Date());
        return Return.build(Type.WebPage, "/webroot/test.jsp", model, false); // 同步
    }

    @Get(value = "/async/{param}", timeout = 500)
    public Return async(final Context context, final Model model, @Param("param") String param) {
        final Return ret = Return.build(Type.WebPage, "/webroot/test.jsp", model, true); // 异步
        async(ret, context, param); // 在这里自己调用 ret.complete(context);
        return ret;
    }

    @Post(value = "/async2", timeout = 2500)
    public Return async2(final Context context, final Model model, @Param("param") String param) {
        final Return ret = Return.build(Type.WebPage, "/webroot/test.jsp", model, true);
        async(ret, context, param);
        return ret;
    }

    private void async(final Return ret, final Context context, final String param) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String ex = null;
                byte[] bytes = new byte[1024];
                try {
                    System.out.println("Read: " + context.inputStream().read(bytes, 0, bytes.length));
                } catch (IOException e) {
                    ex += e.toString();
                }
                try {
                    Thread.sleep(1500);
                } catch (InterruptedException e) {
                    ex += e.toString();
                }
                System.out.println("request: " + context.request());
                ret.model().add("v", "Slept 1000ms for " + context.request().getPathInfo()
                        + "<br/>Got input-param: " + param
                        + "<br/>Got input-stream: " + new String(bytes, Charset.forName("UTF-8"))
                        + "<br/>Has exception? " + ex);
                try {
                    ret.complete(context);
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (ServletException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }


    @Post("/file")
    public String file(final Context context) throws IOException, ServletException {
        Part partFile = context.request().getPart("file");
        // read file content, assumes text-file. Normally you don't need to do this, just write data to file.
        BufferedReader file = new BufferedReader(new InputStreamReader(partFile.getInputStream()));
        String line = null;
        StringBuilder result = new StringBuilder();
        while (null != (line = file.readLine())) {
            result.append(line).append("\n");
        }
        return "<html><body><h1>Content of file [" + partFile.getSubmittedFileName() + "]</h1><p>"
                + result.toString().replaceAll("\n", "<br>") + "</p></body></html>";
    }

    // ============================================= filter related ===================================================
    @Get(value = "/filter/fakelogin")
    public Return fakeLogin(Context context, Model model, @Param("user") String user, @Param("pass") String pass) {
        String fakeToken = "token-" + user + ":" + pass;
        context.response().addCookie(new Cookie(CookieCheckFilter.CheckName, fakeToken));
        model.add("info", "Ok, fake login done, got token:" + fakeToken);
        return Return.build(Type.WebPage, "/demo");
    }

    @Get(value = "/filter/fakelogout")
    public Return fakeLogout(Context context, Model model) {
        context.response().addCookie(new Cookie(CookieCheckFilter.CheckName, null));
        model.add("info", "Ok, fake logout done!");
        return Return.build(Type.WebPage, "/demo");
    }

    @Get(value = "/filter/auth", filters = CookieCheckFilter.class, overrideFilters = false)
    public Return authFilter(Context context, Model model) {
        model.add("info", "Ok, You got here, which means you have loginned! Your token: "
                + context.request().getAttribute("token") + "; logged: " + context.request().getAttribute("logged"));
        return Return.build(Type.WebPage, "/demo");
    }

    @Get(value = "/filter/auth-dont-log", filters = CookieCheckFilter.class, overrideFilters = true)
    public Return authFilterDontLog(Context context, Model model) {
        return authFilter(context, model);
    }

    public static void main(String[] args) throws Exception {
        new HttpServer(8081).addHandler(JerryMiceDemo.class).start();
        System.out.println("Cool, main goes here!");
    }
}

