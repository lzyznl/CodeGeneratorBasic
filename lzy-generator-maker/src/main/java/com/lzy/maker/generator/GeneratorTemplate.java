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
 * @date 2024-01-12
 * 使用模板方法设计模式对原有MainGenerator进行优化
 */
public abstract class GeneratorTemplate {

    public void doGenerate() throws TemplateException, IOException, InterruptedException{
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

        //1.拷贝原有代码文件
        copySourceCode(metaModel, OutputRootPath);


        //获取文件的输入根路径
        ClassPathResource classPathResource = new ClassPathResource("");
        String resourceAbsolutePath = classPathResource.getAbsolutePath();
        String InputProjectPath = resourceAbsolutePath+File.separator+"template"+File.separator
                +"java";

        //2.生成文件
        GenerateCode(OutPutProjectPath, InputProjectPath, metaModel, resourceAbsolutePath, OutputRootPath);


        //3构建Jar包
        buildJar(OutputRootPath, metaModel);

        //4.生成脚本
        String jarName = buildScript(OutputRootPath, metaModel);

        //5.生成精简版文件夹
        buildDist(metaModel, OutputRootPath, jarName);
    }

    /**
     * 生成精简版文件夹
     * @param metaModel
     * @param OutputRootPath
     * @param jarName
     */
    protected void buildDist(Meta metaModel, String OutputRootPath, String jarName) {
        //生成精简版文件包
        String simpleDirName = metaModel.getName()+"-dist";
        String simpleFileInputPath = OutputRootPath +File.separator+"generated"+File.separator+ metaModel.getName();
        String simpleFileOutputPath = OutputRootPath +File.separator+"generated"+File.separator+simpleDirName;

        String finalSimpleFileInputPath;
        String finalSimpleFileOutputPath;

        //拷贝.source文件夹
        finalSimpleFileInputPath = simpleFileInputPath+File.separator+".source";
        finalSimpleFileOutputPath = simpleFileOutputPath+File.separator;
        FileUtil.copy(finalSimpleFileInputPath,finalSimpleFileOutputPath,true);

        //拷贝jar包到target目录下
        FileUtil.mkdir(new File(simpleFileOutputPath+File.separator+"target"));
        finalSimpleFileInputPath = simpleFileInputPath+File.separator+"target"+File.separator+ jarName;
        finalSimpleFileOutputPath = simpleFileOutputPath+File.separator+"target";
        FileUtil.copy(finalSimpleFileInputPath,finalSimpleFileOutputPath,true);

        //拷贝脚本文件
        finalSimpleFileInputPath = simpleFileInputPath+File.separator+"generator.bat";
        finalSimpleFileOutputPath = simpleFileOutputPath+File.separator+"generator.bat";
        FileUtil.copy(finalSimpleFileInputPath,finalSimpleFileOutputPath,true);

        //拷贝README.md文件
        finalSimpleFileInputPath = simpleFileInputPath+File.separator+"README.md";
        finalSimpleFileOutputPath = simpleFileOutputPath+File.separator+"README.md";
        FileUtil.copy(finalSimpleFileInputPath,finalSimpleFileOutputPath,true);
    }

    /**
     * 生成脚本
     * @param OutputRootPath
     * @param metaModel
     * @return
     */
    protected String buildScript(String OutputRootPath, Meta metaModel) {
        String scriptOutputPath = OutputRootPath +File.separator+"generated"+File.separator+ metaModel.getName()+File.separator+"generator";
        String jarName = String.format("%s-%s-%s-jar-with-dependencies.jar", metaModel.getName(), metaModel.getVersion(),"SNAPSHOT");
        String jarPath = OutputRootPath +File.separator+"generated"+File.separator+ metaModel.getName()+File.separator
                +"target"+File.separator+jarName;
        ScriptGenerator.doGenerate(scriptOutputPath,jarPath,false);
        return jarName;
    }

    /**
     * 生成Jar包
     * @param OutputRootPath
     * @param metaModel
     * @throws IOException
     * @throws InterruptedException
     */
    protected void buildJar(String OutputRootPath, Meta metaModel) throws IOException, InterruptedException {
        String path = OutputRootPath +File.separator+"generated"+File.separator+ metaModel.getName();
        JarGenerator.doGenerate(path);
    }

    /**
     * 生成代码
     * @param OutPutProjectPath
     * @param InputProjectPath
     * @param metaModel
     * @param resourceAbsolutePath
     * @param OutputRootPath
     * @throws IOException
     * @throws TemplateException
     */
    protected void GenerateCode(String OutPutProjectPath, String InputProjectPath, Meta metaModel, String resourceAbsolutePath, String OutputRootPath) throws IOException, TemplateException {
        String finalInputPath;
        String finalOutputPath;
        //生成数据模型
        finalOutputPath = OutPutProjectPath +File.separator+"model"+File.separator+"DataModel.java";
        finalInputPath = InputProjectPath +File.separator+"model"+File.separator+"DataModel.java.ftl";

        DynamicFileGenerator.dynamicGenerator(finalInputPath,finalOutputPath, metaModel);

        /**
         * 生成命令执行路径
         */
        //生成ConfigCommand
        finalOutputPath = OutPutProjectPath +File.separator+"cli"+File.separator+"command"+File.separator+"ConfigCommand.java";
        finalInputPath = InputProjectPath +File.separator+"cli"+File.separator+"command"+File.separator+"ConfigCommand.java.ftl";
        DynamicFileGenerator.dynamicGenerator(finalInputPath,finalOutputPath, metaModel);

        //生成GeneratorCommand
        finalOutputPath = OutPutProjectPath +File.separator+"cli"+File.separator+"command"+File.separator+"GeneratorCommand.java";
        finalInputPath = InputProjectPath +File.separator+"cli"+File.separator+"command"+File.separator+"GeneratorCommand.java.ftl";
        DynamicFileGenerator.dynamicGenerator(finalInputPath,finalOutputPath, metaModel);

        //生成ListCommand
        finalOutputPath = OutPutProjectPath +File.separator+"cli"+File.separator+"command"+File.separator+"ListCommand.java";
        finalInputPath = InputProjectPath +File.separator+"cli"+File.separator+"command"+File.separator+"ListCommand.java.ftl";
        DynamicFileGenerator.dynamicGenerator(finalInputPath,finalOutputPath, metaModel);

        //生成CommandExecutor
        finalOutputPath = OutPutProjectPath +File.separator+"cli"+File.separator+"CommandExecutor.java";
        finalInputPath = InputProjectPath +File.separator+"cli"+File.separator+"CommandExecutor.java.ftl";
        DynamicFileGenerator.dynamicGenerator(finalInputPath,finalOutputPath, metaModel);

        //生成Main.java
        finalInputPath = InputProjectPath +File.separator+"Main.java.ftl";
        finalOutputPath = OutPutProjectPath +File.separator+"Main.java";
        DynamicFileGenerator.dynamicGenerator(finalInputPath,finalOutputPath, metaModel);

        //生成DynamicFileGenerator,java
        finalInputPath = InputProjectPath +File.separator+"generator"+File.separator+"DynamicFileGenerator,java.ftl";
        finalOutputPath = OutPutProjectPath +File.separator+"generator"+File.separator+"DynamicFileGenerator.java";
        DynamicFileGenerator.dynamicGenerator(finalInputPath,finalOutputPath, metaModel);

        //生成MainFileGenerator.java
        finalInputPath = InputProjectPath +File.separator+"generator"+File.separator+"MainFileGenerator.java.ftl";
        finalOutputPath = OutPutProjectPath +File.separator+"generator"+File.separator+"MainFileGenerator.java";
        DynamicFileGenerator.dynamicGenerator(finalInputPath,finalOutputPath, metaModel);

        //生成StaticFileGenerator.java
        finalInputPath = InputProjectPath +File.separator+"generator"+File.separator+"StaticFileGenerator.java.ftl";
        finalOutputPath = OutPutProjectPath +File.separator+"generator"+File.separator+"StaticFileGenerator.java";
        DynamicFileGenerator.dynamicGenerator(finalInputPath,finalOutputPath, metaModel);

        //生成pom.xml
        finalInputPath = resourceAbsolutePath +File.separator+"template"+File.separator+"pom.xml.ftl";
        finalOutputPath = OutputRootPath +File.separator+"generated"+File.separator+ metaModel.getName()+File.separator+"pom.xml";
        DynamicFileGenerator.dynamicGenerator(finalInputPath,finalOutputPath, metaModel);

        //生成README.md文件
        finalInputPath = resourceAbsolutePath+File.separator+"template"+File.separator+"README.md.ftl";
        finalOutputPath = OutputRootPath+File.separator+"generated"+File.separator+metaModel.getName()+File.separator+"README.md";
        DynamicFileGenerator.dynamicGenerator(finalInputPath,finalOutputPath,metaModel);
    }

    /**
     * 拷贝原始文件
     * @param metaModel
     * @param OutputRootPath
     */
    protected void copySourceCode(Meta metaModel, String OutputRootPath) {
        String sourceRootPath;
        String destRootPath;
        sourceRootPath = metaModel.getFileConfig().getSourceRootPath();
        destRootPath = OutputRootPath +File.separator+"generated"+File.separator+ metaModel.getName()+File.separator
                +".source";
        FileUtil.copy(sourceRootPath,destRootPath,true);
    }
}
