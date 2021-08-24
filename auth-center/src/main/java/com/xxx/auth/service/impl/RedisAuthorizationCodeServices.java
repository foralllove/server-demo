package com.xxx.auth.service.impl;

import com.xxx.common.util.JsonUtil;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.code.RandomValueAuthorizationCodeServices;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * 描述：RedisAuthorizationCodeServices
 *
 * @author 归墟
 * @email huanghe@shzx.com
 * @date 2021/8/16 16:55
 * @company 数海掌讯
 */
@Service
public class RedisAuthorizationCodeServices extends RandomValueAuthorizationCodeServices {
    private final RedisTemplate<String,String> redisTemplate;

    public RedisAuthorizationCodeServices(RedisTemplate<String,String>  redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    /**
     * 替换JdbcAuthorizationCodeServices的存储策略   json格式存储。。好看（可升级序列化存储）
     * 将存储code到redis，并设置过期时间，10分钟
     */
    @Override
    protected void store(String code, OAuth2Authentication authentication) {
        redisTemplate.opsForValue().set(getOauthCodeKey(code), JsonUtil.toJsonString(authentication), 10, TimeUnit.MINUTES);
    }

    @Override
    protected OAuth2Authentication remove(final String code) {
        String codeKey = getOauthCodeKey(code);
        OAuth2Authentication token = JsonUtil.toObject(redisTemplate.opsForValue().get(codeKey),OAuth2Authentication.class);
        redisTemplate.delete(codeKey);
        return token;
    }

    private String getOauthCodeKey(String code) {
        return "oauth.code:" + code;
    }

}
