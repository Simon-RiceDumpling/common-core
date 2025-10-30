package com.utils;

import cn.hutool.core.util.ObjectUtil;
import lombok.Data;
import lombok.SneakyThrows;

import java.util.Arrays;
import java.util.regex.Pattern;

/**
 * @ClassNAME ChineseUtils
 * @Description TODO
 * @Author renbo.ren@cyberklick.com
 * @Date 2023/2/15 10:57
 */

public class ChineseUtils {

    /**
     * 判断字符串中是否包含中文
     *
     * @param str 待校验字符串
     * @return 是否为中文
     * @warn 不能校验是否为中文标点符号
     */
    public static boolean isContainChinese(String str) {
        return Pattern.compile("[\u4e00-\u9fa5]").matcher(str).find();
    }

    public static void main(String[] args) {
        Person person = new Person();
        person.setAge(23);
        person.setName("zhangsan");
        person.setAddress("陕西省西安市中||");
        System.out.println(person);
        checkObjectFiledContainChineseAndEncoding(person);
        System.out.println(person);
    }


    @SneakyThrows
    public static void checkObjectFiledContainChineseAndEncoding(Object obj) {
        //STEP1 所有的中文全部 encode
        Arrays.stream(obj.getClass().getDeclaredFields())
                .forEach(x -> {
                    try {
                        x.setAccessible(true);
                        Object o = x.get(obj);
                        if (ObjectUtil.isNotEmpty(o) && o instanceof String && isContainChinese(o.toString())) {
                            x.set(obj, UrlUtils.encode(o.toString()));
                        }
                    } catch (Exception e) {
                        System.out.println("校验错误");
                    }
                });
    }

    @Data
    static class Person {
        private String name;
        private Integer age;
        private String address;
    }
}
