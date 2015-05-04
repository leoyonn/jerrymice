/**
 * Path.java
 * [CopyRight]
 * @author leo [liuy@xiaomi.com]
 * @date Sep 9, 2013 2:10:38 PM
 */

package me.lyso.http.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author leo
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
}
