package org.example.generator;

import freemarker.template.TemplateException;
import org.example.model.MainTemplateConfig;

import java.io.File;
import java.io.IOException;

/**
 * @author lzy
 * @date 2024-01-06
 * 总体代码生成器，包括动态代码生成以及静态代码生成
 */
public class MainGenerator {

    public static void main(String[] args) throws TemplateException, IOException {
        MainTemplateConfig mainTemplateConfig = new MainTemplateConfig();
        mainTemplateConfig.setOutputText("程序运行结果是:");
        mainTemplateConfig.setAuthor("lzyyyy");
        mainTemplateConfig.setLoop(true);
        doCodeGenerator(mainTemplateConfig);
    }

    /**
     * 动静结合，生成代码文件
     * @param Model 插值数据
     */
    public static void doCodeGenerator(Object Model) throws TemplateException, IOException {
        String projectPath = System.getProperty("user.dir");
        String StaticInputFilePath = projectPath+File.separator+"lzy-generator-demo-project/acm-template";
        String StaticOutputFilePath = projectPath;
        //生成静态文件
        StaticGenerator.copFileByHutool(StaticInputFilePath,StaticOutputFilePath);
        //在静态文件的基础上生成动态文件
        String DynamicInputFilePath = projectPath+File.separator+"lzy-generator-basic/src/main/resources/template";
        String DynamicOutputFilePath = StaticOutputFilePath+File.separator+"acm-template/src/com/lzy/acm";
        String templateName = "MainTemplate.java.ftl";
        String generatorFileName = "MainTemplate.java";
        DynamicGenerator.dynamicGenerator(DynamicInputFilePath,DynamicOutputFilePath,templateName,generatorFileName,Model);
    }
}
