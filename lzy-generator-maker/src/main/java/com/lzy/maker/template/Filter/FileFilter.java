package com.lzy.maker.template.Filter;

import cn.hutool.core.io.FileUtil;
import com.lzy.maker.template.model.FileFilterConfig;
import com.lzy.maker.template.model.enums.FilterRangeEnum;
import com.lzy.maker.template.model.enums.FilterTypeEnum;

import java.io.File;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author lzy
 * @date 2024-01-29
 * 文件过滤器
 */
public class FileFilter {


    /**
     * 多个文件的过滤
     * @param filePath 文件的绝对路径
     * @param config
     * @return
     */
    public static List<File> doMoreFileFilter(String filePath,List<FileFilterConfig> config){
        List<File> files = FileUtil.loopFiles(filePath);
        return files.stream()
                .filter(file -> {
                    return doSingleFilter(config,file);
                }).collect(Collectors.toList());
    }

    /**
     * 单个文件过滤
     * @param FilterConfig
     * @param file
     * @return
     */
    public static Boolean doSingleFilter(List<FileFilterConfig> FilterConfig, File file){
        String fileName = file.getName();
        String fileContent = FileUtil.readUtf8String(file);

        boolean fact = true;
        if(FilterConfig==null|| FilterConfig.isEmpty()){
            return fact;
        }

        for (FileFilterConfig filterConfig:FilterConfig){
            String range = filterConfig.getRange();
            String rule = filterConfig.getRule();
            String value = filterConfig.getValue();

            FilterRangeEnum rangeEnum = FilterRangeEnum.getEnumByValue(range);
            if(rangeEnum==null){
                return false;
            }
            FilterTypeEnum ruleEnum = FilterTypeEnum.getEnumByValue(rule);
            if(ruleEnum==null){
                return false;
            }

            //默认过滤内容为文件名称
            String filterContent = fileName;
            switch (rangeEnum){
                case FILENAME:
                    filterContent=fileName;
                    break;
                case FILECONTENT:
                    filterContent=fileContent;
                    break;
                default:
            }

            switch (ruleEnum){
                case CONTAINS:
                    fact = filterContent.contains(value);
                    break;
                case STARTS_WITH:
                    fact = filterContent.startsWith(value);
                    break;
                case ENDS_WITH:
                    fact = filterContent.endsWith(value);
                    break;
                case REGEX:
                    fact = filterContent.matches(value);
                    break;
                case EQUALS:
                    fact = filterContent.equals(value);
                    break;
            }
        }
        return fact;
    }

}
