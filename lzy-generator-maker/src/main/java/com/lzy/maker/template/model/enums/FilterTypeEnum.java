package com.lzy.maker.template.model.enums;

import cn.hutool.core.util.ObjectUtil;

/**
 * @author lzy
 * @date 2024-01-29
 * 文件过滤规则枚举类
 */
public enum FilterTypeEnum {

    CONTAINS("包含","contains"),
    STARTS_WITH("前缀包含","startsWith"),
    ENDS_WITH("后缀包含","endsWith"),
    REGEX("正则匹配","regex"),
    EQUALS("相等","equals");


    private final String text;

    private final String value;

    FilterTypeEnum(String text, String value) {
        this.text = text;
        this.value = value;
    }
    public String getText() {
        return text;
    }

    public String getValue() {
        return value;
    }

    public static FilterTypeEnum getEnumByValue(String value){
        if(ObjectUtil.isEmpty(value)){
            return null;
        }
        for(FilterTypeEnum enumElement:FilterTypeEnum.values()){
            if(enumElement.getValue().equals(value)){
                return enumElement;
            }
        }
        return null;
    }
}
