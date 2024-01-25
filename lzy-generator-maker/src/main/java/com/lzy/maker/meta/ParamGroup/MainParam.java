package com.lzy.maker.meta.ParamGroup;

import lombok.Data;

/**
 * @author lzy
 * @date 2024-01-25
 * 用户自定义参数组类（比如MySQL相关配置可以视为一个参数组，Redis相关配置可以视为一个参数组）
 */
@Data
public class MainParam {

    private String author;

    private String outputText;

}
