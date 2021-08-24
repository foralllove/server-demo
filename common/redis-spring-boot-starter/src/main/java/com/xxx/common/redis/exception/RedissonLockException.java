package com.xxx.common.redis.exception;

/**
 * 描述：RedissonLockException 锁异常
 *
 * @author 归墟
 * @email huanghe@shzx.com
 * @date 2021/8/14 15:17
 * @company 数海掌讯
 */
public class RedissonLockException extends RuntimeException {
    private static final long serialVersionUID = 6610083281801529147L;

    public RedissonLockException(String message) {
        super(message);
    }
}
