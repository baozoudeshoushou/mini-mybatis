package com.lin.mybatis.entity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author linjiayi5
 * @Date 2023/5/23 16:04:02
 */
public class RichType {

    private RichType richType;

    private String richField;

    private String richProperty;

    private Map richMap = new HashMap<>();

    private List richList = new ArrayList<>() {
        private static final long serialVersionUID = 1L;
        {
            add("bar");
        }
    };

    public RichType getRichType() {
        return richType;
    }

    public void setRichType(RichType richType) {
        this.richType = richType;
    }

    public String getRichProperty() {
        return richProperty;
    }

    public void setRichProperty(String richProperty) {
        this.richProperty = richProperty;
    }

    public List getRichList() {
        return richList;
    }

    public void setRichList(List richList) {
        this.richList = richList;
    }

    public Map getRichMap() {
        return richMap;
    }

    public void setRichMap(Map richMap) {
        this.richMap = richMap;
    }

}
