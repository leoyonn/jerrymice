/**
 *
 * DemoFIlters.java
 * @date 5/11/15 22:11
 * @author leo [liuy@xiaomi.com]
 * [CopyRight] All Rights Reserved.
 */

package me.lyso.http.demo;

import me.lyso.http.base.Filter;
import me.lyso.http.base.Return;
import me.lyso.http.base.Return.Type;
import me.lyso.http.handler.Context;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.Cookie;
import java.util.Arrays;

/**
 * @author leo
 */
public class DemoFilters {
    public static class LogFilter implements Filter {
        private static final Logger logger = LoggerFactory.getLogger(LogFilter.class);
        @Override
        public Return pre(Context context, Object... args) {
            logger.info("Got request {} with args {}", context.request().getContextPath(), Arrays.asList(args));
            context.request().setAttribute("logged", true);
            return null;
        }

        @Override
        public void post(Context context) {
            logger.info("Post processing request {}", context.request().getContextPath());
        }
    }

    public static class CookieCheckFilter implements Filter {
        private static final Logger logger = LoggerFactory.getLogger(CookieCheckFilter.class);
        static final String CheckName = "AuthToken";

        @Override
        public Return pre(Context context, Object... args) {
            logger.info("Got request {} with args {}", context.request().getContextPath(), Arrays.asList(args));
            for (Cookie cookie : context.request().getCookies()) {
                if (cookie.getName().equals(CheckName) && cookie.getValue() != null && cookie.getValue().length() > 0) {
                    context.request().setAttribute("token", cookie.getValue());
                    logger.info("Ok, cookie check passed: <{}:{}>", cookie.getName(), cookie.getValue());
                    return null;
                }
            }
            logger.info("Oh, cookie check failed!");
            context.response().setStatus(403);
            return Return.build(Type.Error, "No auth token found in cookie!");
        }

        @Override
        public void post(Context context) {
            logger.info("Post processing request {}", context.request().getContextPath());
        }
    }

}
