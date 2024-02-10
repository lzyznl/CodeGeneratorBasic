package com.lzy.maker.template.model;

import lombok.Data;

/**
 * @author lzy
 * @date 2024-02-10
 * 模板生成工具输出配置类
 */
@Data
public class TemplateMakerOutputConfig {

    /**
     * 是否删除外层文件中重复的组内配置文件
     */
    private Boolean removeGroupFileFromRoot = true;
}
