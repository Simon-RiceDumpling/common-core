package com.aspect;


import com.annotation.RedisLock;
import com.exception.AssemblyCatServiceException;
import com.utils.MD5Util;
import com.utils.RedissonUtils;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

/**
 * 项目名称: pay_core
 *
 * @ClassName RedisLockAspect
 * @Description
 * @Author renBo
 * @Date 2023年01月11日15:08:58
 */
@Aspect
//@Component
@Slf4j
public class RedisLockAspect_old {
    private static final String REDIS_LOCK = "cat:redisson:lock:";

    /**
     * 切入点为所有的@RedisLock 注解方法
     */
    @Pointcut("@annotation(com.annotation.RedisLock)")
    private void serviceAspect() {
    }

    @SneakyThrows
    @Around("serviceAspect()")
    public Object around(ProceedingJoinPoint point) {
        Method method = ((MethodSignature) point.getSignature()).getMethod();
        RedisLock redisLock = method.getAnnotation(RedisLock.class);
        String redisLockKey = REDIS_LOCK.concat(method.getName())
                .concat(":")
                .concat(redisLock.key().contains("#") ? getParamsKey(point, redisLock.key().replace("#", "").split("\\.")) : redisLock.key());
        //尝试获取锁资源
        boolean lock = RedissonUtils.getTryLock(redisLockKey, TimeUnit.SECONDS, redisLock.waitTime(), redisLock.leaseTime());
        try {
            if (lock) {
                //获取到锁资源 执行业务逻辑
                return point.proceed();
            }
            //获取分布式锁失败 抛出异常
            throw new AssemblyCatServiceException("分布式锁获取失败");
        } finally {
            //释放锁
            if (lock) {
                RedissonUtils.unlock(redisLockKey);
            }
        }
    }

    /**
     * a.b.c.d
     */
    public String getParamsKey(ProceedingJoinPoint point, String[] regexParams) {
        //获取到方法参数名称
        //查询参数名称是否有匹配的参数 没有返回null
        Object obj = Arrays.stream(point.getArgs())
                .filter(arg -> arg.getClass().getSimpleName().equalsIgnoreCase(regexParams[0]))
                .findFirst()
                .orElseThrow(() -> new AssemblyCatServiceException("没有匹配的参数值,请检查RedisLock key()"));
        //获取改参数的所有属性 查找下一个符合条件的参数
        //获取参数值 返回
        return getParamsKey(obj, obj.getClass().getDeclaredFields(), regexParams, 1);
    }

    @SneakyThrows
    public String getParamsKey(Object obj, Field[] fields, String[] regexParams, int i) {
        if (i > regexParams.length) {
            throw new AssemblyCatServiceException("没有匹配的参数值,请检查RedisLock key()");
        }
        //获取游标为i的参数名称
        String paramName = regexParams[i];
        Field field = Arrays.stream(fields)
                .filter(f -> f.getName().equals(paramName))
                .findFirst()
                .orElseThrow(() -> new AssemblyCatServiceException("没有匹配的参数值,请检查RedisLock key()"));
        field.setAccessible(true);
        return i == regexParams.length - 1 ? MD5Util.MD5Str(String.valueOf(field.get(obj))) : getParamsKey(field.get(obj), field.getType().getDeclaredFields(), regexParams, ++i);
    }
}