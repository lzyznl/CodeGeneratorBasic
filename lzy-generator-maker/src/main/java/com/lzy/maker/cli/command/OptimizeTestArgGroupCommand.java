package com.lzy.maker.cli.command;

import com.lzy.maker.generator.file.MainFileGenerator;
import com.lzy.maker.model.DataModel;
import freemarker.template.TemplateException;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

import java.io.IOException;

/**
 * @author lzy
 * @date 2024-01-25
 * 测试参数组命令生成优化类
 */

@Command(name = "generate", version = "Generator 1.0", mixinStandardHelpOptions = true)
public class OptimizeTestArgGroupCommand implements Runnable{

    /**
     * 是否需要生成.gitignore文件
     */
    @Option(names = {"-needGit", "--needGit"}, arity = "0..1",description = "是否需要生成.gitignore文件", interactive = true
            ,defaultValue = "true",echo = true)
    private boolean needGit  = true;

    /**
     * 是否循环
     */
    @Option(names = {"-l", "--loop"}, arity = "0..1",description = "是否需要循环输入", interactive = true,
            echo = true)
    private Boolean loop = false;

    static DataModel.MainParam mainParam = new DataModel.MainParam();


    @Command(name = "MainParam",mixinStandardHelpOptions = true)
    public static class MainParamCommand implements Runnable{
        /**
         * 作者注释
         */
        @Option(names = {"-a", "--author"}, arity = "0..1",description = "核心参数_作者名", interactive = true
                ,defaultValue = "lzy",echo = true)
        private String author;

        /**
         * 程序输出结果
         */
        @Option(names = {"-o", "--outputText"}, arity = "0..1",description = "核心参数_模板程序输出描述", interactive = true,
                defaultValue = "程序输出结果是:",echo = true)
        private String outputText;

        @Override
        public void run() {
            mainParam.author = author;
            mainParam.outputText = outputText;
        }
    }

    @Override
    public void run() {
        if(loop){
            System.out.println("请按照下方提示依次输入核心配置");
            CommandLine commandLine = new CommandLine(MainParamCommand.class);
            String[] arg = new String[]{"-a","-o"};
            commandLine.execute(arg);
        }
        DataModel dataModel = new DataModel();
        dataModel.needGit = needGit;
        dataModel.loop = loop;
        dataModel.mainParam = mainParam;
        try {
            MainFileGenerator.doCodeGenerator(dataModel);
        } catch (TemplateException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
