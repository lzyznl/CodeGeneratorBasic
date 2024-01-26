package com.lzy.cli.command;


import freemarker.template.TemplateException;
import com.lzy.generator.MainFileGenerator;
import com.lzy.model.DataModel;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

import java.io.IOException;


/**
 * @author lzyaaa
 * @date 2024-01-08
 * 生成文件夹子命令类
 */
@Command(name = "generate", version = "Generator 1.0", mixinStandardHelpOptions = true)
public class GeneratorCommand implements Runnable{


    
    /**
    * 是否需要生成.gitignore文件
    */
    @Option(names = {"-needGit", "--needGit"}, arity = "0..1",description = "是否需要生成.gitignore文件", interactive = true
            ,defaultValue = "true",echo = true)
    private boolean needGit  = true;



    /**
     *参数组
     */
    static DataModel.MainParam mainParam = new DataModel.MainParam();

    @Command(name = "mainParam",mixinStandardHelpOptions = true)
    public static class MainParamCommand implements Runnable{
        
        /**
        * 作者注释
        */
        @Option(names = {"-a", "--author"}, arity = "0..1",description = "作者注释", interactive = true
            ,defaultValue = "lzy",echo = true)
        private String author ;
        
        /**
        * 输出信息
        */
        @Option(names = {"-o", "--outputText"}, arity = "0..1",description = "输出信息", interactive = true
            ,defaultValue = "result:",echo = true)
        private String outputText ;

        @Override
        public void run() {
            mainParam.author = author;
            mainParam.outputText = outputText;
        }
    }



    
    /**
    * 是否开启循环
    */
    @Option(names = {"-l", "--loop"}, arity = "0..1",description = "是否开启循环", interactive = true
            ,defaultValue = "false",echo = true)
    private boolean loop  = false;



    @Override
    public void run() {
        if(loop){
            System.out.println("请按照下方提示依次输入核心配置");
            CommandLine commandLine = new CommandLine(MainParamCommand.class);
            commandLine.execute("--author", "--outputText");
        }
        DataModel dataModel = new DataModel();
        dataModel.needGit = needGit;
        dataModel.mainParam = mainParam;
        dataModel.loop = loop;
        try {
            MainFileGenerator.doCodeGenerator(dataModel);
        } catch (TemplateException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
