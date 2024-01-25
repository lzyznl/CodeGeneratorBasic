package ${basePackage}.cli.command;


import freemarker.template.TemplateException;
import ${basePackage}.generator.MainFileGenerator;
import ${basePackage}.model.DataModel;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

import java.io.IOException;

<#--生成参数方法-->
<#macro generateOptions indent modelInfo>
${indent}<#if modelInfo.description??>
${indent}/**
${indent}* ${modelInfo.description}
${indent}*/
</#if>
${indent}@Option(names = {"${modelInfo.abbr}", "${modelInfo.fullName}"}, arity = "0..1",<#if modelInfo.description??>description = "${modelInfo.description}"</#if>, interactive = true
,<#if modelInfo.defaultValue??>defaultValue = "${modelInfo.defaultValue?string}"</#if>,echo = true)
${indent}private ${modelInfo.type} ${modelInfo.fieldName} <#if modelInfo.defaultValue??><#if modelInfo.defaultValue?is_boolean> = ${modelInfo.defaultValue?c}</#if></#if>;
</#macro>

<#--调用核心配置参数命令-->
<#macro generateCommand indent modelInfo>
${indent}System.out.println("请按照下方提示依次输入核心配置");
${indent}CommandLine commandLine = new CommandLine(${modelInfo.type}Command.class);
${indent}commandLine.execute(${modelInfo.mediumArgs});
</#macro>

/**
 * @author ${author}
 * @date ${createTime}
 * 生成文件夹子命令类
 */

@Command(name = "generate", version = "Generator 1.0", mixinStandardHelpOptions = true)
public class GeneratorCommand implements Runnable{

<#list modelConfig.models as modelInfo>

    <#if modelInfo.groupKey??>
    /**
     *<#if modelInfo.groupName>modeInfo.groupName</#if>
     */
    static DataModel.${modelInfo.type} ${modelInfo.groupKey} = new DataModel.${modelInfo.type}();

    @Command(name = "${modelInfo.groupKey}",mixinStandardHelpOptions = true)
    public static class ${modelInfo.type}Command implements Runnable{
    <#list modelInfo.models as subModelInfo>
    <@generateOptions indent="        " modelInfo=subModelInfo/>
    </#list>

        @Override
        public void run() {
            <#list modelInfo.models as subModelInfo>
            ${modelInfo.groupKey}.${subModelInfo.fieldName} = ${subModelInfo.fieldName};
            </#list>
        }
    }
    <#else >
    <@generateOptions indent="    " modelInfo=modelInfo/>
    </#if>


</#list>

    @Override
    public void run() {
    <#list modelConfig.models as modelInfo>
    <#if modelInfo.groupKey??>
    <#if modelInfo.condition??>
    if(${modelInfo.condition}){
    <@generateOptions indent="            " modelInfo=modelInfo/>
    }
    <#else>
    <@generateOptions indent="        " modelInfo=modelInfo/>
    </#if>
    </#if>
    </#list>
        DataModel dataModel = new DataModel();
        <#list modelConfig.models as modelInfo>
        <#if modelInfo.groupKey??>
        dataModel.${modelInfo.groupKey} = ${modelInfo.groupKey};
        <#else>
        dataModel.${modelInfo.fieldName} = ${modelInfo.fieldName};
        </#if>
        </#list>
        try {
            MainFileGenerator.doCodeGenerator(dataModel);
        } catch (TemplateException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
