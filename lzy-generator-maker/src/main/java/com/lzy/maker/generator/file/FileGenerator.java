package com.lzy.maker.generator.file;

import com.lzy.maker.generator.file.DynamicFileGenerator;
import freemarker.template.TemplateException;
import com.lzy.maker.model.DataModel;

import java.io.File;
import java.io.IOException;

/**
 * @author lzy
 * @date 2024-01-08
 * 总体代码生成器，包括动态代码生成以及静态代码生成
 */
public class FileGenerator {

    public static void main(String[] args) throws TemplateException, IOException {
        DataModel dataModel = new DataModel();
        dataModel.setOutputText("fact:");
        dataModel.setAuthor("lzyyyy");
        dataModel.setLoop(true);
        doCodeGenerator(dataModel);
    }

    /**
     * 动静结合，生成代码文件
     * @param Model 插值数据
     */
    public static void doCodeGenerator(Object Model) throws TemplateException, IOException {
        String projectPath = System.getProperty("user.dir");
        File parentFile = new File(projectPath).getParentFile();
        String StaticInputFilePath = parentFile.getPath()+File.separator+"lzy-generator-demo-project/acm-template";
        String StaticOutputFilePath = projectPath;
        //生成静态文件
        StaticFileGenerator.copFileByHutool(StaticInputFilePath,StaticOutputFilePath);
        //在静态文件的基础上生成动态文件
        String DynamicInputFilePath = projectPath+File.separator+"/src/main/resources/template";
        String DynamicOutputFilePath = StaticOutputFilePath+File.separator+"acm-template/src/com/lzy/acm";
        String templateName = "DataModel.java.ftl";
        String generatorFileName = "MainTemplate.java";
        DynamicFileGenerator.dynamicGenerator(DynamicInputFilePath,DynamicOutputFilePath,templateName,generatorFileName,Model);
    }
}
