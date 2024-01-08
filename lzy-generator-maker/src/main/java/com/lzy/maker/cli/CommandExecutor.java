package com.lzy.maker.cli;

import com.lzy.maker.cli.command.ConfigCommand;
import com.lzy.maker.cli.command.GeneratorCommand;
import com.lzy.maker.cli.command.ListCommand;
import picocli.CommandLine;
import picocli.CommandLine.Command;

/**
 * @author lzy
 * @date 2024-01-07
 * 命令执行类
 */
@Command(name = "lzy", version = "lzy 1.0", mixinStandardHelpOptions = true)
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
