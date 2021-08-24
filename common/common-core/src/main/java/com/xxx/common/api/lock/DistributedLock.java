package com.xxx.common.api.lock;

import java.util.concurrent.TimeUnit;

/**
 * 描述：DistributedLock
 *
 * @author 归墟
 * @email huanghe@shzx.com
 * @date 2021/8/14 15:08
 * @company 数海掌讯
 */
public interface DistributedLock {
    /**
     * 获取锁，如果获取不成功则一直等待直到lock被获取
     *
     * @param key 锁的key
     * @param leaseTime 加锁的时间，超过这个时间后锁便自动解锁；
     *                  如果leaseTime为-1，则保持锁定直到显式解锁
     * @param unit {@code leaseTime} 参数的时间单位
     * @param isFair 是否公平锁
     * @throws Exception 异常
     * @return 锁对象
     */
    Lock lock(String key, long leaseTime, TimeUnit unit, boolean isFair) throws Exception;

    default Lock lock(String key, long leaseTime, TimeUnit unit) throws Exception {
        return this.lock(key, leaseTime, unit, false);
    }

    default Lock lock(String key, boolean isFair) throws Exception {
        return this.lock(key, -1, null, isFair);
    }

    default Lock lock(String key) throws Exception {
        return this.lock(key, -1, null, false);
    }

    /**
     * 尝试获取锁，如果锁不可用则等待最多waitTime时间后放弃
     * @param key 锁的key
     * @param waitTime 获取锁的最大尝试时间(单位 {@code unit})
     * @param leaseTime 加锁的时间，超过这个时间后锁便自动解锁；
     *                  如果leaseTime为-1，则保持锁定直到显式解锁
     * @param unit {@code waitTime} 和 {@code leaseTime} 参数的时间单位
     * @return 锁对象，如果获取锁失败则为null
     */
    Lock tryLock(String key, long waitTime, long leaseTime, TimeUnit unit, boolean isFair) throws Exception;

    default Lock tryLock(String key, long waitTime, long leaseTime, TimeUnit unit) throws Exception {
        return this.tryLock(key, waitTime, leaseTime, unit, false);
    }

    default Lock tryLock(String key, long waitTime, TimeUnit unit, boolean isFair) throws Exception {
        return this.tryLock(key, waitTime, -1, unit, isFair);
    }

    default Lock tryLock(String key, long waitTime, TimeUnit unit) throws Exception {
        return this.tryLock(key, waitTime, -1, unit, false);
    }

    /**
     * 释放锁
     * @param lock 锁对象
     * @throws Exception 异常
     */
    void unlock(Object lock) throws Exception;

    /**
     * 释放锁
     * @param zLock 锁抽象对象
     * @throws Exception 异常
     */
    default void unlock(Lock zLock) throws Exception {
        if (zLock != null) {
            this.unlock(zLock.getLockObject());
        }
    }
}
