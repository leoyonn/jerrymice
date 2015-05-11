/**
 * Path.java
 * [CopyRight]
 * @author leo [leoyonn@gmail.com]
 * @date Sep 9, 2013 2:10:38 PM
 */

package me.lyso.http.annotation;

import me.lyso.http.base.Filter;

import java.lang.annotation.*;

/**
 * @author leo [leoyonn@gmail.com]
 */
@Target({
        ElementType.TYPE
})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Path {
    /**
     * Url prefix
     *
     * @return
     */
    String value();

    /**
     * Filter list on this path.
     *
     * @return
     */
    Class<? extends Filter>[] filters() default {};
}
