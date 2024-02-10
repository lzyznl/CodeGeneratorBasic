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
import com.lzy.maker.template.model.TemplateMakerConfig;
import com.lzy.maker.template.model.TemplateMakerFileConfig;
import com.lzy.maker.template.model.TemplateMakerModelConfig;
import com.lzy.maker.template.model.TemplateMakerOutputConfig;

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


    /**
     * 制作模板
     * @param newMeta   元信息的根信息
     * @param sourceRootPath 源文件的路径
     * @param templateMakerFileConfig 模板制作工具文件配置
     * @param templateMakerModelConfig 模板制作工具模型配置
     * @param templateMakerOutputConfig 模板制作工具输出配置
     * @param id  所对应的临时目录id
     * @return
     */
    public static long makeTemplate(Meta newMeta, String sourceRootPath, TemplateMakerFileConfig templateMakerFileConfig, TemplateMakerModelConfig templateMakerModelConfig, TemplateMakerOutputConfig templateMakerOutputConfig,Long id){
        if (id==null){
            //使用雪花算法生成一个唯一的id作为每一次生成文件的目录名
            id = IdUtil.getSnowflakeNextId();
        }

        //生成一个基本的模板制作工具，我们基本需要从以下几个方面进行生成
        //1：找到原始的输入文件
        String projectPath = System.getProperty("user.dir");
        if(sourceRootPath!=null) {
            sourceRootPath = sourceRootPath.replaceAll("\\\\", "/");
        }

        //2：转移到一个新的隔离的工作目录下，避免污染原始文件目录
        String tempDir = projectPath+File.separator+".temp";
        String subTempPath = tempDir+File.separator+id;
        //判断文件是否存在，如果不存在，则进行复制拷贝
        if(!FileUtil.exist(subTempPath)){
            FileUtil.mkdir(subTempPath);
            FileUtil.copy(sourceRootPath,subTempPath,true);
        }else{
            sourceRootPath = FileUtil.loopFiles(new File(subTempPath),1,null)
                    .stream()
                    .filter(File::isDirectory)
                    .findFirst()
                    .orElseThrow(RuntimeException::new)
                    .getAbsolutePath()
                    .replaceAll("\\\\","/");
        }

        List<Meta.ModelConfigDTO.ModelsInfo> newModelsInfoList = getModelInfoList(templateMakerModelConfig);

        List<Meta.FileConfigDTO.FilesInfo> newFileInfoList = getFileInfoList(sourceRootPath, templateMakerFileConfig, templateMakerModelConfig, subTempPath);

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
            models.addAll(newModelsInfoList);
            
            //对追加后的配置进行去重处理
            newMeta.getFileConfig().setFiles(distinctFiles(filesInfos));
            newMeta.getModelConfig().setModels(distinctModels(models));

            //执行用户自定义的输出规则
            removeGroupFileFromRootFile(templateMakerOutputConfig,newMeta);

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
            modelInfoList.addAll(newModelsInfoList);
            modelConfig.setModels(modelInfoList);
            newMeta.setModelConfig(modelConfig);

            //执行用户自定义的输出规则
            removeGroupFileFromRootFile(templateMakerOutputConfig,newMeta);

            //生成元信息文件
            String jsonPrettyStr = JSONUtil.toJsonPrettyStr(newMeta);
            FileUtil.writeUtf8String(jsonPrettyStr,new File(metaOutputPath));
        }
        return id;
    }

    /**
     * 是否删除外层文件中重复的组内配置文件
     * @param templateMakerOutputConfig
     * @param meta
     * @return
     */
    private static void removeGroupFileFromRootFile(TemplateMakerOutputConfig templateMakerOutputConfig, Meta meta) {
        if(templateMakerOutputConfig !=null){
            Boolean removeGroupFileFromRoot = templateMakerOutputConfig.getRemoveGroupFileFromRoot();
            if(removeGroupFileFromRoot){
                List<Meta.FileConfigDTO.FilesInfo> filesInfos = meta.getFileConfig().getFiles();
                meta.getFileConfig().setFiles(TemplateMakerUtils.removeGroupFileInRoot(filesInfos));
            }
        }
    }

    /**
     * 生成meta元信息文件中的模型配置
     * @param templateMakerModelConfig
     * @return
     */
    private static List<Meta.ModelConfigDTO.ModelsInfo> getModelInfoList(TemplateMakerModelConfig templateMakerModelConfig) {
        //处理模型组
        List<Meta.ModelConfigDTO.ModelsInfo> newModelsInfoList = new ArrayList<>();

        //增加非空校验
        if(templateMakerModelConfig==null){
            return newModelsInfoList;
        }
        List<TemplateMakerModelConfig.ModelInfoConfig> modelConfigList = templateMakerModelConfig.getModels();
        if(modelConfigList==null||modelConfigList.isEmpty()){
            return newModelsInfoList;
        }
        List<Meta.ModelConfigDTO.ModelsInfo> modelsInfoList = modelConfigList.stream()
                .map(modelInfoConfig -> {
                    Meta.ModelConfigDTO.ModelsInfo modelsInfo = new Meta.ModelConfigDTO.ModelsInfo();
                    BeanUtil.copyProperties(modelInfoConfig, modelsInfo);
                    return modelsInfo;
                }).collect(Collectors.toList());
        TemplateMakerModelConfig.ModelGroupConfig modelGroupConfig = templateMakerModelConfig.getModelGroupConfig();
        if(modelGroupConfig!=null){
            //有模型分组
            String condition = modelGroupConfig.getCondition();
            String groupKey = modelGroupConfig.getGroupKey();
            String groupName = modelGroupConfig.getGroupName();

            Meta.ModelConfigDTO.ModelsInfo modelsInfo = new Meta.ModelConfigDTO.ModelsInfo();
            modelsInfo.setGroupKey(groupKey);
            modelsInfo.setCondition(condition);
            modelsInfo.setGroupName(groupName);
            modelsInfo.setModels(modelsInfoList);
            newModelsInfoList.add(modelsInfo);
        }else{
            //没有模型分组
            newModelsInfoList.addAll(modelsInfoList);
        }
        return newModelsInfoList;
    }

    /**
     * 生成meta元信息文件中的文件配置
     * @param sourceRootPath
     * @param templateMakerFileConfig
     * @param templateMakerModelConfig
     * @param subTempPath
     * @return
     */
    private static List<Meta.FileConfigDTO.FilesInfo> getFileInfoList(String sourceRootPath, TemplateMakerFileConfig templateMakerFileConfig, TemplateMakerModelConfig templateMakerModelConfig, String subTempPath) {
        List<Meta.FileConfigDTO.FilesInfo> newFileInfoList = new ArrayList<>();
        //非空校验
        if(templateMakerFileConfig==null){
            return newFileInfoList;
        }
        List<TemplateMakerFileConfig.FileInfoConfig> files = templateMakerFileConfig.getFiles();
        if(files==null|| files.isEmpty()){
            return newFileInfoList;
        }


        //3：确定输入文件绝对路径以及输出文件绝对路径
        String copiedDirName = FileUtil.getLastPathEle(Paths.get(sourceRootPath)).toString();
        //4：生成文件模板
        for(TemplateMakerFileConfig.FileInfoConfig fileInfoConfig: files){
            String inputFilePath = fileInfoConfig.getPath();
            String currentSourceRootPath = "";
            //如果是相对路径，则改为绝对路径
            if(!inputFilePath.startsWith(sourceRootPath)){
                currentSourceRootPath = subTempPath +File.separator+copiedDirName;
                inputFilePath = currentSourceRootPath+File.separator+inputFilePath;
            }
            //获取到过滤后的文件列表
            List<File> fileList = FileFilter.doMoreFileFilter(inputFilePath,fileInfoConfig.getFilterConfigs());
            //对过滤后的文件列表再次进行过滤，过滤出文件名称尾部有.ftl文件
            fileList = fileList.stream()
                    .filter(file -> !file.getAbsolutePath().endsWith(".ftl")).collect(Collectors.toList());
            for(File file:fileList){
                Meta.FileConfigDTO.FilesInfo filesInfo = getFilesInfo(templateMakerModelConfig,currentSourceRootPath,file,fileInfoConfig);
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
        return newFileInfoList;
    }


    /**
     * 根据templateMaker.json中的配置信息生成对应的FileInfo信息
     * @param templateMakerModelConfig
     * @param sourceRootPath
     * @param file
     * @return
     */
    private static Meta.FileConfigDTO.FilesInfo getFilesInfo(TemplateMakerModelConfig templateMakerModelConfig, String sourceRootPath, File file, TemplateMakerFileConfig.FileInfoConfig fileInfoConfig) {
        String inputPath = file.getAbsolutePath();
        sourceRootPath = sourceRootPath.replaceAll("\\\\","/");
        String replacedFilePath = inputPath.replaceAll("\\\\", "/");
        String finalInputPath = replacedFilePath.replace(sourceRootPath + "/", "");
        String fileInputPath = finalInputPath;
        String fileOutputPath = finalInputPath+".ftl";
        String fileInputAbsolutePath = file.getAbsolutePath();
        String fileOutputAbsolutePath = file.getAbsolutePath()+".ftl";

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

        String newFileContent = sourceContent;
        //处理模型信息，遍历模型集合，对一个文件进行多次挖坑操作
        List<TemplateMakerModelConfig.ModelInfoConfig> modelInfoConfigs = templateMakerModelConfig.getModels();
        TemplateMakerModelConfig.ModelGroupConfig modelGroupConfig = templateMakerModelConfig.getModelGroupConfig();
        for(TemplateMakerModelConfig.ModelInfoConfig modelInfoConfig:modelInfoConfigs){
            String fieldName = modelInfoConfig.getFieldName();
            String replacement;
            if(modelGroupConfig==null){
                //没有模型分组，只是简单的多个模型
                replacement = String.format("${%s}", fieldName);
            }else{
                //存在模型分组
                String groupKey = modelGroupConfig.getGroupKey();
                replacement = String.format("${%s.%s}",groupKey,fieldName);
            }
            newFileContent = StrUtil.replace(newFileContent,modelInfoConfig.getReplaceText(),replacement);
        }

        //4：元信息中的文件配置信息
        Meta.FileConfigDTO.FilesInfo filesInfo = new Meta.FileConfigDTO.FilesInfo();
        filesInfo.setInputPath(fileOutputPath);
        filesInfo.setOutputPath(fileInputPath);
        filesInfo.setCondition(fileInfoConfig.getCondition());
        filesInfo.setType(FileTypeEnum.FILE.getValue());


        //5：开始生成对应的动态模板文件以及元信息文件
        //5.1生成动态模板文件
        //将替换后的内容与原始内容进行对比，避免生成一些没有挖坑的文件
        boolean contentEquals = sourceContent.equals(newFileContent);
        //没有模板文件，同时没有内容更改，那么就是静态文件
        if(!hasTemplate){
            if(contentEquals){
                filesInfo.setInputPath(fileInputPath);
                filesInfo.setGenerateType(GenerateFileTypeEnum.STATIC.getValue());
            }else{
                //生成动态文件
                FileUtil.writeUtf8String(newFileContent,new File(fileOutputAbsolutePath));
                filesInfo.setGenerateType(GenerateFileTypeEnum.DYNAMIC.getValue());
            }
        }else{
            filesInfo.setGenerateType(GenerateFileTypeEnum.DYNAMIC.getValue());
            if(!contentEquals){
                //将替换后的内容生成到输出文件当中
                FileUtil.writeUtf8String(newFileContent,new File(fileOutputAbsolutePath));
            }
        }
        return filesInfo;
    }


    /**
     * 实现文件去重(同时实现对文件分组的去重)
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
     * 实现模型数据去重(同时实现对模型分组的去重)
     * @param modelsInfos
     * @return
     */
    public static List<Meta.ModelConfigDTO.ModelsInfo> distinctModels(List<Meta.ModelConfigDTO.ModelsInfo> modelsInfos){
        //首先先对分组去重,过滤出所有的分组
        Map<String, List<Meta.ModelConfigDTO.ModelsInfo>> waitProcessMap = modelsInfos.stream()
                .filter(modelsInfo -> {
                    return StrUtil.isNotBlank(modelsInfo.getGroupKey());
                }).collect(
                        Collectors.groupingBy(Meta.ModelConfigDTO.ModelsInfo::getGroupKey)
                );
        Map<String, Meta.ModelConfigDTO.ModelsInfo> distinctMap = new HashMap<>();
        //对分组进行打散，然后进行去重
        for(Map.Entry<String,List<Meta.ModelConfigDTO.ModelsInfo>> entry:waitProcessMap.entrySet()){
            List<Meta.ModelConfigDTO.ModelsInfo> tempModelInfoList = entry.getValue();
            ArrayList<Meta.ModelConfigDTO.ModelsInfo> distinctModelInfoList = new ArrayList<>(tempModelInfoList.stream()
                    .flatMap(modelsInfo -> modelsInfo.getModels().stream())
                    .collect(Collectors.toMap(Meta.ModelConfigDTO.ModelsInfo::getFieldName, o -> o, (exists, replacement) -> replacement))
                    .values());
            //更新当前最新的modelInfo分组配置
            Meta.ModelConfigDTO.ModelsInfo modelsInfo = CollUtil.getLast(tempModelInfoList);
            modelsInfo.setModels(distinctModelInfoList);
            String groupKey = entry.getKey();
            distinctMap.put(groupKey,modelsInfo);
        }
        List<Meta.ModelConfigDTO.ModelsInfo> distinctModelsInfoList = new ArrayList<>();

        distinctModelsInfoList.addAll(distinctMap.values());

        //接下来对不是分组的模型进行去重处理
        List<Meta.ModelConfigDTO.ModelsInfo> noGroupList = modelsInfos.stream()
                .filter(modelsInfo -> {
                    return StrUtil.isBlank(modelsInfo.getGroupKey());
                }).collect(Collectors.toList());

        ArrayList<Meta.ModelConfigDTO.ModelsInfo> distinctNoGroupList = new ArrayList<>(noGroupList.stream()
                .collect(Collectors.toMap(
                        Meta.ModelConfigDTO.ModelsInfo::getFieldName, o -> o, (exists, replacement) -> replacement)
                ).values());
        distinctModelsInfoList.addAll(distinctNoGroupList);

        return distinctModelsInfoList;
    }


    /**
     * 重载方法，接收封装参数
     * @param templateMakerConfig
     * @return
     */
    public static long makeTemplate(TemplateMakerConfig templateMakerConfig){
        Meta meta = templateMakerConfig.getMeta();
        String sourceRootPath = templateMakerConfig.getSourceRootPath();
        TemplateMakerOutputConfig outputConfig = templateMakerConfig.getOutputConfig();
        TemplateMakerFileConfig templateMakerFileConfig = templateMakerConfig.getFileConfig();
        TemplateMakerModelConfig templateMakerModelConfig = templateMakerConfig.getModelConfig();
        Long id = templateMakerConfig.getId();
        return makeTemplate(meta,sourceRootPath,templateMakerFileConfig,templateMakerModelConfig,outputConfig,id);
    }

}
