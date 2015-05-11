/**
 * [Copyright] 
 * @author leo [leoyonn@gmail.com]
 * Aug 23, 2013 4:11:46 PM
 */
package me.lyso.http.annotation;

import me.lyso.http.base.Filter;

import java.lang.annotation.*;

/**
 * @author leo
 */
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

    /**
     * Filter list on this method.
     *
     * @return
     */
    Class<? extends Filter>[] filters() default {};

    /**
     * Whether override handler-scope filters:
     * <p/>
     * <li>if true, just use mine filters.</li>
     * <li>if false, just append mine filters after global ones.</li>
     *
     * @return
     */
    boolean overrideFilters() default false;
}
