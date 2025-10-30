package com.utils;

import cn.hutool.extra.spring.SpringUtil;
import org.redisson.Redisson;
import org.redisson.api.RLock;

import java.util.concurrent.TimeUnit;

/**
 * @ClassName RedissonUtils
 * @Description
 * @Author renBo renbo@chinasofti.com
 * @Date 2022/5/6 14:24
 */

public class RedissonUtils {

    private static Redisson redisson = SpringUtil.getBean(Redisson.class);

    public static RLock getTimeOutLock(String lockKey, long timeout) {
        RLock rLock = redisson.getLock(lockKey);
        rLock.lock(timeout, TimeUnit.SECONDS);
        return rLock;
    }

    public static RLock getLock(String lockKey) {
        RLock rLock = redisson.getLock(lockKey);
        rLock.lock();
        return rLock;
    }

    public static RLock lock(String lockKey, TimeUnit unit, long timeout) {
        RLock lock = redisson.getLock(lockKey);
        lock.lock(timeout, unit);
        return lock;
    }

    public static boolean getTryLock(String lockKey, TimeUnit unit, long waitTime, long leaseTime) {
        RLock lock = redisson.getLock(lockKey);
        try {
            return lock.tryLock(waitTime, leaseTime, unit);
        } catch (InterruptedException var9) {
            return false;
        }
    }

    public static boolean getTryLockByKey(String lockKey) {
        RLock lock = redisson.getLock(lockKey);
        try {
            return lock.tryLock(10L, 5L, TimeUnit.SECONDS);
        } catch (InterruptedException var4) {
            return false;
        }
    }

    public static boolean getTryLockTimeOut(String lockKey, long waitTime) {
        RLock lock = redisson.getLock(lockKey);
        try {
            return lock.tryLock(waitTime, TimeUnit.SECONDS);
        } catch (InterruptedException var6) {
            return false;
        }
    }

    public static RLock getFairLock(String lockKey) {
        RLock lock = redisson.getFairLock(lockKey);
        return lock;
    }

    public static RLock getFairLockTimeOut(String lockKey, long waitTime) {
        RLock lock = redisson.getFairLock(lockKey);
        lock.lock(waitTime, TimeUnit.SECONDS);
        return lock;
    }

    public static boolean getFairLockTryLock(String lockKey, TimeUnit unit, Long timout, Long leaseTime) {
        RLock lock = redisson.getFairLock(lockKey);
        try {
            return lock.tryLock(timout, leaseTime, unit);
        } catch (InterruptedException var7) {
            return false;
        }
    }

    public static void unlock(String lockKey) {
        RLock lock = redisson.getLock(lockKey);
        if (lock.isLocked() && lock.isHeldByCurrentThread()) {
            lock.unlock();
        }
    }

    public static void unlock(RLock lock) {
        if (lock.isLocked() && lock.isHeldByCurrentThread()) {
            lock.unlock();
        }
    }
}
