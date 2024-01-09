package com.lzy.generator.file;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.ArrayUtil;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

/**
 * @author lzyaaa
 * @date  2024-01-08
 * 静态代码生成器
 */
public class StaticFileGenerator {


    /**
     * 直接使用Hutool工具类中的方法进行开发
     * @param srcPath
     * @param destPath
     */
    public static void copFileByHutool(String srcPath,String destPath){
        File srcFile = new File(srcPath);
        File destFile = new File(destPath);
        FileUtil.copy(srcFile, destFile, false);
    }


    /**
     * 根据递归进行文件的复制
     * @param srcFile
     * @param destFile
     */
    public static void copFileByRecursion(File srcFile,File destFile) throws IOException {

        if(srcFile.isDirectory()){
            System.out.println(srcFile.getName());
            File destOutputFile = new File(destFile,srcFile.getName());
            //如果是目录，首先创建目录
            if(!destOutputFile.exists()){
                destOutputFile.mkdir();
            }
            //获取该目录下的所有文件以及子目录
            File[] files = srcFile.listFiles();
            if(ArrayUtil.isEmpty(files)){
                return;
            }
            for (File file:files){
                copFileByRecursion(file,destOutputFile);
            }
        }else{
            Path destPath = destFile.toPath().resolve(srcFile.getName());
            Files.copy(srcFile.toPath(),destPath, StandardCopyOption.REPLACE_EXISTING);
        }
    }
}
