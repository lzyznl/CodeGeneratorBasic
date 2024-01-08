package ${basePackage}.cli.command.CommandType;


import freemarker.template.TemplateException;
import ${basePackage}.generator.file.FileGenerator;
import ${basePackage}.model.DataModel;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

import java.io.IOException;

/**
 * @author ${author}
 * @date ${createTime}
 * 生成文件夹子命令类
 */

@Command(name = "generate", version = "Generator 1.0", mixinStandardHelpOptions = true)
public class GeneratorCommand implements Runnable{

<#list modelConfig.models as modelInfo>

    <#if modelInfo.description??>
    /**
     * ${modelInfo.description}
     */
    </#if>
    @Option(names = {"${modelInfo.abbr}", "${modelInfo.fullName}"}, arity = "0..1",<#if modelInfo.description??>description = "${modelInfo.description}"</#if>, interactive = true
    ,<#if modelInfo.defaultValue??>defaultValue = "${modelInfo.defaultValue?string}"</#if>,echo = true)
    private ${modelInfo.type} ${modelInfo.fieldName};

</#list>

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
