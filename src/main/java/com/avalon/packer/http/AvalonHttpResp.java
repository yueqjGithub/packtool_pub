package com.avalon.packer.http;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.io.Serializable;

/**
 * 通用的全局返回参数
 * @author wangxb
 **/
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
public class AvalonHttpResp<T> implements Serializable {
    protected static final long serialVersionUID = 1L;


    private int status;

    private String message;


    private T data;

    public AvalonHttpResp() {
    }

    public AvalonHttpResp(AvalonError errorEnum){
        this.status = errorEnum.getStatus();
        this.message  = errorEnum.getDesc();
    }

    public AvalonHttpResp(int status, String msg, T data) {
        this.status = status;
        this.message = msg;
        this.data = data;
    }

    public static <T> AvalonHttpResp<T> ok() {
        return restResult(null, AvalonError.OK);
    }

    public static <T> AvalonHttpResp<T> ok(T data) {
        return restResult(data, AvalonError.OK);
    }

    private static <T> AvalonHttpResp<T> restResult(T data, AvalonError errorEnum) {
        return new AvalonHttpResp<>(errorEnum.getStatus(), errorEnum.getDesc(), data);
    }

    private static <T> AvalonHttpResp<T> restResult(T data, AvalonError errorEnum, String msg) {
        return new AvalonHttpResp<>(errorEnum.getStatus(), msg, data);
    }

    public static <T> AvalonHttpResp<T> failed(String msg) {
        return restResult(null, AvalonError.UNKNOWN_ERROR, msg);
    }

    public static <T> AvalonHttpResp<T> failed(Throwable msg) {
        return restResult(null, AvalonError.UNKNOWN_ERROR, msg.getMessage());
    }


    public static <T> AvalonHttpResp<T> failed(AvalonError errorEnum, String msg) {
        return restResult(null, errorEnum, msg);
    }



    public int getStatus() {
        return status;
    }

    public AvalonHttpResp<T> setStatus(int status) {
        this.status = status;
        return this;
    }

    public String getMessage() {
        return message;
    }

    public AvalonHttpResp<T> setMessage(String message) {
        this.message = message;
        return this;
    }

    public T getData() {
        return data;
    }

    public AvalonHttpResp<T> setData(T data) {
        this.data = data;
        return this;
    }
}
