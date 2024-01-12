### ${name}

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

> generate <#list modelConfig.models as modelInfo>-${modelInfo.abbr} </#list>

### 参数说明

<#list modelConfig.models as modelInfo>
（${modelInfo?index+1}） ${modelInfo.fieldName}

描述：${modelInfo.description}

类型：${modelInfo.type}

默认值：<#if modelInfo.defaultValue??>${modelInfo.defaultValue?string}</#if>

命令缩写：${modelInfo.abbr}

</#list>