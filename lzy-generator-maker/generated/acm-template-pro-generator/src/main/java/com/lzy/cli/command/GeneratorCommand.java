package com.lzy.cli.command.CommandType;


import freemarker.template.TemplateException;
import com.lzy.generator.file.FileGenerator;
import com.lzy.model.DataModel;
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
     * 作者注释
     */
    @Option(names = {"-a", "--authorName"}, arity = "0..1",description = "作者注释", interactive = true
    ,defaultValue = "lzy",echo = true)
    private String author;


    /**
     * 输出信息
     */
    @Option(names = {"-o", "--outputText"}, arity = "0..1",description = "输出信息", interactive = true
    ,defaultValue = "result:",echo = true)
    private String outputText;


    /**
     * 是否开启循环
     */
    @Option(names = {"-l", "--loop"}, arity = "0..1",description = "是否开启循环", interactive = true
    ,defaultValue = false,echo = true)
    private boolean loop;


    @Override
    public void run() {
        DataModel dataModel = new DataModel();
        dataModel.setLoop(loop);
        dataModel.setOutputText(outputText);
        dataModel.setAuthor(author);
        try {
            FileGenerator.doCodeGenerator(dataModel);
        } catch (TemplateException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
