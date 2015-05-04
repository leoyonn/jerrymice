/**
 * [Copyright] 
 * @author leo [leoyonn@gmail.com]
 * Aug 23, 2013 4:11:46 PM
 */
package me.lyso.http.annotation;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Get {
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
