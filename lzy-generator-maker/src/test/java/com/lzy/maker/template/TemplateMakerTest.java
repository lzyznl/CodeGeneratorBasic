package com.lzy.maker.template;


import com.lzy.maker.meta.Meta;
import com.lzy.maker.template.model.FileFilterConfig;
import com.lzy.maker.template.model.TemplateMakerFileConfig;
import com.lzy.maker.template.model.enums.FilterRangeEnum;
import com.lzy.maker.template.model.enums.FilterTypeEnum;

import java.io.File;
import java.util.Arrays;
import java.util.List;

/**
 * 模板制作工具测试类
 * @author lzy
 * @date 2024-02-05
 */
public class TemplateMakerTest {

    public static void main(String[] args) {
        String projectPath = System.getProperty("user.dir");
//        String sourceRootPath = new File(projectPath).getParent()+File.separator+"lzy-generator-demo-project"+File.separator+"acm-template";
//        String inputPath = "src/com/lzy/acm/MainTemplate.java";

        String sourceRootPath = new File(projectPath).getParent()+File.separator+"lzy-generator-demo-project"+File.separator+"springboot-init-master";
        String inputPath = "src/main/java/com/yupi/springbootinit";
        String inputPath1 = "src/main/java/com/yupi/springbootinit/aop";
        String inputPath2 = "src/main/java/com/yupi/springbootinit/controller";

        List<String> inputPathList = Arrays.asList(inputPath1,inputPath2);


        Meta meta = new Meta();
        String name = "acm-template";
        String description = "ACM模板示例代码生成器";
        String basePackage = "com.lzy";
        meta.setName(name);
        meta.setDescription(description);
        meta.setBasePackage(basePackage);

        //添加文件过滤器
        TemplateMakerFileConfig config = new TemplateMakerFileConfig();
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
                .value("User").build();
        fileInfoConfig2.setFilterConfigs(Arrays.asList(filterConfig2));

        //设置文件分组
        TemplateMakerFileConfig.FileGroupInfoConfig fileGroupInfoConfig = new TemplateMakerFileConfig.FileGroupInfoConfig();
        fileGroupInfoConfig.setCondition("test");
        fileGroupInfoConfig.setGroupKey("test");
        fileGroupInfoConfig.setGroupDescription("测试");
        config.setFileGroupInfoConfig(fileGroupInfoConfig);
        config.setFiles(Arrays.asList(fileInfoConfig1,fileInfoConfig2));



        //(第一次生成)
//        Meta.ModelConfigDTO.ModelsInfo modelInfo = new Meta.ModelConfigDTO.ModelsInfo();
//        modelInfo.setFieldName("outputText");
//        modelInfo.setType("String");
//        modelInfo.setDefaultValue("fact= ");

        //(第二次生成)
        Meta.ModelConfigDTO.ModelsInfo modelInfo = new Meta.ModelConfigDTO.ModelsInfo();
        modelInfo.setFieldName("className");
        modelInfo.setType("String");

        //(第一次生成)
//        String searchStr = "Sum: ";

        //(第二次生成)
        String searchStr = "BaseResponse";

        TemplateMaker.makeTemplate(sourceRootPath, config,meta,modelInfo,searchStr,1754521574135508992L);
    }
}