package ${basePackage}.maker.cli.command.CommandType;

import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.ReflectUtil;
import ${basePackage}.maker.model.DataModel;
import picocli.CommandLine.Command;

import java.lang.reflect.Field;

/**
 * @author ${author}
 * @date ${createTime}
 * 生成文件的配置参数类
 */
@Command(name = "config", version = "Config 1.0", mixinStandardHelpOptions = true)
public class ConfigCommand implements Runnable{

    @Override
    public void run() {
        Field[] fields = ReflectUtil.getFields(DataModel.class);
        for(int i=0;i< ArrayUtil.length(fields);++i){
            System.out.println("字段类型:"+fields[i].getType()+",字段名称:"+fields[i].getName());
        }
    }
}
