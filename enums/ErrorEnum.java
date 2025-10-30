package com.enums;

import lombok.Getter;

import java.util.Arrays;

/**
 * @Author: renBo.ren
 * @Description:
 * @Data: Created in 17:28 2022/8/1
 */
@Getter
public enum ErrorEnum {
    IS00001("IS00001", "系统异常,请稍后重试"),
    IS00002("IS00002", "查询异常"),
    IS00003("IS00003", "更新异常"),
    IS00004("IS00004", "插入异常"),
    IS00005("IS00005", "删除异常"),
    IS00006("IS00006", "参数异常，请检查参数！"),
    IS00007("IS00007", "根据广告主回传数据查询数据为空!");

    private String errorCode;
    private String value;

    ErrorEnum(String errorCode, String value) {
        this.errorCode = errorCode;
        this.value = value;
    }

    public static String getNameByErrorCode(String errorCode) {
        return Arrays.stream(ErrorEnum.values())
                .filter(x -> x.getErrorCode().equals(errorCode))
                .map(ErrorEnum::getValue)
                .findFirst()
                .orElse("");

    }

    public static ErrorEnum getEnumByErrorCode(String errorCode) {
        return Arrays.stream(ErrorEnum.values())
                .filter(x -> x.getErrorCode().equals(errorCode))
                .findFirst()
                .orElse(null);
    }
}
