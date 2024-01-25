package com.lzy.maker.meta;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author lzy
 * @date 2024-01-08
 * 元信息对应实体类
 */
@NoArgsConstructor
@Data
public class Meta {

    private String name;
    private String description;
    private String basePackage;
    private String version;
    private String author;
    private String createTime;
    private FileConfigDTO fileConfig;
    private ModelConfigDTO modelConfig;

    @NoArgsConstructor
    @Data
    public static class FileConfigDTO {
        private String inputRootPath;
        private String outputRootPath;
        private String sourceRootPath;
        private String Type;
        private List<FilesInfo> files;

        @NoArgsConstructor
        @Data
        public static class FilesInfo {
            private String inputPath;
            private String outputPath;
            private String type;
            private String generateType;
            private String condition;
            private String groupKey;
            private String groupDescription;
            private List<FilesInfo> files;
        }
    }

    @NoArgsConstructor
    @Data
    public static class ModelConfigDTO {
        private List<ModelsInfo> models;

        @NoArgsConstructor
        @Data
        public static class ModelsInfo {
            private String fieldName;
            private String type;
            private String groupKey;
            private String groupName;
            private String condition;
            private List<ModelsInfo> models;
            private String description;
            private Object defaultValue;
            private String abbr;
            private String fullName;

            //设置一个中间参数，用户无需进行自定义，便于部分实现部分逻辑
            private String mediumArgs;
        }
    }
}
