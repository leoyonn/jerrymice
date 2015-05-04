/**
 * [Copyright] 
 * @author leo [leoyonn@gmail.com]
 * Aug 23, 2013 4:13:38 PM
 */
package me.lyso.http.annotation;

import java.lang.annotation.*;

/**
 *
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Post {
    /**
     * The Url pattern
     *
     * @return
     */
    String value();

    /**
     * Timeout in milliseconds for async.
     *
     * @return
     */
    long timeout() default 2000;
}
