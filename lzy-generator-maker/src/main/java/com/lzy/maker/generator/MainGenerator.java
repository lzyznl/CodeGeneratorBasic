package com.lzy.maker.generator;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.resource.ClassPathResource;
import cn.hutool.core.util.StrUtil;
import com.lzy.maker.Main;
import com.lzy.maker.generator.file.DynamicFileGenerator;
import com.lzy.maker.meta.Meta;
import com.lzy.maker.meta.MetaManger;
import freemarker.template.TemplateException;

import java.io.File;
import java.io.IOException;

/**
 * @author lzy
 * @date 2024-01-08
 * 总体代码生成类，继承自模板方法类
 */
public class MainGenerator extends GeneratorTemplate{
    @Override
    protected void buildDist(Meta metaModel, String OutputRootPath, String jarName) {
        super.buildDist(metaModel, OutputRootPath, jarName);
    }
}
