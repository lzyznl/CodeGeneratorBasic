package com.lzy.maker.template.model;

import lombok.Data;

import java.util.List;

/**
 * @author lzy
 * @date 2024-02-06
 * 模板生成工具模型配置类
 */
@Data
public class TemplateMakerModelConfig {

    private List<ModelInfoConfig> models;

    private ModelGroupConfig modelGroupConfig;


    @Data
    public static class ModelInfoConfig{
        private String fieldName;

        private String type;

        private String description;

        private Object defaultValue;


        private String abbr;

        //要替换的文本
        private String replaceText;
    }

    @Data
    public static class ModelGroupConfig{
        private String condition;

        private String groupKey;

        private String groupName;
    }
}
