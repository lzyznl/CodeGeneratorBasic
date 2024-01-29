package com.lzy.maker.template.model.enums;

import cn.hutool.core.util.ObjectUtil;

/**
 * @author lzy
 * @date 2024-01-29
 * 文件过滤规则-过滤范围枚举类
 */
public enum FilterRangeEnum {

    FILENAME("文件名称","fileName"),
    FILECONTENT("文件内容","fileContent");


    private final String text;

    private final String value;

    FilterRangeEnum(String text, String value) {
        this.text = text;
        this.value = value;
    }
    public String getText() {
        return text;
    }

    public String getValue() {
        return value;
    }

    public static FilterRangeEnum getEnumByValue(String value){
        if(ObjectUtil.isEmpty(value)){
            return null;
        }
        for(FilterRangeEnum enumElement: FilterRangeEnum.values()){
            if(enumElement.getValue().equals(value)){
                return enumElement;
            }
        }
        return null;
    }
}
