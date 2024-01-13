package ${basePackage}.cli.command;


import freemarker.template.TemplateException;
import ${basePackage}.generator.MainFileGenerator;
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
    private ${modelInfo.type} ${modelInfo.fieldName} <#if modelInfo.defaultValue??><#if modelInfo.defaultValue?is_boolean> = ${modelInfo.defaultValue?c}</#if></#if>;

</#list>

    @Override
    public void run() {
        DataModel dataModel = new DataModel();
        dataModel.loop = loop;
        dataModel.outputText = outputText;
        dataModel.author = author;
        dataModel.needGit = needGit;
        try {
            MainFileGenerator.doCodeGenerator(dataModel);
        } catch (TemplateException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
