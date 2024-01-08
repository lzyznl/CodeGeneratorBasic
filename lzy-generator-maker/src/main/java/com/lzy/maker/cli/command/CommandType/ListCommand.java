package com.lzy.maker.cli.command.CommandType;

import cn.hutool.core.io.FileUtil;
import picocli.CommandLine.Command;

import java.io.File;
import java.util.List;

/**
 * @author lzy
 * @date 2024-01-07
 * 遍历查看所要生成的文件列表
 */

@Command(name = "list", version = "List 1.0", mixinStandardHelpOptions = true)
public class ListCommand implements Runnable{

    @Override
    public void run() {
        String projectPath = System.getProperty("user.dir");
        String filePath = new File(projectPath).getParentFile().getPath()+File.separator+"lzy-generator-demo-project/acm-template";
        List<File> files = FileUtil.loopFiles(new File(filePath));
        for(File file:files){
            System.out.println(file.getPath());
        }
    }
}
