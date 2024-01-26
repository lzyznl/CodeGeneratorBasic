package com.lzy.maker.meta.enums;

/**
 * @author lzy
 * @date 2024-01-12
 * 模型参数类型枚举类
 */
public enum FieldTypeEnum {

    STRING("String","String"),
    BOOLEAN("Boolean","boolean");

    private final String text;

    private final String value;

    FieldTypeEnum(String text, String value) {
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
