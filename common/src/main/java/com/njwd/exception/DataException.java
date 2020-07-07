package com.njwd.exception;



/**
 * 数据异常类(数据处理时手动抛出异常)
 *
 * @author shenhf
 */
public class DataException extends RuntimeException {

    private static final long serialVersionUID = -6914996666319154848L;
    public final int code;
    public final String message;
    public Object data;

    public DataException(String message, ResultCode resultCode) {
        super(message);
        this.code = resultCode.code;
        this.message = message;
    }

    public DataException(String message, ResultCode resultCode, Object data) {
        this(message, resultCode);
        this.data = data;
    }

    public DataException(ResultCode resultCode) {
        this(resultCode.message, resultCode);
    }

    public DataException(ResultCode resultCode, Object data) {
        this(resultCode.message, resultCode, data);
    }
}
