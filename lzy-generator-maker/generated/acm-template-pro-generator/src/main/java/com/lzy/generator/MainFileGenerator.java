package com.lzy.generator;

import com.lzy.model.DataModel;
import freemarker.template.TemplateException;

import java.io.File;
import java.io.IOException;

/**
 * @author lzyaaa
 * @date 2024-01-08
 * 总体代码文件生成器，包括动态文件生成以及静态文件生成
 */
public class MainFileGenerator {
    /**
     * 动静结合，生成代码文件
     * @param Model 插值数据
     */
    public static void doCodeGenerator(DataModel Model) throws TemplateException, IOException {

        //修改为从元信息配置文件中读取配置
        String InputRootPath = ".source/acm-template-pro";
        String OutputRootPath = "generated";

        String finalInputPath;
        String finalOutputPath;

        boolean needGit = Model.needGit;
        String author = Model.author;
        String outputText = Model.outputText;
        boolean loop = Model.loop;


        finalInputPath=InputRootPath+File.separator+"src/com/lzy/acm/MainTemplate.java.ftl";
        finalOutputPath = OutputRootPath+File.separator+"src/com/lzy/acm/MainTemplate.java";
        DynamicFileGenerator.dynamicGenerator(finalInputPath,finalOutputPath,Model);


        if(needGit){
            finalInputPath=InputRootPath+File.separator+".gitignore";
            finalOutputPath = OutputRootPath+File.separator+".gitignore";
            StaticFileGenerator.copFileByHutool(finalInputPath,finalOutputPath);
        }


        finalInputPath=InputRootPath+File.separator+"README.md";
        finalOutputPath = OutputRootPath+File.separator+"README.md";
        StaticFileGenerator.copFileByHutool(finalInputPath,finalOutputPath);


    }
}
