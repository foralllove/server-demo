package com.xxx.auth.store;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cloud.bootstrap.encrypt.KeyProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.core.annotation.Order;
import org.springframework.security.oauth2.provider.token.DefaultAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.DefaultUserAuthenticationConverter;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;
import org.springframework.security.oauth2.provider.token.store.KeyStoreKeyFactory;
import org.springframework.stereotype.Component;

import java.security.KeyPair;

/**
 * 认证服务器使用 JWT RSA 非对称加密令牌
 *
 * @author Jarvan
 * @date 2018/7/24 16:21
 */
@Component
@ConditionalOnProperty(prefix = "oauth2.token.store", name = "type", havingValue = "authJwt")
public class AuthJwtTokenStore {

    private final KeyProperties keyProperties = new KeyProperties();

    @Bean
    public TokenStore tokenStore(JwtAccessTokenConverter jwtAccessTokenConverter) {
        return new JwtTokenStore(jwtAccessTokenConverter);
    }

    @Bean
    @Order(2)
    public JwtAccessTokenConverter jwtAccessTokenConverter() {
        final JwtAccessTokenConverter converter = new JwtAccessTokenConverter();
        KeyPair keyPair = new KeyStoreKeyFactory
                (keyProperties.getKeyStore().getLocation(), keyProperties.getKeyStore().getSecret().toCharArray())
                .getKeyPair(keyProperties.getKeyStore().getAlias());
        converter.setKeyPair(keyPair);
        DefaultAccessTokenConverter tokenConverter = (DefaultAccessTokenConverter)converter.getAccessTokenConverter();
        tokenConverter.setUserTokenConverter(new DefaultUserAuthenticationConverter());
        return converter;
    }

    // /**
    //  * jwt 生成token 定制化处理
    //  * 添加一些额外的用户信息到token里面
    //  *
    //  * @return TokenEnhancer
    //  */
    // @Bean
    // @Order(1)
    // public TokenEnhancer tokenEnhancer() {
    //     return (accessToken, authentication) -> {
    //         final Map<String, Object> additionalInfo = new HashMap<>(1);
    //         Object principal = authentication.getPrincipal();
    //         //增加id参数
    //        // if (principal instanceof SysUser) {
    //        //     SysUser user = (SysUser)principal;
    //        //     additionalInfo.put("id", user.getId());
    //        // }
    //         ((DefaultOAuth2AccessToken) accessToken).setAdditionalInformation(additionalInfo);
    //         return accessToken;
    //     };
    // }
}
