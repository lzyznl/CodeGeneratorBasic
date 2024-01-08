package com.lzy.maker.generator;

import com.lzy.maker.meta.Meta;
import com.lzy.maker.meta.MetaManger;

/**
 * @author lzy
 * @date 2024-01-08
 * 总体代码生成类
 */
public class MainGenerator {

    public static void main(String[] args) {
        MetaManger metaManger = new MetaManger();
        Meta metaModel = metaManger.getMetaModel();
        System.out.println(metaModel);
    }
}
