package cn.onlyhi.common.util;

import cn.onlyhi.common.enums.CodeEnum;

import java.io.Serializable;

public class Response implements Serializable {
    private static final long serialVersionUID = 8612112620973384029L;
    private boolean hasError;
    private int code;
    private String message;
    private Object data;

    public Response() {

    }

    public Response(boolean hasError, int code, String message, Object data) {
        super();
        this.hasError = hasError;
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public Response(boolean hasError, int code, String message) {
        super();
        this.hasError = hasError;
        this.code = code;
        this.message = message;
    }

    public static Response build(boolean hasError, int code, String message, Object data) {
        return new Response(hasError, code, message, data);
    }

    public static Response build(boolean hasError, int code, String message) {
        return new Response(hasError, code, message);
    }

    public static Response success() {
        return new Response(false, CodeEnum.SUCCESS.getCode(), CodeEnum.SUCCESS.getMsg());
    }

    public static Response success(Object data) {
        return new Response(false, CodeEnum.SUCCESS.getCode(), CodeEnum.SUCCESS.getMsg(), data);
    }

    public static Response successCustom(String message) {
        return new Response(false, CodeEnum.SUCCESS.getCode(), message);
    }

    public static Response successCustom(String message, Object data) {
        return new Response(false, CodeEnum.SUCCESS.getCode(), message, data);
    }

    public static Response errorCustom(String message) {
        return new Response(true, CodeEnum.FAILURE.getCode(), message);
    }

    public static Response error(int code, String message) {
        return new Response(true, code, message);
    }

    public static Response error(CodeEnum codeEnum) {
        return new Response(true, codeEnum.getCode(), codeEnum.getMsg());
    }

    public static Response error(CodeEnum codeEnum, Object data) {
        return new Response(true, codeEnum.getCode(), codeEnum.getMsg(), data);
    }

    public boolean isHasError() {
        return hasError;
    }

    public void setHasError(boolean hasError) {
        this.hasError = hasError;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "Response{" +
                "hasError=" + hasError +
                ", code=" + code +
                ", message='" + message + '\'' +
                ", data=" + data +
                '}';
    }
}
