package ${basePackage}.generator;


import cn.hutool.core.io.FileUtil;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

import java.io.*;
import java.nio.charset.StandardCharsets;

/**
 * @author ${author}
 * @date ${createTime}
 * 动态代码生成器
 */
public class DynamicFileGenerator {


    /**
     * 动态生成代码函数
     * @param inputPath 模板路径
     * @param outputPath 生成文件路径
     * @param Model 数据模型
     * @throws IOException
     * @throws TemplateException
     */
    public static void dynamicGenerator(String inputPath,String outputPath,Object Model) throws IOException, TemplateException {
        Configuration cfg = new Configuration(Configuration.VERSION_2_3_31);

        String inputParentPath = new File(inputPath).getParentFile().getPath();
        cfg.setDirectoryForTemplateLoading(new File(inputParentPath));
        cfg.setDefaultEncoding("UTF-8");
        //获取到对应的模板
        String templateName = new File(inputPath).getName();
        Template template = cfg.getTemplate(templateName);

        if(!FileUtil.exist(outputPath)){
            FileUtil.touch(outputPath);
        }
        //指定模板引擎生成的文件路径
        FileOutputStream fileOutputStream = new FileOutputStream(outputPath);
        Writer output = new OutputStreamWriter(fileOutputStream,StandardCharsets.UTF_8);
        template.process(Model,output);
        output.close();
    }
}
