/**
 * [Copyright] 
 * @author leo [leoyonn@gmail.com]
 * Aug 23, 2013 4:11:46 PM
 */
package me.lyso.http.annotation;

import java.lang.annotation.*;

/**
 * @author leo
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface Param {
    /**
     * The Url pattern
     *
     * @return
     */
    String value();

    /**
     * Whether this parameter is json formatted.
     *
     * @return
     */
    boolean json() default false;
}
