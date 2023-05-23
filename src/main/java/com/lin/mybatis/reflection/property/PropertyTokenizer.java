package com.lin.mybatis.reflection.property;

import java.util.Iterator;

/**
 * @Author linjiayi5
 * @Date 2023/5/11 14:27:53
 */
public class PropertyTokenizer implements Iterator<PropertyTokenizer> {

    private String name;

    private final String children;

    private final String indexedName;

    private String index;

    public PropertyTokenizer(String fullName) {
        // a[0].b.c
        int delim = fullName.indexOf('.');
        if (delim > -1) {
            // name = a[0]
            name = fullName.substring(0, delim);
            // children = b.c
            children = fullName.substring(delim + 1);
        } else {
            name = fullName;
            children = null;
        }
        // indexedName = a[0]
        indexedName = name;
        delim = name.indexOf('[');
        if (delim > -1) {
            // index = 0
            index = name.substring(delim + 1, name.length() - 1);
            // name = a
            name = name.substring(0, delim);
        }
    }

    public String getName() {
        return name;
    }

    public String getIndex() {
        return index;
    }

    public String getIndexedName() {
        return indexedName;
    }

    public String getChildren() {
        return children;
    }

    @Override
    public boolean hasNext() {
        return children != null;
    }

    @Override
    public PropertyTokenizer next() {
        return new PropertyTokenizer(children);
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException(
                "Remove is not supported, as it has no meaning in the context of properties.");
    }

}
