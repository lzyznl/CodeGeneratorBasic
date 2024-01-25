package com.lzy.maker.cli.command;


import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

/**
 * @author lzy
 * @date 2024-01-25
 * 测试参数组命令生成
 */

@Command(name = "generate", version = "Generator 1.0", mixinStandardHelpOptions = true)
public class TestArgGroupCommand implements Runnable{

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

    @CommandLine.ArgGroup(exclusive = false,heading = "核心配置参数%n")
    MainParam mainParam;

    static public class MainParam{
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
    }


    @Override
    public void run() {
        System.out.println("needGit:"+needGit);
        System.out.println("loop:"+loop);
        System.out.println(mainParam.author);
        System.out.println(mainParam.outputText);
    }

    public static void main(String[] args) {
        CommandLine commandLine  = new CommandLine(TestArgGroupCommand.class);
        String[] arg = new String[]{"-a"};
        commandLine.execute(arg);
    }
}
