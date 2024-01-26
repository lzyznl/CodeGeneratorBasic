package com.lzy.model;

import lombok.Data;


/**
 * @author lzyaaa
 * @date 2024-01-08
 * MainTemplate模板数据
 */
@Data
public class DataModel {

    /**
     * 是否需要生成.gitignore文件
     */
    public boolean needGit  = true ;


    /**
    * 参数组
    */
    public MainParam mainParam = new MainParam();

    @Data
    public static class MainParam{
        /**
         * 作者注释
         */
        public String author  = "lzy" ;
        /**
         * 输出信息
         */
        public String outputText  = "result:" ;
    }


    /**
     * 是否开启循环
     */
    public boolean loop  = false ;



}
