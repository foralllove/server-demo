package com.xxx.auth.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.security.oauth2.common.exceptions.InvalidClientException;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.NoSuchClientException;
import org.springframework.security.oauth2.provider.client.JdbcClientDetailsService;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;

/**
 * 描述：RedisClientDetailsService redis 存储
 *
 * @author 归墟
 * @email huanghe@shzx.com
 * @date 2021/8/14 14:34
 * @company 数海掌讯
 */
@Slf4j
@Service
public class RedisClientDetailsService extends JdbcClientDetailsService {

    private final RedisTemplate<Object, Object> redisTemplate;

    public RedisClientDetailsService(DataSource dataSource, RedisTemplate<Object, Object> redisTemplate) {
        super(dataSource);
        StringRedisSerializer stringRedisSerializer = new StringRedisSerializer();
        redisTemplate.setKeySerializer(stringRedisSerializer);
        this.redisTemplate = redisTemplate;

    }

    @Override
    public ClientDetails loadClientByClientId(String clientId) {
        // 先从redis获取
        ClientDetails clientDetails = (ClientDetails) redisTemplate.opsForValue().get(getClientRedisKey(clientId));
        if (clientDetails == null) {
            clientDetails = getAndSetClient(clientId);
        }
        return clientDetails;
    }

    private ClientDetails getAndSetClient(String clientId) {
        ClientDetails clientDetails = null;
        try {
            clientDetails = super.loadClientByClientId(clientId);
            if (clientDetails != null) {
                // 写入redis缓存
                redisTemplate.opsForValue().set(getClientRedisKey(clientId), clientDetails);
                log.info("认证中心缓存clientId:{},{}", clientId, clientDetails);
            }
        } catch (NoSuchClientException e) {
            log.error("clientId:{}", clientId, e);
        } catch (InvalidClientException e) {
            log.error("getAndSetClient-invalidClient:{}", clientId, e);
        }
        return clientDetails;
    }

    @Override
    public void updateClientDetails(ClientDetails clientDetails) {
        super.updateClientDetails(clientDetails);
        getAndSetClient(clientDetails.getClientId());
    }

    @Override
    public void updateClientSecret(String clientId, String secret) {
        super.updateClientSecret(clientId, secret);
        getAndSetClient(clientId);
    }

    @Override
    public void removeClientDetails(String clientId) {
        super.removeClientDetails(clientId);
        removeRedisCache(clientId);
    }

    /**
     * 删除redis缓存
     *
     * @param clientId 客户端id
     */
    private void removeRedisCache(String clientId) {
        redisTemplate.delete(getClientRedisKey(clientId));
    }

    private String getClientRedisKey(String clientId) {
        return "oauth_client_details:" + clientId;
    }
}
