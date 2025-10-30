package com.enums;

import lombok.Getter;

import java.util.Arrays;

/**
 * @Author: renBo.ren renbo.ren@cyberklick.com.cn
 * @Description:
 * @Data: Created in 13:10 2022/8/25
 */
@Getter
public enum FactoryObjectEnum {
    O_1("O_1", "对象1"),
    O_2("O_2", "对象2"),
    O_3("O_3", "对象3");

    private String status;
    private String value;

    FactoryObjectEnum(String status, String value) {
        this.status = status;
        this.value = value;
    }

    /**
     * 根据状态查询枚举
     */
    public static FactoryObjectEnum getEnumByStatus(String status) {
        return Arrays.stream(FactoryObjectEnum.values())
                .filter(x -> x.getStatus().equalsIgnoreCase(status))
                .findFirst()
                .orElse(null);
    }
}
