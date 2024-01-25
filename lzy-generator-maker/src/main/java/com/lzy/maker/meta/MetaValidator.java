package com.lzy.maker.meta;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import com.lzy.maker.meta.enums.FileTypeEnum;
import com.lzy.maker.meta.enums.FiledTypeEnum;
import com.lzy.maker.meta.enums.GenerateFileTypeEnum;

import java.nio.file.Paths;
import java.util.List;

/**
 * 元信息校验类
 * @author lzy
 * @date 2024-01-12
 */
public class MetaValidator {

    public static void doValidAndFill(Meta meta){
        validRootInfoAndFill(meta);
        validFileConfigAndFill(meta);
        validModelConfigAndFill(meta);
    }

    /**
     * 校验模型配置相关信息
     * @param meta
     */
    private static void validModelConfigAndFill(Meta meta) {
        //元信息模型配置校验以及填充
        Meta.ModelConfigDTO modelConfig = meta.getModelConfig();
        List<Meta.ModelConfigDTO.ModelsInfo> models = modelConfig.getModels();
        if(CollUtil.isEmpty(models)){
            throw new MetaException("未配置模型信息");
        }
        else{
            for (Meta.ModelConfigDTO.ModelsInfo info:models){

                //todo 此处如果是参数组则不进行校验
                if(StrUtil.isNotEmpty(info.getGroupKey())){
                    continue;
                }

                String fieldName = info.getFieldName();
                if(StrUtil.isBlank(fieldName)){
                    throw new MetaException("未填写 fieldName");
                }

                String type = info.getType();
                info.setType(StrUtil.blankToDefault(type, FiledTypeEnum.STRING.getValue()));

                String fullName = info.getFullName();
                info.setFullName(StrUtil.blankToDefault(fullName,"--"+fieldName));
            }
        }
    }

    /**
     * 校验文件配置相关信息
     * @param meta
     */
    private static void validFileConfigAndFill(Meta meta) {
        //元信息文件配置校验以及填充
        Meta.FileConfigDTO fileConfig = meta.getFileConfig();
        if(fileConfig==null){
            return ;
        }
        String sourceRootPath = fileConfig.getSourceRootPath();
        if(StrUtil.isBlank(sourceRootPath)){
            throw new MetaException("没有指定SourceRootPath");
        }
        String inputRootPath = fileConfig.getInputRootPath();
        if(StrUtil.isBlank(inputRootPath)){
            inputRootPath = ".source/"+
                    FileUtil.getLastPathEle(Paths.get(sourceRootPath)).getFileName().toString();
            fileConfig.setInputRootPath(inputRootPath);
        }
        String outputRootPath = fileConfig.getOutputRootPath();
        String newOutputRootPath = StrUtil.blankToDefault(outputRootPath, "generated");
        fileConfig.setOutputRootPath(newOutputRootPath);

        String Type = fileConfig.getType();
        fileConfig.setType(StrUtil.blankToDefault(Type, FileTypeEnum.DIR.getValue()));

        List<Meta.FileConfigDTO.FilesInfo> files = fileConfig.getFiles();
        if (CollUtil.isEmpty(files)){
            throw new MetaException("没有对应的文件配置信息");
        }
        for (Meta.FileConfigDTO.FilesInfo info:files){
            //todo 遇到文件组时先不对里面的路径做校验，随后再添加
            if(info.getType().equals(FileTypeEnum.GROUP.getValue())){
                continue;
            }
            String inputPath = info.getInputPath();
            if(StrUtil.isBlank(inputPath)){
                throw new MetaException("未填写InputPath");
            }

            String outputPath = info.getOutputPath();
            StrUtil.blankToDefault(outputPath,inputPath);
            info.setOutputPath(outputPath);

            String type = info.getType();
            if(StrUtil.isBlank(type)){
                if(StrUtil.isNotBlank(FileUtil.getSuffix(inputPath))){
                    type = FileTypeEnum.FILE.getValue();
                }else{
                    type = FileTypeEnum.DIR.getValue();
                }
                info.setType(type);
            }
            String generateType = info.getGenerateType();
            if(StrUtil.isBlank(generateType)){
                if(inputPath.endsWith(".ftl")){
                    generateType = GenerateFileTypeEnum.DYNAMIC.getValue();
                }else{
                    generateType = GenerateFileTypeEnum.STATIC.getValue();
                }
                info.setGenerateType(generateType);
            }
        }
    }

    /**
     * 校验meta文件源文件根信息（基础信息）
     * @param meta
     */
    private static void validRootInfoAndFill(Meta meta) {

        String name = StrUtil.blankToDefault(meta.getName(), "my-generator");
        String description = StrUtil.blankToDefault(meta.getDescription(), "我的项目代码生成器");
        String basePackage = StrUtil.blankToDefault(meta.getBasePackage(), "com.lzy");
        String version = StrUtil.blankToDefault(meta.getVersion(), "1.0");
        String author = StrUtil.blankToDefault(meta.getAuthor(), "lzy");
        String createTime = StrUtil.blankToDefault(meta.getCreateTime(), DateUtil.now());

        meta.setName(name);
        meta.setDescription(description);
        meta.setVersion(version);
        meta.setBasePackage(basePackage);
        meta.setAuthor(author);
        meta.setCreateTime(createTime);

        /**
         * //圈复杂度优化之前的代码如下
         *         //元信息基本信息校验以及填充
         *         String name = meta.getName();
         *         if (StrUtil.isBlank(name)){
         *             name = "my-generator";
         *             meta.setName(name);
         *         }
         *         String description = meta.getDescription();
         *         if(StrUtil.isBlank(description)){
         *             description = "我的项目代码生成器";
         *             meta.setDescription(description);
         *         }
         *         String basePackage = meta.getBasePackage();
         *         if(StrUtil.isBlank(basePackage)){
         *             basePackage = "com.lzy";
         *             meta.setBasePackage(basePackage);
         *         }
         *         String version = meta.getVersion();
         *         if(StrUtil.isBlank(version)){
         *             version = "1.0";
         *             meta.setVersion(version);
         *         }
         *         String author = meta.getAuthor();
         *         if(StrUtil.isEmpty(author)){
         *             author = "lzy";
         *             meta.setAuthor(author);
         *         }
         *         String createTime = meta.getCreateTime();
         *         if(StrUtil.isEmpty(createTime)){
         *             createTime = DateUtil.now();
         *             meta.setCreateTime(createTime);
         *         }
         */
    }
}
