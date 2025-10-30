package com.utils;


import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;
import java.util.UUID;

/**
 * @Author: renBo.ren
 * @Description:
 * @Data: Created in 10:30 2022/8/4
 */
public class UUIDUtils {
    public static final String ALL_CHAR = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";

    /**
     * UUID随机生成方法
     */
    public static String getUUID() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    public static String randomID() {
        return getUUID() + createRandomStr(3);
    }

    /**
     *  随机生成length位字符串
     */
    public static String createRandomStr(int length) {
        Random random = new Random();
        StringBuffer stringBuffer = new StringBuffer();
        for (int i = 0; i < length; i++) {
            int number = random.nextInt(62);
            stringBuffer.append(ALL_CHAR.charAt(number));
        }
        return stringBuffer.toString();
    }


    /**
     *  跳转广告主生成唯一订单号
     */
    public static String createClickId() {
       return "TK"+createRandomStr(10);
    }


    public static String createDataSer(){
        SimpleDateFormat sd=new SimpleDateFormat("YYYYMMddhhmmss");
        String format = sd.format(new Date());
        return format;
    }

}
