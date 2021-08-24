package com.xxx.common.redis.lock;


import com.xxx.common.api.lock.DistributedLock;
import com.xxx.common.api.lock.Lock;
import com.xxx.common.redis.exception.RedissonLockException;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;

import java.util.concurrent.TimeUnit;

/**
 * 描述：AuthCenterApplication
 *高级的锁功能，请自行扩展或直接使用原生api
 * https://gitbook.cn/gitchat/activity/5f02746f34b17609e14c7d5a
 *
 * @author 归墟
 * @email huanghe@shzx.com
 * @date 2021/8/14 11:31
 * @company 数海掌讯
 */

@ConditionalOnClass(RedissonClient.class)
@ConditionalOnProperty(prefix = "redis.lock", name = "lockerType", havingValue = "REDIS", matchIfMissing = true)
public class RedissonDistributedLock implements DistributedLock {
    @Autowired
    private RedissonClient redissonClient;

   private static final String LOCK_KEY_PREFIX = "redisson_lock_key:";

    private Lock getLock(String key, boolean isFair) {
        RLock lock;
        if (isFair) {
            lock = redissonClient.getFairLock(LOCK_KEY_PREFIX + key);
        } else {
            lock = redissonClient.getLock(LOCK_KEY_PREFIX + key);
        }
        return new Lock(lock, this);
    }

    @Override
    public Lock lock(String key, long leaseTime, TimeUnit unit, boolean isFair) {
        Lock zLock = getLock(key, isFair);
        RLock lock = (RLock) zLock.getLockObject();
        lock.lock(leaseTime, unit);
        return zLock;
    }

    @Override
    public Lock tryLock(String key, long waitTime, long leaseTime, TimeUnit unit, boolean isFair) throws InterruptedException {
        Lock zLock = getLock(key, isFair);
        RLock lock = (RLock) zLock.getLockObject();
        if (lock.tryLock(waitTime, leaseTime, unit)) {
            return zLock;
        }
        return null;
    }

    @Override
    public void unlock(Object lock) {
        if (lock != null) {
            if (lock instanceof RLock) {
                RLock rLock = (RLock) lock;
                if (rLock.isLocked()) {
                    rLock.unlock();
                }
            } else {
                throw new RedissonLockException("requires RLock type");
            }
        }
    }
}
