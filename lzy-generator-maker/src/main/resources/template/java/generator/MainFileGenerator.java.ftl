package ${basePackage}.generator;

import ${basePackage}.model.DataModel;
import freemarker.template.TemplateException;

import java.io.File;
import java.io.IOException;

/**
 * @author ${author}
 * @date ${createTime}
 * 总体代码文件生成器，包括动态文件生成以及静态文件生成
 */
public class MainFileGenerator {
    /**
     * 动静结合，生成代码文件
     * @param Model 插值数据
     */
    public static void doCodeGenerator(Object Model) throws TemplateException, IOException {

        //修改为从元信息配置文件中读取配置
        String InputRootPath = "${fileConfig.inputRootPath}";
        String OutputRootPath = "${fileConfig.outputRootPath}";

        String finalInputPath;
        String finalOutputPath;
        String templateName = "MainTemplate.java.ftl";
        String GeneratorTemplateName = "MainTemplate.java";

      <#list fileConfig.files as file>
        finalInputPath=InputRootPath+File.separator+"${file.inputPath}";
        finalOutputPath = OutputRootPath+File.separator+"${file.outputPath}";
      <#if file.generateType == "Dynamic">
        DynamicFileGenerator.dynamicGenerator(finalInputPath,finalOutputPath,Model);
      <#else >
        StaticFileGenerator.copFileByHutool(finalInputPath,finalOutputPath);
      </#if>
      </#list>

    }
}
