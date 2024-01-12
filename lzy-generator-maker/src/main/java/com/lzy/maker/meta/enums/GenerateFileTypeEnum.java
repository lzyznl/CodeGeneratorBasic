package com.lzy.maker.meta.enums;

/**
 * @author lzy
 * @date 2024-01-12
 * 生成文件类型枚举类
 */
public enum GenerateFileTypeEnum {
    DYNAMIC("动态","Dynamic"),
    STATIC("静态","static");

    private final String text;

    private final String value;

    GenerateFileTypeEnum(String text,String value) {
        this.text = text;
        this.value = value;
    }
    public String getText() {
        return text;
    }

    public String getValue() {
        return value;
    }
}
