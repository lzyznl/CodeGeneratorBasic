package com.lzy.maker.generator.file;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import com.lzy.maker.model.DataModel;

import java.io.*;
import java.nio.charset.StandardCharsets;

/**
 * @author lzy
 * @date 2021-01-08
 * 动态代码生成器
 */
public class DynamicFileGenerator {


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
        FileOutputStream fileOutputStream = new FileOutputStream(outputPath+File.separator+GeneratorFileName);
        Writer output = new OutputStreamWriter(fileOutputStream,StandardCharsets.UTF_8);
        template.process(Model,output);
        output.close();
    }
}
