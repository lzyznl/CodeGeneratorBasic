package ${basePackage}.model;

import lombok.Data;

/**
 * @author ${author}
 * @date ${createTime}
 * MainTemplate模板数据
 */
@Data
public class DataModel {
<#list modelConfig.models as modelInfo>

    <#if modelInfo.description??>
    /**
     * ${modelInfo.description}
     */
    </#if>
    public ${modelInfo.type} ${modelInfo.fieldName} <#if modelInfo.defaultValue??> = <#if modelInfo.defaultValue?is_string>"${modelInfo.defaultValue?string}"<#else>${modelInfo.defaultValue?c}</#if></#if> ;

</#list>


}
