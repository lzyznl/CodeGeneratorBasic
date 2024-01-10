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

    public static void main(String[] args) throws TemplateException, IOException, InterruptedException {
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


        finalOutputPath = OutPutProjectPath+File.separator+"model"+File.separator+"DataModel.java";
        finalInputPath = InputProjectPath+File.separator+"model"+File.separator+"DataModel.java.ftl";

        DynamicFileGenerator.dynamicGenerator(finalInputPath,finalOutputPath,metaModel);

        /**
         * 生成命令执行路径
         */
        finalOutputPath = OutPutProjectPath+File.separator+"cli"+File.separator+"command"+File.separator+"ConfigCommand.java";
        finalInputPath = InputProjectPath+File.separator+"cli"+File.separator+"command"+File.separator+"ConfigCommand.java.ftl";

        DynamicFileGenerator.dynamicGenerator(finalInputPath,finalOutputPath, metaModel);

        finalOutputPath = OutPutProjectPath+File.separator+"cli"+File.separator+"command"+File.separator+"GeneratorCommand.java";
        finalInputPath = InputProjectPath+File.separator+"cli"+File.separator+"command"+File.separator+"GeneratorCommand.java.ftl";

        DynamicFileGenerator.dynamicGenerator(finalInputPath,finalOutputPath, metaModel);

        finalOutputPath = OutPutProjectPath+File.separator+"cli"+File.separator+"command"+File.separator+"ListCommand.java";
        finalInputPath = InputProjectPath+File.separator+"cli"+File.separator+"command"+File.separator+"ListCommand.java.ftl";

        DynamicFileGenerator.dynamicGenerator(finalInputPath,finalOutputPath, metaModel);

        finalOutputPath = OutPutProjectPath+File.separator+"cli"+File.separator+"CommandExecutor.java";
        finalInputPath = InputProjectPath+File.separator+"cli"+File.separator+"CommandExecutor.java.ftl";

        DynamicFileGenerator.dynamicGenerator(finalInputPath,finalOutputPath, metaModel);

        finalInputPath = InputProjectPath+File.separator+"Main.java.ftl";
        finalOutputPath = OutPutProjectPath+File.separator+"Main.java";
        DynamicFileGenerator.dynamicGenerator(finalInputPath,finalOutputPath,metaModel);

        finalInputPath = InputProjectPath+File.separator+"generator"+File.separator+"DynamicFileGenerator,java.ftl";
        finalOutputPath = OutPutProjectPath+File.separator+"generator"+File.separator+"DynamicFileGenerator.java";
        DynamicFileGenerator.dynamicGenerator(finalInputPath,finalOutputPath,metaModel);

        finalInputPath = InputProjectPath+File.separator+"generator"+File.separator+"MainFileGenerator.java.ftl";
        finalOutputPath = OutPutProjectPath+File.separator+"generator"+File.separator+"MainFileGenerator.java";
        DynamicFileGenerator.dynamicGenerator(finalInputPath,finalOutputPath,metaModel);


        finalInputPath = InputProjectPath+File.separator+"generator"+File.separator+"StaticFileGenerator.java.ftl";
        finalOutputPath = OutPutProjectPath+File.separator+"generator"+File.separator+"StaticFileGenerator.java";
        DynamicFileGenerator.dynamicGenerator(finalInputPath,finalOutputPath,metaModel);

        finalInputPath = resourceAbsolutePath+File.separator+"template"+File.separator+"pom.xml.ftl";
        finalOutputPath = OutputRootPath+File.separator+"generated"+File.separator+metaModel.getName()+File.separator+"pom.xml";
        DynamicFileGenerator.dynamicGenerator(finalInputPath,finalOutputPath,metaModel);

        String path = OutputRootPath+File.separator+"generated"+File.separator+metaModel.getName();

        //构建Jar包
        JarGenerator.doGenerate(path);

    }
}
