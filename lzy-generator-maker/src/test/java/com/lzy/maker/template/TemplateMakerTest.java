package com.lzy.maker.template;


import cn.hutool.core.io.resource.ResourceUtil;
import cn.hutool.json.JSONUtil;
import com.lzy.maker.meta.Meta;
import com.lzy.maker.template.model.FileFilterConfig;
import com.lzy.maker.template.model.TemplateMakerConfig;
import com.lzy.maker.template.model.TemplateMakerFileConfig;
import com.lzy.maker.template.model.TemplateMakerModelConfig;
import com.lzy.maker.template.model.enums.FilterRangeEnum;
import com.lzy.maker.template.model.enums.FilterTypeEnum;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 模板制作工具测试类
 * @author lzy
 * @date 2024-02-05
 */
public class TemplateMakerTest {

    public static void makeTemplateTest1(){
        String projectPath = System.getProperty("user.dir");
//        String sourceRootPath = new File(projectPath).getParent()+File.separator+"lzy-generator-demo-project"+File.separator+"acm-template";
//        String inputPath = "src/com/lzy/acm/MainTemplate.java";

        String sourceRootPath = new File(projectPath).getParent()+File.separator+"lzy-generator-demo-project"+File.separator+"springboot-init-master";
        String inputPath = "src/main/java/com/yupi/springbootinit";
        String inputPath1 = "src/main/java/com/yupi/springbootinit/aop";
        String inputPath2 = "src/main/resources/application.yml";

        List<String> inputPathList = Arrays.asList(inputPath1,inputPath2);


        Meta meta = new Meta();
        String name = "acm-template";
        String description = "ACM模板示例代码生成器";
        String basePackage = "com.lzy";
        meta.setName(name);
        meta.setDescription(description);
        meta.setBasePackage(basePackage);

        //添加文件过滤器
        TemplateMakerFileConfig templateMakerFileConfig = new TemplateMakerFileConfig();
        TemplateMakerFileConfig.FileInfoConfig fileInfoConfig1 = new TemplateMakerFileConfig.FileInfoConfig();
        FileFilterConfig filterConfig1 = FileFilterConfig.builder()
                .range(FilterRangeEnum.FILENAME.getValue())
                .rule(FilterTypeEnum.CONTAINS.getValue())
                .value("Auth").build();
        List<FileFilterConfig> filterConfigList = Arrays.asList(filterConfig1);
        fileInfoConfig1.setPath(inputPath1);
        fileInfoConfig1.setFilterConfigs(filterConfigList);

        TemplateMakerFileConfig.FileInfoConfig fileInfoConfig2 = new TemplateMakerFileConfig.FileInfoConfig();
        fileInfoConfig2.setPath(inputPath2);
        FileFilterConfig filterConfig2 = FileFilterConfig.builder()
                .range(FilterRangeEnum.FILENAME.getValue())
                .rule(FilterTypeEnum.STARTS_WITH.getValue())
                .value("app").build();
        fileInfoConfig2.setFilterConfigs(Arrays.asList(filterConfig2));

        //设置文件分组
        TemplateMakerFileConfig.FileGroupInfoConfig fileGroupInfoConfig = new TemplateMakerFileConfig.FileGroupInfoConfig();
        fileGroupInfoConfig.setCondition("test1");
        fileGroupInfoConfig.setGroupKey("test1");
        fileGroupInfoConfig.setGroupDescription("测试");
        templateMakerFileConfig.setFileGroupInfoConfig(fileGroupInfoConfig);
        templateMakerFileConfig.setFiles(Arrays.asList(fileInfoConfig1,fileInfoConfig2));


        //添加模型配置
        TemplateMakerModelConfig templateMakerModelConfig = new TemplateMakerModelConfig();

        TemplateMakerModelConfig.ModelGroupConfig modelGroupConfig = new TemplateMakerModelConfig.ModelGroupConfig();
        modelGroupConfig.setGroupKey("mysql");
        modelGroupConfig.setGroupName("数据库配置");
        modelGroupConfig.setCondition("mysql");

        TemplateMakerModelConfig.ModelInfoConfig modelInfoConfig1 = new TemplateMakerModelConfig.ModelInfoConfig();
        modelInfoConfig1.setFieldName("url");
        modelInfoConfig1.setDefaultValue("jdbc:mysql://localhost:3306/my_db");
        modelInfoConfig1.setReplaceText("jdbc:mysql://localhost:3306/my_db");
        modelInfoConfig1.setType("String");

        TemplateMakerModelConfig.ModelInfoConfig modelInfoConfig2 = new TemplateMakerModelConfig.ModelInfoConfig();
        modelInfoConfig2.setFieldName("username");
        modelInfoConfig2.setDefaultValue("root");
        modelInfoConfig2.setReplaceText("root");
        modelInfoConfig2.setType("String");

        List<TemplateMakerModelConfig.ModelInfoConfig> modelInfoConfigList = new ArrayList<>();
        modelInfoConfigList.add(modelInfoConfig1);
        modelInfoConfigList.add(modelInfoConfig2);

        templateMakerModelConfig.setModelGroupConfig(modelGroupConfig);
        templateMakerModelConfig.setModels(modelInfoConfigList);

        //(第一次生成)
//        Meta.ModelConfigDTO.ModelsInfo modelInfo = new Meta.ModelConfigDTO.ModelsInfo();
//        modelInfo.setFieldName("outputText");
//        modelInfo.setType("String");
//        modelInfo.setDefaultValue("fact= ");

        //(第二次生成)
//        Meta.ModelConfigDTO.ModelsInfo modelInfo = new Meta.ModelConfigDTO.ModelsInfo();
//        modelInfo.setFieldName("className");
//        modelInfo.setType("String");

        //(第一次生成)
//        String searchStr = "Sum: ";

        //(第二次生成)
//        String searchStr = "BaseResponse";

        TemplateMaker.makeTemplate(meta,sourceRootPath,templateMakerFileConfig,templateMakerModelConfig,1754715694820990976L);
    }

    public static void makeTemplateTest2(){
        String jsonStr = ResourceUtil.readUtf8Str("templateMaker.json");
        TemplateMakerConfig templateMakerConfig = JSONUtil.toBean(jsonStr, TemplateMakerConfig.class);
        TemplateMaker.makeTemplate(templateMakerConfig);
    }

    public static void main(String[] args) {
        TemplateMakerTest.makeTemplateTest2();
    }
}