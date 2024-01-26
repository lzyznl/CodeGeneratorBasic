### ${name}
<#macro generateDescription indent modelInfo>
描述：${modelInfo.description}

类型：${modelInfo.type}

默认值：<#if modelInfo.defaultValue??>${modelInfo.defaultValue?string}</#if>

命令缩写：${modelInfo.abbr}

</#macro>
>项目描述: ${description}
>
>作者: lzy
>
>Github：https://github.com/lzyznl
>
>项目Github地址：https://github.com/lzyznl/CodeGeneratorBasic

### 使用说明

根据您的系统环境执行我们所提供给您的脚本

> generate <命令> <命令参数>

使用示例

> generate <#list modelConfig.models as modelInfo><#if modelInfo.groupKey??><#list modelInfo.models as subModelInfo>-${subModelInfo.abbr} </#list><#else>-${modelInfo.abbr} </#if></#list>

### 参数说明

<#list modelConfig.models as modelInfo>
<#if modelInfo.groupKey??>
（${modelInfo?index+1}）${modelInfo.groupName}
<#list modelInfo.models as subModelInfo>
<@generateDescription indent="" modelInfo=subModelInfo/>
</#list>
<#else >
（${modelInfo?index+1}） ${modelInfo.fieldName}
<@generateDescription indent="" modelInfo=modelInfo/>
</#if>
</#list>