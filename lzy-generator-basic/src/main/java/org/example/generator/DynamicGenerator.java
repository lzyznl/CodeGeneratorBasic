package org.example.generator;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.example.model.MainTemplateConfig;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

/**
 * @author lzy
 * @date 2021-01-06
 * 动态代码生成器
 */
public class DynamicGenerator {
    public static void main(String[] args) throws IOException, TemplateException {
        String projectPath = System.getProperty("user.dir");
        String filePath = projectPath+File.separator+"lzy-generator-basic"+File.separator+"src/main/resources/template";
        String outputFilePath = projectPath;
        String templateName = "MainTemplate.java.ftl";
        String GeneratorFileName = "MainTemplate.java";

        MainTemplateConfig mainTemplateConfig = new MainTemplateConfig();
        mainTemplateConfig.setOutputText("程序运行结果是:");
        mainTemplateConfig.setAuthor("lzy");
        mainTemplateConfig.setLoop(false);

        dynamicGenerator(filePath,outputFilePath,templateName,GeneratorFileName,mainTemplateConfig);
    }


    /**
     * 动态生成代码函数
     * @param inputPath 模板父文件路径
     * @param outputPath 生成文件路径
     * @param templateName 模板名称
     * @param GeneratorFileName 生成文件名称
     * @param Model 数据模型
     * @throws IOException
     * @throws TemplateException
     */
    public static void dynamicGenerator(String inputPath,String outputPath,String templateName,String GeneratorFileName,Object Model) throws IOException, TemplateException {
        Configuration cfg = new Configuration(Configuration.VERSION_2_3_31);
        cfg.setDirectoryForTemplateLoading(new File(inputPath));
        cfg.setDefaultEncoding("UTF-8");
        //获取到对应的模板
        Template template = cfg.getTemplate(templateName);

        //指定模板引擎生成的文件路径
        Writer output = new FileWriter(outputPath+File.separator+GeneratorFileName);
        template.process(Model,output);
        output.close();
    }
}
