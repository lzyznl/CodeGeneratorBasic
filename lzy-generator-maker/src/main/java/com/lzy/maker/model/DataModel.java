package com.lzy.maker.model;

import lombok.Data;

/**
 * @author lzy
 * @date 2024-01-08
 * MainTemplate模板数据
 */
@Data
public class DataModel {

    public Boolean needGit;

    /**
     * 作者信息
     */
    public String author;


    /**
     * 修改程序输出提示信息
     */
    public String outputText;

    /**
     * 修改程序是否需要循环输入
     */
    public Boolean loop;

}
