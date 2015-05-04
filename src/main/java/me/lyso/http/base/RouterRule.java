/**
 * [Copyright] 
 * @author leo [leoyonn@gmail.com]
 * Aug 27, 2013 1:26:52 PM
 */
package me.lyso.http.base;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import me.lyso.http.utils.HttpMethodUtils;

/**
 * Router rule to define a url path and check if another url matches this rule.
 *
 * @author leo [leoyonn@gmail.com]
 * @author leo
 */
public class RouterRule {
    private static final Pattern DYNAMIC = Pattern.compile("^\\{(<.+>)?(.+)\\}$");

    private List<PathPart> parts;

    /**
     * Construct and init with a url #rule.
     *
     * @param rule
     */
    public RouterRule(String rule) {
        List<PathPart> pathParts = new ArrayList<PathPart>();
        rule = HttpMethodUtils.removeHeadAndTailSlash(rule);
        for (String part : rule.split("/")) {
            Matcher m = DYNAMIC.matcher(part);
            if (m.find()) {
                String constraint = m.group(1);
                String regex = constraint == null ? "(.+)" : constraint.replace('<', '(').replace('>', ')');
                pathParts.add(new DynamicPart(m.group(2), Pattern.compile(regex)));
            } else {
                pathParts.add(new StaticPart(part));
            }
        }
        this.parts = pathParts;
    }

    /**
     * Check if this #url matches the rule.
     *
     * @param url
     * @return null means bad match, empty collections means no parameters.
     */
    public Map<String, String> check(String url) {
        url = HttpMethodUtils.removeHeadAndTailSlash(url);
        String[] urlParts = url.split("/");
        if (urlParts.length != this.parts.size()) {
            return null;
        }
        Map<String, String> result = new HashMap<String, String>();
        for (int i = 0; i < urlParts.length; ++i) {
            PathPart pathPart = this.parts.get(i);
            String urlPart = urlParts[i];

            if (pathPart instanceof StaticPart) {
                if (!((StaticPart) pathPart).name.equals(urlPart)) {
                    return null;
                }
            } else if (pathPart instanceof DynamicPart) {
                DynamicPart dyPart = (DynamicPart) pathPart;
                Matcher m = dyPart.regex.matcher(urlPart);
                if (m.matches()) {
                    result.put(dyPart.name, m.group());
                } else {
                    return null;
                }
            }
        }
        return result;
    }

    @Override
    public String toString() {
        return "<parts:" + parts + ">";
    }

    /**
     * A part of a url Path(split by '/').
     *
     * @author leo
     */
    private static interface PathPart {
    }

    /**
     * A static part of a url Path.
     *
     * @author leo
     */
    private static class StaticPart implements PathPart {
        private final String name;

        private StaticPart(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return "<static:" + name + ">";
        }
    }

    /**
     * A dynamic part of a url Path.
     *
     * @author leo
     */
    private static class DynamicPart implements PathPart {
        private final String name;
        private final Pattern regex;

        private DynamicPart(String name, Pattern regex) {
            this.name = name;
            this.regex = regex;
        }

        @Override
        public String toString() {
            return "<dynamic:" + name + ":" + regex + ">";
        }

    }
}
