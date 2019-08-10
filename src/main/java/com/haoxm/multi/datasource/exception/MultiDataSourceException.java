package com.haoxm.multi.datasource.exception;

/**
 * @Author: haoxm
 * @Description:
 * @Date: 2019/8/10 16:16
 * @Modified By:
 */
public class MultiDataSourceException extends RuntimeException {

    public MultiDataSourceException() {
    }

    public MultiDataSourceException(String message) {
        super(message);
    }

    public MultiDataSourceException(String message, Throwable cause) {
        super(message, cause);
    }
}
