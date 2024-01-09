package com.lzy.generator.file;

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
    public static void doCodeGenerator(Object Model) throws TemplateException, IOException {

        //修改为从元信息配置文件中读取配置
        String InputRootPath = "D:\Java_Project\Code\lzy-generator\lzy-generator-demo-project\acm-template-pro";
        String OutputRootPath = "D:\Java_Project\Code\lzy-generator\acm-template-pro";

        String finalInputPath;
        String finalOutputPath;
        String templateName = "MainTemplate.java.ftl";
        String GeneratorTemplateName = "MainTemplate.java";

        finalInputPath=InputRootPath+File.separator+"src/com/lzy/acm/MainTemplate.java.ftl";
        finalOutputPath = OutputRootPath+File.separator+"src/com/lzy/acm/MainTemplate.java";
        DynamicFileGenerator.dynamicGenerator(finalInputPath,finalOutputPath,templateName,GeneratorTemplateName,Model);
        finalInputPath=InputRootPath+File.separator+".gitignore";
        finalOutputPath = OutputRootPath+File.separator+".gitignore";
        StaticFileGenerator.copFileByHutool(finalInputPath,finalOutputPath);
        finalInputPath=InputRootPath+File.separator+"README.md";
        finalOutputPath = OutputRootPath+File.separator+"README.md";
        StaticFileGenerator.copFileByHutool(finalInputPath,finalOutputPath);

    }
}
