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
import java.io.File;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
/**
 * @author lzy
 * @date 2024-01-26
 * 实现模板制作工具，提高生产效率
 */
public class TemplateMaker {

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
        //3：确定输入文件路径以及输出文件路径
        String outputPath = inputPath+".ftl";
        String copiedDirName = FileUtil.getLastPathEle(Paths.get(sourceRootPath)).toString();
        String fileInputPath = subTempPath+File.separator+copiedDirName+File.separator+inputPath;
        String fileOutputPath = subTempPath+File.separator+copiedDirName+File.separator+outputPath;
        //4：元信息中的文件配置信息
        //todo 随后这边应该也是需要进行抽离的，可能会有多个文件要进行挖坑
        Meta.FileConfigDTO.FilesInfo filesInfo = new Meta.FileConfigDTO.FilesInfo();
        filesInfo.setInputPath(inputPath);
        filesInfo.setOutputPath(outputPath);
        filesInfo.setType(FileTypeEnum.FILE.getValue());
        filesInfo.setGenerateType(GenerateFileTypeEnum.DYNAMIC.getValue());
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
        //将替换后的内容生成到输出文件当中
        FileUtil.writeUtf8String(replaceContent,new File(fileOutputPath));
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
        String sourceRootPath = new File(projectPath).getParent()+File.separator+"lzy-generator-demo-project/acm-template";
        String inputPath = "src/com/lzy/acm/MainTemplate.java";


        Meta meta = new Meta();
        String name = "acm-template";
        String description = "ACM模板示例代码生成器";
        String basePackage = "com.lzy";
        meta.setName(name);
        meta.setDescription(description);
        meta.setBasePackage(basePackage);


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
        String searchStr = "MainTemplate";

        makeTemplate(sourceRootPath,inputPath,meta,modelInfo,searchStr,1750857753164410880L);
    }
}
