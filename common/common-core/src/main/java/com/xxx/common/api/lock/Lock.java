package com.xxx.common.api.lock;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 描述：ZLock
 *
 * @author 归墟
 * @email huanghe@shzx.com
 * @date 2021/8/14 15:09
 * @company 数海掌讯
 */
@AllArgsConstructor
public class Lock implements AutoCloseable{
    @Getter
    private final Object lockObject;

    private final DistributedLock locker;

    @Override
    public void close() throws Exception {
        locker.unlock(lockObject);
    }
}
