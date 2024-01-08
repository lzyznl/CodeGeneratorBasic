package com.lzy.maker.cli.command.CommandType;

import cn.hutool.core.io.FileUtil;
import picocli.CommandLine.Command;

import java.io.File;
import java.util.List;

/**
 * @author lzyaaa
 * @date 2024-01-08
 * 遍历查看所要生成的文件列表
 */

@Command(name = "list", version = "List 1.0", mixinStandardHelpOptions = true)
public class ListCommand implements Runnable{

    @Override
    public void run() {
        String InputPath = "D:\Java_Project\Code\lzy-generator\lzy-generator-demo-project\acm-template-pro";
        List<File> files = FileUtil.loopFiles(new File(InputPath));
        for(File file:files){
            System.out.println(file.getPath());
        }
    }
}
