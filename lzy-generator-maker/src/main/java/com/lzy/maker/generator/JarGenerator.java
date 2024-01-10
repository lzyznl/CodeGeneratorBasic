package com.lzy.maker.generator;

import java.io.*;

/**
 * @author lzy
 * @date 2024-01-10
 * Jar包生成器
 */
public class JarGenerator {

    public static void doGenerate(String path) throws IOException, InterruptedException {
        String windowsMvnCmd = "mvn.cmd clean package -DskipTests=true";
        String otherMvnCmd = "mvn clean package -DskipTests=true";
        String processCmd = windowsMvnCmd;

        ProcessBuilder processBuilder = new ProcessBuilder(processCmd.split(" "));
        processBuilder.directory(new File(path));
        Process process = processBuilder.start();

        InputStream inputStream = process.getInputStream();
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        String line;
        while((line = bufferedReader.readLine())!=null){
            System.out.println(line);
        }

        int exitCode = process.waitFor();
        System.out.println("命令执行结束，退出码为:"+exitCode);
    }
}
