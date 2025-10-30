package com.utils;

import lombok.extern.slf4j.Slf4j;

import java.math.BigInteger;
import java.security.MessageDigest;

/**
 * @Author: renBo.ren renbo.ren@cyberklick.com.cn
 * @Description:
 * @Data: Created in 17:55 2022/8/24
 */
@Slf4j
public class MD5Util {

        public static String MD5Str(String str) {
            byte[] digest;
            try {
                MessageDigest md5 = MessageDigest.getInstance("md5");
                digest = md5.digest(str.getBytes("utf-8"));
            } catch (Exception e) {
                e.printStackTrace();
                log.error("MD5加密出错");
                return str;
            }
            //16是表示转换为16进制数
            return new BigInteger(1, digest).toString(16);
        }
}
