/*
 *
 *      Copyright (c) 2018-2025, xiaomuding All rights reserved.
 *
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions are met:
 *
 * Redistributions of source code must retain the above copyright notice,
 *  this list of conditions and the following disclaimer.
 *  Redistributions in binary form must reproduce the above copyright
 *  notice, this list of conditions and the following disclaimer in the
 *  documentation and/or other materials provided with the distribution.
 *  Neither the name of the pig4cloud.com developer nor the names of its
 *  contributors may be used to endorse or promote products derived from
 *  this software without specific prior written permission.
 *  Author: xiaomuding
 *
 */

package com.bo;


import lombok.*;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * 响应信息主体
 *
 * @param <T>
 * @author xiaomuding
 */
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)

public class R<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    @Getter
    @Setter

    private int code;

    @Getter
    @Setter

    private String msg;

    @Getter
    @Setter

    private T data;
    @Getter
    @Setter
    private boolean encryption =false;

    public static <T> R<T> ok() {
        return restResult(null, 200, null);
    }

    public static <T> R<T> ok(T data, Integer successCode, String msg) {
        return restResult(data, successCode, msg);
    }

    public static <T> R<T> ok(T data) {
        return restResult(data, 200, "");
    }
    public static <T> R<T> ok(T data,String msg) {
        return restResult(data, 200, msg);
    }
    public static <T> R<T> success(String msg) {
        return restResult(null,200, msg);
    }
    public static <T> R<T> success() {
        return restResult(null, 200, "");
    }
    public static <T> R<T> success(T data) {
        return restResult(data, 200, "");
    }

    public static <T> R<T> failed() {
        return restResult(null, 501, null);
    }

    public static <T> R<T> failed(String msg) {
        return restResult(null, 501, msg);
    }

    public static <T> R<T> failed(T data) {
        return restResult(data, 501, null);
    }

    public static <T> R<T> failed(T data, String msg) {
        return restResult(data, 501, msg);
    }

    public static <T> R<T> failed(T data, String msg, Integer code) {
        return restResult(data, code, msg);
    }


    static <T> R<T> restResult(T data, int code, String msg) {
        R<T> apiResult = new R<>();
        apiResult.setCode(code);
        apiResult.setData(data);
        apiResult.setMsg(msg);
        return apiResult;
    }

}
