/**
 * [Copyright] 
 * @author leo [leoyonn@gmail.com]
 * Aug 27, 2013 2:20:25 PM
 */
package me.lyso.http.utils;

import java.util.HashMap;
import java.util.Map;


/**
 * Simple converters for http-method-parameter.
 *
 * @author leo
 */
public class Converters {
    public static interface Converter {
        Object convert(String str);
    }
    
    private static final Converter IntConverter = new Converter() {
        @Override public Object convert(String str) { return Integer.valueOf(str); }
    };
    
    private static final Converter LongConverter = new Converter() {
        @Override public Object convert(String str) { return Long.valueOf(str); }
    };

    private static final Converter FloatConverter = new Converter() {
        @Override public Object convert(String str) { return Float.valueOf(str); }
    };

    private static final Converter DoubleConverter = new Converter() {
        @Override public Object convert(String str) { return Double.valueOf(str); }
    };

    private static final Converter BooleanConverter = new Converter() {
        @Override public Object convert(String str) { return Boolean.valueOf(str); }
    };

    private static final Converter DefaultConverter = new Converter() {
        @Override public Object convert(String str) { return str; }
    };
    
    private static final Map<Class<?>, Converter> converters = buildMap();
    private static Map<Class<?>, Converter> buildMap() {
        Map<Class<?>, Converter> map = new HashMap<Class<?>, Converter>();
        map.put(int.class, IntConverter);
        map.put(Integer.class, IntConverter);
        map.put(long.class, LongConverter);
        map.put(Long.class, LongConverter);
        map.put(float.class, FloatConverter);
        map.put(Float.class, FloatConverter);
        map.put(double.class, DoubleConverter);
        map.put(Double.class, DoubleConverter);
        map.put(boolean.class, BooleanConverter);
        map.put(Boolean.class, BooleanConverter);
        return map;
    }

    public static<T> T convert(Class<T> cls, String value) {
        Converter converter = converters.get(cls);
        if (converter == null) {
            converter = DefaultConverter;
        }
        return (T)converter.convert(value);
    }
}
