package com.lzy.maker.generator;

import cn.hutool.core.io.FileUtil;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.attribute.PosixFilePermission;
import java.nio.file.attribute.PosixFilePermissions;
import java.util.Set;

/**
 * @author lzy
 * @date 2024-01-11
 * 脚本生成器
 */
public class ScriptGenerator {


    public static void doGenerate(String outputPath,String jarPath,Boolean isLinux){
        StringBuilder scriptBuilder = new StringBuilder();
        if(isLinux){
            //Linux脚本
            //#! bin/bash
            //java -jar target/lzy-generator-basic-1.0-SNAPSHOT-jar-with-dependencies.jar "$@"
            scriptBuilder.append("#! bin/bash").append("\n");
            scriptBuilder.append("java -jar ").append(jarPath).append(" \"$@\"");
        }else{
            //windows脚本
            //@echo off
            //java -jar target/lzy-generator-basic-1.0-SNAPSHOT-jar-with-dependencies.jar %*
            scriptBuilder.append("@echo off").append("\n");
            scriptBuilder.append("java -jar ").append(jarPath).append(" %*");
        }
        String cmdStr = scriptBuilder.toString();
        if(isLinux){
            FileUtil.writeBytes(cmdStr.getBytes(StandardCharsets.UTF_8),outputPath);
            Set<PosixFilePermission> permissions = PosixFilePermissions.fromString("rwxrwxrwx");
            try {
                Files.setPosixFilePermissions(Paths.get(outputPath),permissions);
            } catch (IOException e) {
            }
        }else{
            FileUtil.writeBytes(cmdStr.getBytes(StandardCharsets.UTF_8),outputPath+".bat");
        }
    }
}
