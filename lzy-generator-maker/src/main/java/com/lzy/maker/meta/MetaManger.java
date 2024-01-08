package com.lzy.maker.meta;

import cn.hutool.core.io.resource.ResourceUtil;
import cn.hutool.json.JSONUtil;

/**
 * @author lzy
 * @date 2024-01-08
 * 读取配置文件中的元信息，生成对应的实体对象
 * 此处使用双检锁单例模式（设计模式）
 */
public class MetaManger {

    private static volatile Meta meta;

    /**
     * 双检锁单例模式
     * @return
     */
    private static Meta getMeta(){
        if(meta==null){
            synchronized (MetaManger.class){
                if(meta==null){
                    meta = initMeta();
                }
            }
        }
        return meta;
    }

    private static Meta initMeta(){
        String metaJson = ResourceUtil.readUtf8Str("meta.json");
        Meta meta = JSONUtil.toBean(metaJson, Meta.class);
        return meta;
    }

    public Meta getMetaModel(){
        return getMeta();
    }
}
