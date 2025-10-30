package com.utils;

import cn.hutool.core.date.DateUtil;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

/**
 * @Author RenBo
 * @Date 2024-04-08 17:32
 * @PackageName:com.xmd.xiaomuding.common.core.util
 * @ClassName: XmdDataUtils
 * @Description: TODO
 * @Version 1.0
 */
@Slf4j
public class SimonDateUtils {

    public static Date parseToData(String text) {
        return DateUtil.parseDate(text);
    }

    public static LocalDate parseToLocalDate(String text) {
        return parseToData(text).toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }

}
