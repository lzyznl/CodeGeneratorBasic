package com.lzy.maker.template;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.lzy.maker.meta.Meta;
import com.lzy.maker.meta.enums.FileTypeEnum;
import com.lzy.maker.meta.enums.GenerateFileTypeEnum;
import com.lzy.maker.template.Filter.FileFilter;
import com.lzy.maker.template.model.TemplateMakerFileConfig;

import java.io.File;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
/**
 * @author lzy
 * @date 2024-01-26
 * 实现模板制作工具，提高生产效率
 */
public class TemplateMaker {

    //todo 这个模板制作工具还是有bug感觉

    /**
     * 制作模板
     * @param sourceRootPath 源文件的路径
     * @param templateMakerFileConfig 模板文件配置类
     * @param newMeta   元信息的根信息
     * @param modelInfo 元信息中的模型信息
     * @param searchStr 所要替换的字符串内容
     * @param id        所对应的临时目录
     * @return
     */
    public static long makeTemplate(String sourceRootPath, TemplateMakerFileConfig templateMakerFileConfig, Meta newMeta, Meta.ModelConfigDTO.ModelsInfo modelInfo,String searchStr,Long id){
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
        String copiedDirName = FileUtil.getLastPathEle(Paths.get(sourceRootPath)).toString();
        //4：生成文件模板
        List<Meta.FileConfigDTO.FilesInfo> newFileInfoList = new ArrayList<>();
        for(TemplateMakerFileConfig.FileInfoConfig fileInfoConfig:templateMakerFileConfig.getFiles()){
            String inputFilePath = fileInfoConfig.getPath();
            String currentSourceRootPath = "";
            //如果是相对路径，则改为绝对路径
            if(!inputFilePath.startsWith(sourceRootPath)){
                currentSourceRootPath = subTempPath+File.separator+copiedDirName;
                inputFilePath = currentSourceRootPath+File.separator+inputFilePath;
            }
            //获取到过滤后的文件列表
            List<File> fileList = FileFilter.doMoreFileFilter(inputFilePath,fileInfoConfig.getFilterConfigs());
            //对过滤后的文件列表再次进行过滤，过滤出文件名称尾部有.ftl文件
            fileList = fileList.stream()
                    .filter(file -> !file.getAbsolutePath().endsWith(".ftl")).collect(Collectors.toList());
            for(File file:fileList){
                Meta.FileConfigDTO.FilesInfo filesInfo = getFilesInfo(modelInfo,currentSourceRootPath,searchStr,file);
                newFileInfoList.add(filesInfo);
            }
        }

        //如果是文件组
        TemplateMakerFileConfig.FileGroupInfoConfig fileGroupInfoConfig = templateMakerFileConfig.getFileGroupInfoConfig();
        if(fileGroupInfoConfig!=null){
            String condition = fileGroupInfoConfig.getCondition();
            String groupKey = fileGroupInfoConfig.getGroupKey();
            String groupName = fileGroupInfoConfig.getGroupDescription();

            //新增分组配置
            Meta.FileConfigDTO.FilesInfo filesInfo = new Meta.FileConfigDTO.FilesInfo();
            filesInfo.setCondition(condition);
            filesInfo.setGroupKey(groupKey);
            filesInfo.setGroupDescription(groupName);
            filesInfo.setFiles(newFileInfoList);
            newFileInfoList = new ArrayList<>();
            newFileInfoList.add(filesInfo);
        }

        //5.2 生成meta元信息文件
        String metaOutputPath = subTempPath+File.separator+"meta.json";
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
            filesInfos.addAll(newFileInfoList);
            List<Meta.ModelConfigDTO.ModelsInfo> models = newMeta.getModelConfig().getModels();
            models.add(modelInfo);
            
            //对追加后的配置进行去重处理
            newMeta.getFileConfig().setFiles(distinctFiles(filesInfos));
            newMeta.getModelConfig().setModels(distinctModels(models));

            String jsonPrettyStr = JSONUtil.toJsonPrettyStr(newMeta);
            FileUtil.writeUtf8String(jsonPrettyStr,new File(metaOutputPath));
        }else{
            //填充文件配置
            Meta.FileConfigDTO fileConfig = new Meta.FileConfigDTO();
            fileConfig.setSourceRootPath(sourceRootPath);
            List<Meta.FileConfigDTO.FilesInfo> filesInfoList = new ArrayList<>();
            filesInfoList.addAll(newFileInfoList);
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

    private static Meta.FileConfigDTO.FilesInfo getFilesInfo(Meta.ModelConfigDTO.ModelsInfo modelInfo, String sourceRootPath,String searchStr, File file) {
        String inputPath = file.getAbsolutePath();
        sourceRootPath = sourceRootPath.replaceAll("\\\\","/");
        String replacedFilePath = inputPath.replaceAll("\\\\", "/");
        String finalInputPath = replacedFilePath.replace(sourceRootPath + "/", "");
        String fileInputPath = finalInputPath;
        String fileOutputPath = finalInputPath+".ftl";
        String fileInputAbsolutePath = file.getAbsolutePath();
        String fileOutputAbsolutePath = file.getAbsolutePath()+".ftl";


        //4：元信息中的文件配置信息
        Meta.FileConfigDTO.FilesInfo filesInfo = new Meta.FileConfigDTO.FilesInfo();
        filesInfo.setInputPath(fileOutputPath);
        filesInfo.setOutputPath(fileInputPath);
        filesInfo.setType(FileTypeEnum.FILE.getValue());


        //5：开始生成对应的动态模板文件以及元信息文件
        //5.1生成动态模板文件
        //判断一下是不是首次制作，如果是首次制作就读取源文件内容，如果不是首次挖坑就读取上一次挖坑好后的文件内容
        String sourceContent;
        boolean hasTemplate = FileUtil.exist(fileOutputAbsolutePath);
        if(hasTemplate){
            //不是首次制作
            sourceContent = FileUtil.readUtf8String(new File(fileOutputAbsolutePath));
        }else{
            //是首次制作
            sourceContent = FileUtil.readUtf8String(new File(fileInputAbsolutePath));
        }
        //进行替换
        String replacement = String.format("${%s}", modelInfo.getFieldName());
        String replaceContent = StrUtil.replace(sourceContent, searchStr, replacement);
        //将替换后的内容与原始内容进行对比，避免生成一些没有挖坑的文件
        boolean contentEquals = sourceContent.equals(replaceContent);
        //没有模板文件，同时没有内容更改，那么就是静态文件
        if(!hasTemplate){
            if(contentEquals){
                filesInfo.setInputPath(fileInputPath);
                filesInfo.setGenerateType(GenerateFileTypeEnum.STATIC.getValue());
            }else{
                //生成动态文件
                FileUtil.writeUtf8String(replaceContent,new File(fileOutputAbsolutePath));
                filesInfo.setGenerateType(GenerateFileTypeEnum.DYNAMIC.getValue());
            }
        }else{
            filesInfo.setGenerateType(GenerateFileTypeEnum.DYNAMIC.getValue());
            if(!contentEquals){
                //将替换后的内容生成到输出文件当中
                FileUtil.writeUtf8String(replaceContent,new File(fileOutputAbsolutePath));
            }
        }
        return filesInfo;
    }


    /**
     * 实现文件去重(同时实现对分组的去重)
     * @param filesInfos
     * @return
     */
    public static List<Meta.FileConfigDTO.FilesInfo> distinctFiles(List<Meta.FileConfigDTO.FilesInfo> filesInfos){
        //进行分组处理
        //可能的情况{"groupKey1":files[1,2]},{"groupKey2":files[1,3]},{"groupKey2":files[1,2]};
        List<Meta.FileConfigDTO.FilesInfo> finalDistinctList = new ArrayList<>();
        //筛选出具有分组的配置并根据groupKey进行合并
        Map<String, List<Meta.FileConfigDTO.FilesInfo>> waitProcessMap = filesInfos.stream()
                .filter(file -> {
                    return StrUtil.isNotBlank(file.getGroupKey());
                }).collect(
                        Collectors.groupingBy(Meta.FileConfigDTO.FilesInfo::getGroupKey)
                );
        //{"groupKey1":files:{[1,2],[1,3]}},{"groupKey2",files:{[1,2]}
        //定义去重后的map，去重后每一个map的值是唯一的
        Map<String, Meta.FileConfigDTO.FilesInfo> distinctMap = new HashMap<>();
        //接下来对每一个分组中的文件进行打散去重
        for(Map.Entry<String,List<Meta.FileConfigDTO.FilesInfo>> entry:waitProcessMap.entrySet()){
            List<Meta.FileConfigDTO.FilesInfo> tempGroupList = entry.getValue();
            ArrayList<Meta.FileConfigDTO.FilesInfo> distinctList = new ArrayList<>(tempGroupList.stream()
                    .flatMap(filesInfo -> filesInfo.getFiles().stream())
                    .collect(
                            Collectors.toMap(Meta.FileConfigDTO.FilesInfo::getOutputPath, v -> v, (exits, replacement) -> replacement)
                    ).values());
            //使用当前最新的group配置
            //获取到当前GroupKey分组下最后一个filesInfo
            Meta.FileConfigDTO.FilesInfo newsFileInfo = CollUtil.getLast(tempGroupList);
            //将该filesInfo的files列表设置为去重后的列表
            newsFileInfo.setFiles(distinctList);
            String key = entry.getKey();
            //添加到去重后的map当中
            distinctMap.put(key,newsFileInfo);
        }

        //添加到最终结果
        finalDistinctList.addAll(distinctMap.values());

        //接下来对没有分组的配置进行过滤
        List<Meta.FileConfigDTO.FilesInfo> noGroupList = filesInfos.stream()
                .filter(filesInfo -> {
                    return StrUtil.isBlank(filesInfo.getGroupKey());
                }).collect(Collectors.toList());
        finalDistinctList.addAll( new ArrayList(
        noGroupList.stream()
                .collect(
                        Collectors.toMap(Meta.FileConfigDTO.FilesInfo::getOutputPath,v->v,(exits,replacement)->replacement)
                ).values()));

        return finalDistinctList;
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

}
