package com.enums;


import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * @Author RenBo
 * @Description //产品枚举 用于权限使用
 * @Date 9:28 2023-09-20
 * @return
 * @Param
 */
@NoArgsConstructor
@Getter
public enum ProjectEnum {
    /**
     * 空占位符
     */
    EMPTY("empty", ""),
    /**
     * 八福
     */
    BA_FU("baFu", "八福"),
    /**
     * 畜产品
     */
    XCP("xuchanpin", "畜产品");

    /**
     * 编码
     */
    private String code;

    /**
     * 信息
     */
    private String desc;


    ProjectEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static ProjectEnum getEnum(String value) {
        ProjectEnum[] supplierEnums = values();
        for (ProjectEnum supplierEnum : supplierEnums) {
            if ((supplierEnum.code).equals(value)) {
                return supplierEnum;
            }
        }
        return ProjectEnum.EMPTY;
    }
}