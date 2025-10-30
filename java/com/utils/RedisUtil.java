package com.utils;

import cn.hutool.extra.spring.SpringUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.RedisClusterConnection;
import org.springframework.data.redis.connection.RedisClusterNode;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.data.redis.core.ScanOptions.ScanOptionsBuilder;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.support.atomic.RedisAtomicLong;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

@Slf4j
//@Service
public final class RedisUtil {
   // @Resource
   // private  RedisTemplate redisTemplate;
    private static StringRedisTemplate redisTemplate = SpringUtil.getBean(StringRedisTemplate.class);
    private static RedisConnectionFactory connectionFactory = SpringUtil.getBean(RedisConnectionFactory.class);


    public static boolean expire(String key, long time) {
        return expire(key, time, TimeUnit.SECONDS);
    }

    public static boolean expire(String key, long time, TimeUnit timeUnit) {
        try {
            if (time > 0L) {
                redisTemplate.expire(key, time, timeUnit);
            }
            return true;
        } catch (Exception var6) {
            log.error("expire fail:" + var6.getMessage(), var6);
            return false;
        }
    }

    public static long getExpire(String key) {
        return redisTemplate.getExpire(key, TimeUnit.SECONDS);
    }

    public static boolean hasKey(String key) {
        try {
            return redisTemplate.hasKey(key);
        } catch (Exception var3) {
            log.error("hasKey fail:" + var3.getMessage(), var3);
            return false;
        }
    }

    public static void del(String... key) {
        if (key != null && key.length > 0) {
            if (key.length == 1) {
                redisTemplate.delete(key[0]);
            } else {
                redisTemplate.delete((Collection<String>) CollectionUtils.arrayToList(key));
            }
        }

    }

    public static Object get(String key) {
        return key == null ? null : redisTemplate.opsForValue().get(key);
    }

    public static boolean set(String key, Object value) {
        try {
            redisTemplate.opsForValue().set(key, value.toString());
            return true;
        } catch (Exception var4) {
            log.error("set fail:" + var4.getMessage(), var4);
            return false;
        }
    }

    public static boolean set(String key, Object value, long time) {
        return set(key, value, time, TimeUnit.SECONDS);

    }

    public static boolean set(String key, Object value, long time, TimeUnit timeUnit) {
        try {
            if (time > 0L) {
                redisTemplate.opsForValue().set(key, value.toString(), time, timeUnit);
            } else {
                set(key, value);
            }
            return true;
        } catch (Exception var7) {
            log.error("set fail:" + var7.getMessage(), var7);
            return false;
        }
    }

    public static long incr(String key, long delta) {
        if (delta < 0L) {
            throw new RuntimeException("递增因子必须大于0");
        } else {
            return redisTemplate.opsForValue().increment(key, delta);
        }
    }

    public static Long incrExpire(String key, long liveTime, TimeUnit timeUnit) {
        RedisAtomicLong entityIdCounter = new RedisAtomicLong(key, redisTemplate.getConnectionFactory());
        Long increment = entityIdCounter.getAndIncrement();
        if ((null == increment || increment == 0L) && liveTime > 0L) {
            entityIdCounter.expire(liveTime, timeUnit);
        }
        return increment;
    }

    public static long decr(String key, long delta) {
        if (delta < 0L) {
            throw new RuntimeException("递减因子必须大于0");
        } else {
            return redisTemplate.opsForValue().increment(key, -delta);
        }
    }

    public static Object hget(String key, String item) {
        return redisTemplate.opsForHash().get(key, item);
    }

    public static Map<Object, Object> hmget(String key) {
        return redisTemplate.opsForHash().entries(key);
    }

    public static boolean hmset(String key, Map<String, Object> map) {
        try {
            redisTemplate.opsForHash().putAll(key, map);
            return true;
        } catch (Exception var4) {
            log.error("set hmset:" + var4.getMessage(), var4);
            return false;
        }
    }

    public static boolean hmset(String key, Map<String, Object> map, long time) {
        try {
            redisTemplate.opsForHash().putAll(key, map);
            if (time > 0L) {
                expire(key, time);
            }
            return true;
        } catch (Exception var6) {
            log.error("set hmset:" + var6.getMessage(), var6);
            return false;
        }
    }

    public static boolean hSet(String key, String item, Object value) {
        try {
            redisTemplate.opsForHash().put(key, item, value);
            return true;
        } catch (Exception var5) {
            log.error("set hmset:" + var5.getMessage(), var5);
            return false;
        }
    }

    public static boolean hSet(String key, String item, Object value, long time) {
        try {
            redisTemplate.opsForHash().put(key, item, value);
            if (time > 0L) {
                expire(key, time);
            }
            return true;
        } catch (Exception var7) {
            log.error("set hmset:" + var7.getMessage(), var7);
            return false;
        }
    }

    public static boolean hSet(String key, String item, Object value, long time, TimeUnit timeUnit) {
        try {
            redisTemplate.opsForHash().put(key, item, value);
            if (time > 0L) {
                expire(key, time, timeUnit);
            }
            return true;
        } catch (Exception var8) {
            log.error("set hmset:" + var8.getMessage(), var8);
            return false;
        }
    }

    public static void hDel(String key, Object... item) {
        redisTemplate.opsForHash().delete(key, item);
    }

    public static boolean hHasKey(String key, String item) {
        return redisTemplate.opsForHash().hasKey(key, item);
    }

    public static double hIncr(String key, String item, double by) {
        return redisTemplate.opsForHash().increment(key, item, by);
    }

    public static double hDecr(String key, String item, double by) {
        return redisTemplate.opsForHash().increment(key, item, -by);
    }


    public static boolean sHasKey(String key, Object value) {
        try {
            return redisTemplate.opsForSet().isMember(key, value);
        } catch (Exception var4) {
            log.error("set sHasKey:" + var4.getMessage(), var4);
            return false;
        }
    }





    public static long sGetSetSize(String key) {
        try {
            return redisTemplate.opsForSet().size(key);
        } catch (Exception var3) {
            log.error("set sGetSetSize:" + var3.getMessage(), var3);
            return 0L;
        }
    }

    public static long setRemove(String key, Object... values) {
        try {
            return redisTemplate.opsForSet().remove(key, values);
        } catch (Exception var4) {
            log.error("set setRemove:" + var4.getMessage(), var4);
            return 0L;
        }
    }


    public static long lGetListSize(String key) {
        try {
            return redisTemplate.opsForList().size(key);
        } catch (Exception var3) {
            log.error("set lGetListSize:" + var3.getMessage(), var3);
            return 0L;
        }
    }

    public static Object lGetIndex(String key, long index) {
        try {
            return redisTemplate.opsForList().index(key, index);
        } catch (Exception var5) {
            log.error("set lGetIndex:" + var5.getMessage(), var5);
            return null;
        }
    }

    public static boolean lSet(String key, Object value) {
        try {
            redisTemplate.opsForList().rightPush(key, value.toString());
            return true;
        } catch (Exception var4) {
            log.error("set lSet:" + var4.getMessage(), var4);
            return false;
        }
    }

    public static boolean lSet(String key, Object value, long time) {
        try {
            redisTemplate.opsForList().rightPush(key, value.toString());
            if (time > 0L) {
                expire(key, time);
            }
            return true;
        } catch (Exception var6) {
            log.error("set lSet:" + var6.getMessage(), var6);
            return false;
        }
    }

    public static boolean lSet(String key, List<Object> value) {
        try {
            redisTemplate.opsForList().rightPushAll(key, value.toString());
            return true;
        } catch (Exception var4) {
            log.error("set lSet:" + var4.getMessage(), var4);
            return false;
        }
    }

    public static boolean lSet(String key, List<Object> value, long time) {
        try {
            redisTemplate.opsForList().rightPushAll(key, value.toString());
            if (time > 0L) {
                expire(key, time);
            }
            return true;
        } catch (Exception var6) {
            log.error("set lSet:" + var6.getMessage(), var6);
            return false;
        }
    }

    public static boolean lUpdateIndex(String key, long index, Object value) {
        try {
            redisTemplate.opsForList().set(key, index, value.toString());
            return true;
        } catch (Exception var6) {
            log.error("set lUpdateIndex:" + var6.getMessage(), var6);
            return false;
        }
    }

    public static long lRemove(String key, long count, Object value) {
        try {
            return redisTemplate.opsForList().remove(key, count, value);
        } catch (Exception var6) {
            log.error("set lRemove:" + var6.getMessage(), var6);
            return 0L;
        }
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
                }finally {
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

    public static void delFuzzyKeys(String keys) {
        RedisClusterConnection connection = connectionFactory.getClusterConnection();
        try {
            log.debug("需要查询删除的keys:【{}】", keys);
            ScanOptionsBuilder scanOptionsBuilder = ScanOptions.scanOptions();
            scanOptionsBuilder.match(keys);
            ScanOptions scanOptions = scanOptionsBuilder.build();
            List<RedisClusterNode> redisClusterNodes = (List) connection.clusterGetNodes();
            redisClusterNodes.forEach((redisClusterNode) -> {
                if (redisClusterNode.isMaster()) {
                    log.debug("Master redisClusterNodes:【{}】", redisClusterNode.toString());
                    Cursor cursor = connection.scan(redisClusterNode, scanOptions);
                    while (cursor.hasNext()) {
                        log.debug("要删除的key:【{}】");
                        connection.del(new byte[][]{(byte[]) cursor.next()});
                    }
                }

            });
        } catch (Exception var6) {
            log.error("模糊查询删除错误：【{}】", var6.getMessage());
        }
    }


    public static Long leftPush(String key, String value) {
        return redisTemplate.opsForList().leftPush(key, value);
    }

    public static Object leftPush(String key) {
        return redisTemplate.opsForList().rightPop(key);
    }
}
