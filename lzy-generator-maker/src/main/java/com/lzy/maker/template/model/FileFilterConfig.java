package com.lzy.maker.template.model;

import lombok.Builder;
import lombok.Data;

/**
 * @author lzy
 * @date 2024-01-29
 * 文件过滤规则配置类
 */
@Data
@Builder
public class FileFilterConfig {

    /**
     * 文件过滤范围
     */
    private String range;

    /**
     * 文件过滤规则
     */
    private String rule;

    /**
     * 文件过滤值
     */
    private String value;
}
