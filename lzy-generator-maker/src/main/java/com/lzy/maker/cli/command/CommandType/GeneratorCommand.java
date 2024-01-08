package com.lzy.maker.cli.command.CommandType;


import freemarker.template.TemplateException;
import com.lzy.maker.generator.file.FileGenerator;
import com.lzy.maker.model.DataModel;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

import java.io.IOException;

/**
 * @author lzy
 * @date 2024-01-07
 * 生成文件夹子命令类,实现用户自定义配置
 */

@Command(name = "generate", version = "Generator 1.0", mixinStandardHelpOptions = true)
public class GeneratorCommand implements Runnable{

    /**
     * 作者注释
     */
    @Option(names = {"-a", "--authorName"}, arity = "0..1",description = "作者名", interactive = true
            ,defaultValue = "lzy",echo = true)
    private String author;

    /**
     * 程序输出结果
     */
    @Option(names = {"-o", "--outputText"}, arity = "0..1",description = "模板程序输出描述", interactive = true,
            defaultValue = "程序输出结果是:",echo = true)
    private String outputText;

    /**
     * 是否循环
     */
    @Option(names = {"-l", "--loop"}, arity = "0..1",description = "是否需要循环输入", interactive = true,
            echo = true)
    private Boolean loop = false;


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
