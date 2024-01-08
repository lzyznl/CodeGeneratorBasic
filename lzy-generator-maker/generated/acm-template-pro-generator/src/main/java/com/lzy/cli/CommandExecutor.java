package com.lzy.maker.cli.command;

import com.lzy.maker.cli.command.CommandType.ConfigCommand;
import com.lzy.maker.cli.command.CommandType.GeneratorCommand;
import com.lzy.maker.cli.command.CommandType.ListCommand;
import picocli.CommandLine;
import picocli.CommandLine.Command;

/**
 * @author lzyaaa
 * @date 2024-01-08
 * 命令执行类
 */
@Command(name = "acm-template-pro-generator", version = "acm-template-pro-generator 1.0", mixinStandardHelpOptions = true)
public class CommandExecutor implements Runnable{

    private final CommandLine commandLine;

    {
        commandLine = new CommandLine(this)
                .addSubcommand(new GeneratorCommand())
                .addSubcommand(new ListCommand())
                .addSubcommand(new ConfigCommand());
    }


    @Override
    public void run() {
        System.out.println("请输入对应的子命令，可以通过--help选项进行查看");
    }

    public Integer doExecutor(String[] args){
         return commandLine.execute(args);
    }
}
