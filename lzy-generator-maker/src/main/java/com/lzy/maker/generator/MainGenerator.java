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

        //获取输出根路径
        String OutputRootPath = System.getProperty("user.dir");
        String OutPutProjectPath = OutputRootPath+File.separator+"generated"+File.separator+metaModel.getName();
        if(!FileUtil.exist(OutPutProjectPath)){
            FileUtil.mkdir(OutPutProjectPath);
        }

        String basePackage = metaModel.getBasePackage();
        String parseBasePackage = StrUtil.join(File.separator,StrUtil.split(basePackage,"."));

        /**
         * 生成model输出路径
         */
        String finalOutputPath = OutPutProjectPath+File.separator+"src"+File.separator
                +"main"+File.separator+"java"+File.separator+parseBasePackage+File.separator+"model";
        String templateName = "MainTemplate.java.ftl";
        String generatedTemplateName = "DataModel.java";

        /**
         * 生成命令执行路径
         */
        String CommandFinalOutputPath = OutPutProjectPath+File.separator+"src"+File.separator
                +"main"+File.separator+"java"+File.separator+parseBasePackage+File.separator+"cli"+File.separator+"command";
        String CommandExecutorOutputPath = OutPutProjectPath+File.separator+"src"+File.separator
                +"main"+File.separator+"java"+File.separator+parseBasePackage+File.separator+"cli";

        //获取模板的输入路径
        ClassPathResource classPathResource = new ClassPathResource("");
        String resourceAbsolutePath = classPathResource.getAbsolutePath();
        String CommandFinalInputPath = resourceAbsolutePath+File.separator+"template"+File.separator
                +"java"+File.separator+"cli"+File.separator+"command";
        String CommandExecutorPath = resourceAbsolutePath+File.separator+"template"+File.separator
                +"java"+File.separator+"cli";

        String ModelFinalInputPath = resourceAbsolutePath+File.separator+"template"+File.separator
                +"java"+File.separator+"model";

        DynamicFileGenerator.dynamicGenerator(CommandFinalInputPath,CommandFinalOutputPath,
                "ConfigCommand.java.ftl","ConfigCommand.java",metaModel);

        DynamicFileGenerator.dynamicGenerator(CommandFinalInputPath,CommandFinalOutputPath,
                "GeneratorCommand.java.ftl","GeneratorCommand.java",metaModel);

        DynamicFileGenerator.dynamicGenerator(CommandFinalInputPath,CommandFinalOutputPath,
                "ListCommand.java.ftl","ListCommand.java",metaModel);

        DynamicFileGenerator.dynamicGenerator(CommandExecutorPath,CommandExecutorOutputPath,
                "CommandExecutor.java.ftl","CommandExecutor.java",metaModel);

        DynamicFileGenerator.dynamicGenerator(ModelFinalInputPath,finalOutputPath,templateName,generatedTemplateName,metaModel);

    }
}
