package com.lzy.model;

import lombok.Data;

/**
 * @author lzy
 * @date 2021-01-06
 * MainTemplate模板数据
 */
@Data
public class MainTemplateConfig {

    /**
     * 作者信息
     */
    private String author;


    /**
     * 修改程序输出提示信息
     */
    private String outputText;

    /**
     * 修改程序是否需要循环输入
     */
    private Boolean loop;

}
