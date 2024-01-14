package ${basePackage}.generator;

import ${basePackage}.model.DataModel;
import freemarker.template.TemplateException;

import java.io.File;
import java.io.IOException;

<#macro generateFile indent paramsFile>
${indent}finalInputPath=InputRootPath+File.separator+"${paramsFile.inputPath}";
${indent}finalOutputPath = OutputRootPath+File.separator+"${paramsFile.outputPath}";
<#if paramsFile.generateType == "Dynamic">
${indent}DynamicFileGenerator.dynamicGenerator(finalInputPath,finalOutputPath,Model);
<#else >
${indent}StaticFileGenerator.copFileByHutool(finalInputPath,finalOutputPath);
</#if>
</#macro>

/**
 * @author ${author}
 * @date ${createTime}
 * 总体代码文件生成器，包括动态文件生成以及静态文件生成
 */
public class MainFileGenerator {
    /**
     * 动静结合，生成代码文件
     * @param Model 插值数据
     */
    public static void doCodeGenerator(DataModel Model) throws TemplateException, IOException {

        //修改为从元信息配置文件中读取配置
        String InputRootPath = "${fileConfig.inputRootPath}";
        String OutputRootPath = "${fileConfig.outputRootPath}";

        String finalInputPath;
        String finalOutputPath;

      <#list modelConfig.models as modelInfo>
        ${modelInfo.type} ${modelInfo.fieldName} = Model.${modelInfo.fieldName};
      </#list>

      <#list fileConfig.files as file>
        <#if file.groupKey??>
        // groupKey = ${file.groupKey}
        <#if file.condition??>
        if(${file.condition}){
        <#list file.files as file>
        <@generateFile paramsFile = file indent = "            "/>
        </#list>
        }
        <#else>
        <#list file.files as file>
        <@generateFile paramsFile= file indent="        "/>
        </#list>
        </#if>
        <#else>
        <#if file.condition??>
        if(${file.condition}){
        <@generateFile paramsFile = file indent = "            "/>
        }
        <#else>
        <@generateFile paramsFile= file indent="        "/>
        </#if>
        </#if>
      </#list>

    }
}
