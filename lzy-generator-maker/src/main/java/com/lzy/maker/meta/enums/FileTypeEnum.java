package com.lzy.maker.meta.enums;

/**
 * @author lzy
 * @date 2021-01-12
 * 文件类型枚举类
 */
public enum FileTypeEnum {

    DIR("文件夹","dir"),
    FILE("文件","file"),

    GROUP("文件组","group");

    private final String text;

    private final String value;

    FileTypeEnum(String text,String value) {
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
