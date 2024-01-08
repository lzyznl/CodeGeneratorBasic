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
    private ${modelInfo.type} ${modelInfo.fieldName} <#if modelInfo.defaultValue??> = ${modelInfo.defaultValue?string} ;</#if>

</#list>


}
