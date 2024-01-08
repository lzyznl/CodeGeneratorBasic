package com.lzy.maker;

import com.lzy.maker.cli.CommandExecutor;

public class Main {
    public static void main(String[] args) {
//        args = new String[]{"generate","-a","-o","-l"};
//        args = new String[]{"list"};
//        args = new String[]{"--help"};
        CommandExecutor commandExecutor = new CommandExecutor();
        commandExecutor.doExecutor(args);
    }
}