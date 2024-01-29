package com.lzy.maker.template.model;

import lombok.Data;

import java.util.List;

/**
 * @author lzy
 * @date 2024-01-29
 * 模板制作工具所要过滤文件配置类
 */
@Data
public class TemplateMakerFileConfig {

    private List<FileInfoConfig> files;

    @Data
    public static class FileInfoConfig{
        private String path;

        private List<FileFilterConfig> filterConfigs;
    }
}
