package com.lin.mybatis.builder;

import com.lin.mybatis.mapping.ResultMap;
import com.lin.mybatis.mapping.ResultMapping;

import java.util.List;

/**
 * @author linjiayi5
 */
public class ResultMapResolver {

    private final MapperBuilderAssistant assistant;
    private String id;
    private Class<?> type;
    private List<ResultMapping> resultMappings;

    public ResultMapResolver(MapperBuilderAssistant assistant, String id, Class<?> type, List<ResultMapping> resultMappings) {
        this.assistant = assistant;
        this.id = id;
        this.type = type;
        this.resultMappings = resultMappings;
    }

    public ResultMap resolve() {
        return assistant.addResultMap(id, type, resultMappings);
    }

}
