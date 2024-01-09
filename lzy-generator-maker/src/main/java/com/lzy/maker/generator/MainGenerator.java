package com.lzy.maker.generator;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.resource.ClassPathResource;
import cn.hutool.core.util.StrUtil;
import com.lzy.maker.generator.file.DynamicFileGenerator;
import com.lzy.maker.meta.Meta;
import com.lzy.maker.meta.MetaManger;
import freemarker.template.TemplateException;

import java.io.File;
import java.io.IOException;

/**
 * @author lzy
 * @date 2024-01-08
 * 总体代码生成类
 */
public class MainGenerator {

    public static void main(String[] args) throws TemplateException, IOException {
        Meta metaModel = MetaManger.getMetaModel();
        String basePackage = metaModel.getBasePackage();
        String parseBasePackage = StrUtil.join(File.separator,StrUtil.split(basePackage,"."));
        /**
         * 最终生成文件路径
         */
        String finalOutputPath;
        /**
         * 最终输入文件路径
         */
        String finalInputPath;

        //获取文件输出根路径
        String OutputRootPath = System.getProperty("user.dir");
        String OutPutProjectPath = OutputRootPath+File.separator+"generated"+File.separator+metaModel.getName()
                +File.separator+"src"+File.separator+"main"+File.separator+"java"+File.separator+parseBasePackage;
        if(!FileUtil.exist(OutPutProjectPath)){
            FileUtil.mkdir(OutPutProjectPath);
        }

        //获取文件的输入根路径
        ClassPathResource classPathResource = new ClassPathResource("");
        String resourceAbsolutePath = classPathResource.getAbsolutePath();
        String InputProjectPath = resourceAbsolutePath+File.separator+"template"+File.separator
                +"java";


        finalOutputPath = OutPutProjectPath+File.separator+"model";
        finalInputPath = InputProjectPath+File.separator+"model"+File.separator;
        String templateName = "DataModel.java.ftl";
        String generatedTemplateName = "DataModel.java";

        DynamicFileGenerator.dynamicGenerator(finalInputPath,finalOutputPath,templateName,generatedTemplateName,metaModel);

        /**
         * 生成命令执行路径
         */
        finalOutputPath = OutPutProjectPath+File.separator+"cli"+File.separator+"command";
        finalInputPath = InputProjectPath+File.separator+"cli"+File.separator+"command";

        DynamicFileGenerator.dynamicGenerator(finalInputPath,finalOutputPath,
                "ConfigCommand.java.ftl","ConfigCommand.java",metaModel);

        DynamicFileGenerator.dynamicGenerator(finalInputPath,finalOutputPath,
                "GeneratorCommand.java.ftl","GeneratorCommand.java",metaModel);

        DynamicFileGenerator.dynamicGenerator(finalInputPath,finalOutputPath,
                "ListCommand.java.ftl","ListCommand.java",metaModel);

        finalOutputPath = OutPutProjectPath+File.separator+"cli";
        finalInputPath = InputProjectPath+File.separator+"cli";

        DynamicFileGenerator.dynamicGenerator(finalInputPath,finalOutputPath,
                "CommandExecutor.java.ftl","CommandExecutor.java",metaModel);

    }
}
