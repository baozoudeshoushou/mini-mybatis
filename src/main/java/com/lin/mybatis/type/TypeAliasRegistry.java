package com.lin.mybatis.type;

import com.lin.mybatis.exceptions.MybatisException;
import com.lin.mybatis.io.Resources;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.ResultSet;
import java.util.*;

/**
 * @author linjiayi5
 */
public class TypeAliasRegistry {

    private final Map<String, Class<?>> typeAliases = new HashMap<>();

    public TypeAliasRegistry() {
        registerAlias("string", String.class);

        registerAlias("byte", Byte.class);
        registerAlias("char", Character.class);
        registerAlias("character", Character.class);
        registerAlias("long", Long.class);
        registerAlias("short", Short.class);
        registerAlias("int", Integer.class);
        registerAlias("integer", Integer.class);
        registerAlias("double", Double.class);
        registerAlias("float", Float.class);
        registerAlias("boolean", Boolean.class);

        registerAlias("byte[]", Byte[].class);
        registerAlias("char[]", Character[].class);
        registerAlias("character[]", Character[].class);
        registerAlias("long[]", Long[].class);
        registerAlias("short[]", Short[].class);
        registerAlias("int[]", Integer[].class);
        registerAlias("integer[]", Integer[].class);
        registerAlias("double[]", Double[].class);
        registerAlias("float[]", Float[].class);
        registerAlias("boolean[]", Boolean[].class);

        registerAlias("_byte", byte.class);
        registerAlias("_char", char.class);
        registerAlias("_character", char.class);
        registerAlias("_long", long.class);
        registerAlias("_short", short.class);
        registerAlias("_int", int.class);
        registerAlias("_integer", int.class);
        registerAlias("_double", double.class);
        registerAlias("_float", float.class);
        registerAlias("_boolean", boolean.class);

        registerAlias("_byte[]", byte[].class);
        registerAlias("_char[]", char[].class);
        registerAlias("_character[]", char[].class);
        registerAlias("_long[]", long[].class);
        registerAlias("_short[]", short[].class);
        registerAlias("_int[]", int[].class);
        registerAlias("_integer[]", int[].class);
        registerAlias("_double[]", double[].class);
        registerAlias("_float[]", float[].class);
        registerAlias("_boolean[]", boolean[].class);

        registerAlias("date", Date.class);
        registerAlias("decimal", BigDecimal.class);
        registerAlias("bigdecimal", BigDecimal.class);
        registerAlias("biginteger", BigInteger.class);
        registerAlias("object", Object.class);

        registerAlias("date[]", Date[].class);
        registerAlias("decimal[]", BigDecimal[].class);
        registerAlias("bigdecimal[]", BigDecimal[].class);
        registerAlias("biginteger[]", BigInteger[].class);
        registerAlias("object[]", Object[].class);

        registerAlias("map", Map.class);
        registerAlias("hashmap", HashMap.class);
        registerAlias("list", List.class);
        registerAlias("arraylist", ArrayList.class);
        registerAlias("collection", Collection.class);
        registerAlias("iterator", Iterator.class);

        registerAlias("ResultSet", ResultSet.class);
    }

    public void registerAlias(String alias, Class<?> value) {
        if (alias == null) {
            throw new MybatisException("The parameter alias cannot be null");
        }
        String key = alias.toLowerCase(Locale.ENGLISH);
        if (typeAliases.containsKey(key) && typeAliases.get(key) != null && !typeAliases.get(key).equals(value)) {
            throw new MybatisException("The alias '" + alias + "' is already mapped to the value '" + typeAliases.get(key).getName() + "'.");
        }
        typeAliases.put(key, value);
    }

    @SuppressWarnings("unchecked")
    public <T> Class<? extends T> resolveAlias(String alias) {
        try {
            if (alias == null) {
                return null;
            }

            String key = alias.toLowerCase(Locale.ENGLISH);
            Class<T> value;
            if (typeAliases.containsKey(key)) {
                value = (Class<T>) typeAliases.get(key);
            }
            else {
                value = (Class<T>) Resources.classForName(alias);
            }

            return value;
        }
        catch (ClassNotFoundException e) {
            throw new MybatisException("Could not resolve type alias '" + alias + "'.  Cause: " + e, e);
        }
    }

}
