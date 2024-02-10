package com.lzy.maker.template.model;

import com.lzy.maker.meta.Meta;
import lombok.Data;

/**
 * @author lzy
 * @date 2024-02-06
 * 模板制作工具参数封装类
 */
@Data
public class TemplateMakerConfig {

    private Meta meta = new Meta();

    private String sourceRootPath;

    private TemplateMakerFileConfig fileConfig = new TemplateMakerFileConfig();

    private TemplateMakerModelConfig modelConfig = new TemplateMakerModelConfig();

    private TemplateMakerOutputConfig outputConfig = new TemplateMakerOutputConfig();

    private Long id;
}
