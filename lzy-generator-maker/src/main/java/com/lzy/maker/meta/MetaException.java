package com.lzy.maker.meta;

/**
 * 元信息异常类
 * @author lzy
 * @date 2024-01-12
 */
public class MetaException extends RuntimeException{

    public MetaException(String message) {
        super(message);
    }

    public MetaException(String message, Throwable cause) {
        super(message, cause);
    }
}
