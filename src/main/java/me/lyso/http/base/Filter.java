/**
 *
 * Filter.java
 * @date 5/11/15 19:39
 * @author leo [leoyonn@gmail.com]
 * [CopyRight] All Rights Reserved.
 */

package me.lyso.http.base;

import me.lyso.http.handler.Context;

/**
 * Filter interface defines what to do before and after a request being processed.
 *
 * @author leo
 */
public interface Filter {
    static Filter[] Empty = new Filter[0];

    /**
     * What to do before this request being processed.
     * <li>if returns null, continue filter with following filters and then process;</li>
     * <li>if returns a {@link Return} object, just stop processing and returns to client.</li>
     *
     * @param context
     * @param args
     * @return
     */
    Return pre(Context context, Object... args);

    /**
     * What to do after this request being processed, which would be called in reverse order against pre.
     *
     * @param context
     */
    void post(Context context);
}
