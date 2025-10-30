package com.enums;

/**
 * @author renbo
 */

public enum SensitiveType {
    TYPE1(1, "value1"),
    TYPE2(2, "value22222");

    private final Integer code;
    private final String value;

    SensitiveType(Integer code, String value) {
        this.code = code;
        this.value = value;
    }

    public Integer getCode() {
        return code;
    }

    public String getValue() {
        return value;
    }
}
