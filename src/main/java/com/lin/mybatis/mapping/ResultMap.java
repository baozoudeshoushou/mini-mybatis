package com.lin.mybatis.mapping;

import com.lin.mybatis.session.Configuration;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author linjiayi5
 */
public class ResultMap {

    private Configuration configuration;

    private String id;

    private Class<?> type;

    private List<ResultMapping> resultMappings;

    private Set<String> mappedColumns;

    private ResultMap() {

    }

    public static class Builder {

        private final ResultMap resultMap = new ResultMap();

        public Builder(Configuration configuration, String id, Class<?> type, List<ResultMapping> resultMappings) {
            resultMap.configuration = configuration;
            resultMap.id = id;
            resultMap.type = type;
            resultMap.resultMappings = resultMappings;
        }

        public ResultMap build() {
            resultMap.mappedColumns = new HashSet<>();
            return resultMap;
        }

    }

    public String getId() {
        return id;
    }

    public Class<?> getType() {
        return type;
    }

    public List<ResultMapping> getResultMappings() {
        return resultMappings;
    }

    public Set<String> getMappedColumns() {
        return mappedColumns;
    }

}
