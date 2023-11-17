package com.lin.mybatis.reflection;

import com.lin.mybatis.annotations.Param;
import com.lin.mybatis.binding.MapperMethod;
import com.lin.mybatis.session.Configuration;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * @author linjiayi5
 */
public class ParamNameResolver {

    public static final String GENERIC_NAME_PREFIX = "param";

    /**
     * The key is the index and the value is the name of the parameter.<br />
     * The name is obtained from {@link Param} if specified. When {@link Param} is not specified, the parameter index is
     * used. Note that this index could be different from the actual index when the method has special parameters (i.e.
     * {@link RowBounds} or {@link ResultHandler}).
     * <ul>
     * <li>aMethod(@Param("M") int a, @Param("N") int b) -&gt; {{0, "M"}, {1, "N"}}</li>
     * <li>aMethod(int a, int b) -&gt; {{0, "0"}, {1, "1"}}</li>
     * <li>aMethod(int a, RowBounds rb, int b) -&gt; {{0, "0"}, {2, "1"}}</li>
     * </ul>
     */
    private final SortedMap<Integer, String> names;

    private boolean hasParamAnnotation;

    public ParamNameResolver(Configuration config, Method method) {
        final SortedMap<Integer, String> map = new TreeMap<>();
        final Class<?>[] paramTypes = method.getParameterTypes();
        Annotation[][] paramAnnotations = method.getParameterAnnotations();
        for (int i = 0; i < paramTypes.length; i++) {
            String name = null;
            for (Annotation annotation : paramAnnotations[i]) {
                if (annotation instanceof Param param) {
                    hasParamAnnotation = true;
                    name = param.value();
                    break;
                }
            }
            if (name == null) {
                // use the parameter index as the name ("0", "1", ...)
                name = String.valueOf(map.size());
            }
            map.put(i, name);
        }

        this.names = Collections.unmodifiableSortedMap(map);
    }

    /**
     * Returns parameter names referenced by SQL providers.
     * @return the names
     */
    public String[] getNames() {
        return names.values().toArray(new String[0]);
    }

    /**
     * A single non-special parameter is returned without a name. Multiple parameters are named using the naming rule. In
     * addition to the default names, this method also adds the generic names (param1, param2, ...).
     * @param args the args
     * @return the named params
     */
    public Object getNamedParams(Object[] args) {
        final int paramCount = names.size();
        if (args == null || paramCount == 0) {
            // 没有参数
            return null;
        }
        if (!hasParamAnnotation && paramCount == 1) {
            // 没有 @Param 并且只有一个参数
            return args[names.firstKey()];
        }

        int i = 0;
        // 参数名, 参数
        final Map<String, Object> param = new MapperMethod.ParamMap<>();
        for (Map.Entry<Integer, String> entry : names.entrySet()) {
            param.put(entry.getValue(), args[entry.getKey()]);
            // add generic param names (param1, param2, ...)
            final String genericParamName = GENERIC_NAME_PREFIX + (i + 1);
            // ensure not to overwrite parameter named with @Param
            if (!names.containsValue(genericParamName)) {
                param.put(genericParamName, args[entry.getKey()]);
            }
            i++;
        }

        return param;
    }

}
