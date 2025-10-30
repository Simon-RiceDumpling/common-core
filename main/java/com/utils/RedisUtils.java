package com.utils;

import cn.hutool.extra.spring.SpringUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.*;

import javax.annotation.Resource;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;


/**
 * @author renBo
 * @ClassName: RedissonUtils
 * @Description: redis操作类
 * @date 2023-04-21 10:30
 */
@Slf4j
public class RedisUtils {


    static StringRedisTemplate redisTemplate;

    static {
        try {
            redisTemplate = SpringUtil.getBean(StringRedisTemplate.class);
        } catch (Exception e) {

        }
    }

    /**
     * @return boolean
     * @Author renBo
     * @Description 写入缓存
     * @Date 9:01 2023-04-21
     * @Param [key, value]
     */
    public static boolean set(final String key, Object value) {
        boolean result = false;
        try {
            ValueOperations ops = redisTemplate.opsForValue();
            ops.set(key, value.toString());
            result = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public static boolean set(final String key, Object value, Long expTime, TimeUnit timeUnit) {
        return setExp(key, value, expTime, timeUnit);
    }


    /**
     * @return boolean
     * @Author renBo
     * @Description 写入缓存 带过期时间
     * @Date 9:01 2023-04-21
     * @Param [key, value]
     */
    public static boolean setExp(final String key, Object value, Long expTime, TimeUnit timeUnit) {
        boolean result = false;
        try {
            ValueOperations ops = redisTemplate.opsForValue();
            ops.set(key, value.toString());
            redisTemplate.expire(key, expTime, timeUnit);
            result = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }


    /**
     * @return boolean
     * @Author renBo
     * @Description 判断redis中是否存在对应key
     * @Date 9:01 2023-04-21
     * @Param [key, value]
     */
    public static boolean isExist(final String key) {
        return redisTemplate.hasKey(key);
    }


    /**
     * @return boolean
     * @Author renBo
     * @Description 删除对应的key
     * @Date 9:01 2023-04-21
     * @Param [key, value]
     */
    public static boolean delete(final String key) {
        if (isExist(key)) {
            return redisTemplate.delete(key);
        }
        return false;
    }


    /**
     * @return boolean
     * @Author renBo
     * @Description 读取对应key的值
     * @Date 9:01 2023-04-21
     * @Param [key, value]
     */
    public static Object get(final String key) {
        ValueOperations ops = redisTemplate.opsForValue();
        return ops.get(key);
    }


    public static List<String> keys(String pattern) {
        List<String> keys = new ArrayList();
        scan(pattern, (item) -> {
            String key = new String(item, StandardCharsets.UTF_8);
            keys.add(key);
        });
        return keys;
    }


    public static void scan(String pattern, Consumer<byte[]> consumer) {
        redisTemplate.execute((RedisCallback<Object>) (connection) -> {
            try {
                Cursor<byte[]> cursor = connection.scan(ScanOptions.scanOptions().count(9223372036854775807L).match(pattern).build());
                Throwable var4 = null;
                Object var5;
                try {
                    cursor.forEachRemaining(consumer);
                    var5 = null;
                } finally {
                    if (cursor != null) {
                        if (var4 != null) {
                            try {
                                cursor.close();
                            } catch (Throwable var14) {
                                var4.addSuppressed(var14);
                            }
                        } else {
                            cursor.close();
                        }
                    }
                }
                return var5;
            } catch (Exception var17) {
                log.error("scan:" + var17.getMessage(), var17);
                throw new RuntimeException(var17);
            }
        });
    }

    public static long incr(String key, long delta) {
        if (delta < 0L) {
            throw new RuntimeException("递增因子必须大于0");
        } else {
            return redisTemplate.opsForValue().increment(key, delta);
        }
    }

    public static long incr(String key) {
        return incr(key, 1);
    }

    /**
     * 普通缓存放入并设置时间
     *
     * @param key   键
     * @param value 值
     * @param time  时间(秒) time要大于0 如果time小于等于0 将设置无限期
     * @return true成功 false 失败
     */
    public static boolean set(String key, String value, long time) {
        try {
            if (time > 0) {
                redisTemplate.opsForValue().set(key, value, time, TimeUnit.SECONDS);
            } else {
                set(key, value);
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }


    public static boolean setIfAbsent(String key, String value, long time) {
        return redisTemplate.opsForValue().setIfAbsent(key, value, time, TimeUnit.SECONDS);
    }
}
