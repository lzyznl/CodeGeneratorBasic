package com.lzy.cli.example;

import picocli.CommandLine;
import picocli.CommandLine.Option;
import java.util.concurrent.Callable;

/**
 * @author lzy
 * @date 2024-01-07
 * 实现交互输入以及可选式交互输入
 */
public class Login implements Callable<Integer> {
    @Option(names = {"-u", "--user"}, description = "User name")
    String user;

    @Option(names = {"-p", "--password"}, arity = "0..1",description = "Passphrase", interactive = true)
    String password;

    @Option(names = {"-c", "--checkPassword"}, arity = "0..1",description = "check-password", interactive = true)
    String checkPassword;

    public Integer call() throws Exception {
        System.out.println("password="+password);
        System.out.println("checkPassword="+checkPassword);
        return 0;
    }

    public static void main(String[] args) {
        new CommandLine(new Login()).execute("--user","lzy","-p","-c");
    }
}
