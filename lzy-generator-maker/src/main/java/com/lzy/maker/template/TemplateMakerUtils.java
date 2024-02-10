package com.lzy.maker.template;

import cn.hutool.core.util.StrUtil;
import com.lzy.maker.meta.Meta;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author lzy
 * @date 2024-02-10
 * 模板生成工具-工具类
 */
public class TemplateMakerUtils {

    /**
     * 是否删除外层文件中重复的组内配置文件
     * @param filesInfoList
     */
    public static List<Meta.FileConfigDTO.FilesInfo> removeGroupFileInRoot(List<Meta.FileConfigDTO.FilesInfo> filesInfoList){
        //先找到所有的分组
        List<Meta.FileConfigDTO.FilesInfo> groupList = filesInfoList.stream().filter(filesInfo -> StrUtil.isNotBlank(filesInfo.getGroupKey()))
                .collect(Collectors.toList());
        //对筛选后的分组中的文件进行打平，汇集到一个set当中，便于去重
        Set<String> groupFileSet = groupList.stream()
                .flatMap(filesInfo -> filesInfo.getFiles().stream())
                .map(Meta.FileConfigDTO.FilesInfo::getOutputPath)
                .collect(Collectors.toSet());
        //根据用户指定条件，执行对应的去重
        List<Meta.FileConfigDTO.FilesInfo> finalList = filesInfoList.stream()
                .filter(filesInfo -> !groupFileSet.contains(filesInfo.getOutputPath()))
                .collect(Collectors.toList());
        return finalList;
    }
}
