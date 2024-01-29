package com.lzy.maker.template;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.lzy.maker.meta.Meta;
import com.lzy.maker.meta.enums.FileTypeEnum;
import com.lzy.maker.meta.enums.GenerateFileTypeEnum;
import com.lzy.maker.template.Filter.FileFilter;
import com.lzy.maker.template.model.FileFilterConfig;
import com.lzy.maker.template.model.TemplateMakerFileConfig;
import com.lzy.maker.template.model.enums.FilterRangeEnum;
import com.lzy.maker.template.model.enums.FilterTypeEnum;

import java.io.File;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
/**
 * @author lzy
 * @date 2024-01-26
 * 实现模板制作工具，提高生产效率(已弃用)
 */
@Deprecated
public class TemplateMaker_old {

    /**
     * 制作模板
     * @param sourceRootPath 源文件的路径
     * @param inputPath 动态生成文件的相对路径
     * @param newMeta   元信息的根信息
     * @param modelInfo 元信息中的模型信息
     * @param searchStr 所要替换的字符串内容
     * @param id        所对应的临时目录
     * @return
     */
    public static long makeTemplate(String sourceRootPath, String inputPath, Meta newMeta, Meta.ModelConfigDTO.ModelsInfo modelInfo,String searchStr,Long id){
        if (id==null){
            //使用雪花算法生成一个唯一的id作为每一次生成文件的目录名
            id = IdUtil.getSnowflakeNextId();
        }

        //生成一个基本的模板制作工具，我们基本需要从以下几个方面进行生成
        //1：找到原始的输入文件
        String projectPath = System.getProperty("user.dir");
        sourceRootPath = sourceRootPath.replaceAll("\\\\", "/");


        //2：转移到一个新的隔离的工作目录下，避免污染原始文件目录
        String tempDir = projectPath+File.separator+".temp";
        String subTempPath = tempDir+File.separator+id;
        //判断文件是否存在，如果不存在，则进行复制拷贝
        if(!FileUtil.exist(subTempPath)){
            FileUtil.mkdir(subTempPath);
            FileUtil.copy(sourceRootPath,subTempPath,true);
        }


        //3：确定输入文件绝对路径以及输出文件绝对路径
        String outputPath = inputPath+".ftl";
        String copiedDirName = FileUtil.getLastPathEle(Paths.get(sourceRootPath)).toString();
        String fileInputPath = subTempPath+File.separator+copiedDirName+File.separator+inputPath;
        String fileOutputPath = subTempPath+File.separator+copiedDirName+File.separator+outputPath;


        //4：元信息中的文件配置信息
        Meta.FileConfigDTO.FilesInfo filesInfo = new Meta.FileConfigDTO.FilesInfo();
        //对InputPath和OutPutPath路径分隔符进行处理
        String newInputPath = inputPath.replaceAll("\\\\", "/");
        String newOutputPath = outputPath.replaceAll("\\\\","/");
        filesInfo.setInputPath(newInputPath);
        filesInfo.setOutputPath(newOutputPath);
        filesInfo.setType(FileTypeEnum.FILE.getValue());


        //5：开始生成对应的动态模板文件以及元信息文件
        //5.1生成动态模板文件
        //判断一下是不是首次制作，如果是首次制作就读取源文件内容，如果不是首次挖坑就读取上一次挖坑好后的文件内容
        String sourceContent;
        if(FileUtil.exist(fileOutputPath)){
            //不是首次制作
            sourceContent = FileUtil.readUtf8String(new File(fileOutputPath));
        }else{
            //是首次制作
            sourceContent = FileUtil.readUtf8String(new File(fileInputPath));
        }
        //进行替换
        String replacement = String.format("${%s}",modelInfo.getFieldName());
        String replaceContent = StrUtil.replace(sourceContent, searchStr, replacement);
        //将替换后的内容与原始内容进行对比，避免生成一些没有挖坑的文件
        if(sourceContent.equals(replaceContent)){
            filesInfo.setOutputPath(newInputPath);
            filesInfo.setGenerateType(GenerateFileTypeEnum.STATIC.getValue());
        }else{
            filesInfo.setGenerateType(GenerateFileTypeEnum.DYNAMIC.getValue());
            //将替换后的内容生成到输出文件当中
            FileUtil.writeUtf8String(replaceContent,new File(fileOutputPath));
        }


        //5.2 生成meta元信息文件
        String metaOutputPath = subTempPath+File.separator+copiedDirName+File.separator+"meta.json";
        //同样需要判断meta元信息文件是否生成，如果已经生成，那么就是增加配置，如果还没有生成，那么就是创建配置
        if (FileUtil.exist(metaOutputPath)){
            //已经生成，追加配置即可
            String oldJsonPrettyStr = FileUtil.readUtf8String(new File(metaOutputPath));
            Meta oldMeta = JSONUtil.toBean(oldJsonPrettyStr, Meta.class);
            //将新对象的meta根信息拷贝到老对象的meta根信息当中
            BeanUtil.copyProperties(newMeta,oldMeta, CopyOptions.create().ignoreNullValue());
            newMeta = oldMeta;

            //追加配置
            List<Meta.FileConfigDTO.FilesInfo> filesInfos = newMeta.getFileConfig().getFiles();
            filesInfos.add(filesInfo);
            List<Meta.ModelConfigDTO.ModelsInfo> models = newMeta.getModelConfig().getModels();
            models.add(modelInfo);

            //添加去重配置
            newMeta.getFileConfig().setFiles(distinctFiles(filesInfos));
            newMeta.getModelConfig().setModels(distinctModels(models));

            String jsonPrettyStr = JSONUtil.toJsonPrettyStr(newMeta);
            FileUtil.writeUtf8String(jsonPrettyStr,new File(metaOutputPath));
        }else{
            //填充文件配置
            Meta.FileConfigDTO fileConfig = new Meta.FileConfigDTO();
            fileConfig.setSourceRootPath(sourceRootPath);
            List<Meta.FileConfigDTO.FilesInfo> filesInfoList = new ArrayList<>();
            filesInfoList.add(filesInfo);
            fileConfig.setFiles(filesInfoList);
            newMeta.setFileConfig(fileConfig);
            //填充模型配置
            Meta.ModelConfigDTO modelConfig = new Meta.ModelConfigDTO();
            List<Meta.ModelConfigDTO.ModelsInfo> modelInfoList = new ArrayList<>();
            modelInfoList.add(modelInfo);
            modelConfig.setModels(modelInfoList);
            newMeta.setModelConfig(modelConfig);
            //生成元信息文件
            String jsonPrettyStr = JSONUtil.toJsonPrettyStr(newMeta);
            FileUtil.writeUtf8String(jsonPrettyStr,new File(metaOutputPath));
        }
        return id;
    }


    /**
     * 目录下所有模板文件生成或者多个路径下所有模板文件生成
     * @param sourceRootPath
     * @param templateMakerFileConfig
     * @param newMeta
     * @param modelInfo
     * @param searchStr
     * @param id
     */
    public static void makeMoreFileTemplate(String sourceRootPath, TemplateMakerFileConfig templateMakerFileConfig, Meta newMeta, Meta.ModelConfigDTO.ModelsInfo modelInfo, String searchStr, Long id){
        if (id==null){
            //使用雪花算法生成一个唯一的id作为每一次生成文件的目录名
            id = IdUtil.getSnowflakeNextId();
        }
        List<TemplateMakerFileConfig.FileInfoConfig> fileInfoConfigList = templateMakerFileConfig.getFiles();
        for (TemplateMakerFileConfig.FileInfoConfig fileInfoConfig : fileInfoConfigList){
            String inputPath = fileInfoConfig.getPath();
            String fileAbsolutePath = sourceRootPath+File.separator+inputPath;
            if(FileUtil.isDirectory(new File(fileAbsolutePath))){
                //对指定路径下的文件夹进行过滤，随后返回过滤完成后的文件列表，进行之后的操作
                List<File> files = FileFilter.doMoreFileFilter(fileAbsolutePath, fileInfoConfig.getFilterConfigs());
                for(File file:files){
                    String absolutePath = file.getAbsolutePath();
                    String fileInputPath = absolutePath.replace(sourceRootPath+File.separator, "");
                    makeTemplate(sourceRootPath,fileInputPath,newMeta,modelInfo,searchStr,id);
                }
            }else{
                //不是文件无需遍历
                makeTemplate(sourceRootPath,inputPath,newMeta,modelInfo,searchStr,id);
            }
        }
    }

    /**
     * 实现文件去重
     * @param filesInfos
     * @return
     */
    public static List<Meta.FileConfigDTO.FilesInfo> distinctFiles(List<Meta.FileConfigDTO.FilesInfo> filesInfos){
        return new ArrayList(
                filesInfos.stream()
                        .collect(
                                Collectors.toMap(Meta.FileConfigDTO.FilesInfo::getInputPath,v->v,(exits,replacement)->replacement)
                        ).values());
    }

    /**
     * 实现模型数据去重
     * @param modelsInfos
     * @return
     */
    public static List<Meta.ModelConfigDTO.ModelsInfo> distinctModels(List<Meta.ModelConfigDTO.ModelsInfo> modelsInfos){
        return new ArrayList(
                modelsInfos.stream()
                        .collect(
                                Collectors.toMap(Meta.ModelConfigDTO.ModelsInfo::getFieldName, v->v,(exits, replacement)->replacement)
                        ).values());
    }

    public static void main(String[] args) {
        String projectPath = System.getProperty("user.dir");
//        String sourceRootPath = new File(projectPath).getParent()+File.separator+"lzy-generator-demo-project"+File.separator+"acm-template";
//        String inputPath = "src/com/lzy/acm/MainTemplate.java";

        String sourceRootPath = new File(projectPath).getParent()+File.separator+"lzy-generator-demo-project"+File.separator+"springboot-init-master";
        String inputPath = "src/main/java/com/yupi/springbootinit";
        String inputPath1 = "src/main/java/com/yupi/springbootinit/common";
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
                .value("Base").build();
        List<FileFilterConfig> filterConfigList = Arrays.asList(filterConfig1);
        fileInfoConfig1.setPath(inputPath1);
        fileInfoConfig1.setFilterConfigs(filterConfigList);

        TemplateMakerFileConfig.FileInfoConfig fileInfoConfig2 = new TemplateMakerFileConfig.FileInfoConfig();
        fileInfoConfig2.setPath(inputPath2);
        FileFilterConfig filterConfig2 = FileFilterConfig.builder()
                .range(FilterRangeEnum.FILENAME.getValue())
                .rule(FilterTypeEnum.STARTS_WITH.getValue())
                .value("File").build();
        fileInfoConfig2.setFilterConfigs(Arrays.asList(filterConfig2));

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

        makeMoreFileTemplate(sourceRootPath, config,meta,modelInfo,searchStr,null);
    }
}