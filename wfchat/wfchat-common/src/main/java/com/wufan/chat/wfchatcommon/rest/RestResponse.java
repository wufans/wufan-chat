package com.wufan.chat.wfchatcommon.rest;

/**
 * @Author: wufan
 * @Date: 2019/7/4 16:31
 *
 */

import java.io.Serializable;

/**
 * 针对HTTP的 restful response
 * @param <T>
 */
public class RestResponse<T> implements Serializable{
    public static final String SUCCESS_MSG = "OK";
    public static final String FAILURE_MSG = "wrong";
    public static final Integer SUCCESS_CODE = 200;
    public static final Integer FAILURE_CODE = 400;

    private Integer code = SUCCESS_CODE;

    private String msg = SUCCESS_MSG;
    private T data;

    public RestResponse() {
    }

    public RestResponse(Integer code, String msg, T data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    public RestResponse(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
        data = null;
    }

    public static RestResponse buildFailureResp(Exception e) {
        return new RestResponse(RestResponse.FAILURE_CODE, e.getMessage());
    }

    public static RestResponse buildFailureResp() {
        return new RestResponse(RestResponse.FAILURE_CODE, RestResponse.FAILURE_MSG);
    }

    public static RestResponse buildSuccessResp(Object data) {
        return new RestResponse(RestResponse.SUCCESS_CODE, RestResponse.SUCCESS_MSG, data);
    }

    public static RestResponse buildSuccessResp() {
        return new RestResponse(RestResponse.SUCCESS_CODE, RestResponse.SUCCESS_MSG);
    }

    public Integer getCode() {
        return code;
    }

    public RestResponse<T> setCode(Integer code) {
        this.code = code;
        return this;
    }

    public String getMsg() {
        return msg;
    }

    public RestResponse<T> setMsg(String msg) {
        this.msg = msg;
        return this;
    }

    public T getData() {
        return data;
    }

    public RestResponse<T> setData(T data) {
        this.data = data;
        return this;
    }

}
