package com.exception;

import cn.hutool.http.HttpStatus;

import com.enums.ErrorEnum;
import lombok.Getter;

import java.io.Serializable;

/**
 * @Author: renBo.ren
 * @Description:
 * @Data: Created in 17:23 2022/8/1
 */
@Getter
public class AssemblyCatServiceException extends RuntimeException implements Serializable {
    private String code;
    private String msg;

    public AssemblyCatServiceException() {
        this.code = ErrorEnum.IS00001.getErrorCode();
        this.msg = ErrorEnum.IS00001.getValue();
    }

    public AssemblyCatServiceException(Throwable e) {
        this.code = ErrorEnum.IS00001.getErrorCode();
        this.msg = e.getMessage();
    }

    public AssemblyCatServiceException(String code, String msg) {
        this.code = code;
        this.msg = msg;
    }
    public AssemblyCatServiceException(String msg) {
        this.code = String.valueOf(HttpStatus.HTTP_INTERNAL_ERROR);
        this.msg = msg;
    }
    public AssemblyCatServiceException(ErrorEnum errorEnum) {
        this.code = errorEnum.getErrorCode();
        this.msg = errorEnum.getValue();
    }
}
