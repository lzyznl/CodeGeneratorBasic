package com.lzy.maker.template.model;

import lombok.Data;

import java.util.List;

/**
 * @author lzy
 * @date 2024-01-29
 * 模板制作工具文件配置类
 */
@Data
public class TemplateMakerFileConfig {

    private List<FileInfoConfig> files;

    private FileGroupInfoConfig fileGroupInfoConfig;

    @Data
    public static class FileInfoConfig{
        private String path;

        private String condition;

        private List<FileFilterConfig> filterConfigs;
    }

    @Data
    public static class FileGroupInfoConfig{
        private String condition;

        private String groupKey;

        private String groupDescription;
    }
}
