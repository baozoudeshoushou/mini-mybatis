package com.lin.mybatis.scripting;

import com.lin.mybatis.exceptions.MybatisException;
import com.lin.mybatis.util.Assert;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author linjiayi5
 * @Date 2023/5/23 19:38:56
 */
public class LanguageDriverRegistry {

    private final Map<Class<? extends LanguageDriver>, LanguageDriver> LANGUAGE_DRIVER_MAP = new HashMap<>();

    private Class<? extends LanguageDriver> defaultDriverClass;

    public void register(Class<? extends LanguageDriver> cls) {
        Assert.notNull(cls, "null is not a valid Language Driver");
        LANGUAGE_DRIVER_MAP.computeIfAbsent(cls, k -> {
            try {
                return k.getDeclaredConstructor().newInstance();
            } catch (Exception ex) {
                throw new MybatisException("Failed to load language driver for " + cls.getName(), ex);
            }
        });
    }

    public LanguageDriver getDriver(Class<? extends LanguageDriver> cls) {
        return LANGUAGE_DRIVER_MAP.get(cls);
    }

    public LanguageDriver getDefaultDriver() {
        return getDriver(getDefaultDriverClass());
    }

    public Class<? extends LanguageDriver> getDefaultDriverClass() {
        return defaultDriverClass;
    }

    public void setDefaultDriverClass(Class<? extends LanguageDriver> defaultDriverClass) {
        register(defaultDriverClass);
        this.defaultDriverClass = defaultDriverClass;
    }

}
