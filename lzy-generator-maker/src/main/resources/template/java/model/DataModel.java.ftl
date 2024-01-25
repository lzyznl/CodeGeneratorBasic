package ${basePackage}.model;

import lombok.Data;

<#macro generateModel indent modelInfo>
<#if modelInfo.description??>
${indent}/**
${indent} * ${modelInfo.description}
${indent} */
</#if>
${indent}public ${modelInfo.type} ${modelInfo.fieldName} <#if modelInfo.defaultValue??> = <#if modelInfo.defaultValue?is_string>"${modelInfo.defaultValue?string}"<#else>${modelInfo.defaultValue?c}</#if></#if> ;
</#macro>

/**
 * @author ${author}
 * @date ${createTime}
 * MainTemplate模板数据
 */
@Data
public class DataModel {
<#list modelConfig.models as modelInfo>

    <#if modelInfo.groupKey??>
    /**
    * ${modelInfo.description}
    */
    public ${modelInfo.type} ${modelInfo.groupKey} = new ${modelInfo.type}();

    @Data
    public static class ${modelInfo.type}{
    <#list modelInfo.models as subModelInfo>
    <@generateModel indent="        " modelInfo=subModelInfo/>
    </#list>
    }
    <#else>
    <@generateModel indent="    "  modelInfo=modelInfo/>
    </#if>

</#list>


}
