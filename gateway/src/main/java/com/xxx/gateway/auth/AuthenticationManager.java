package com.xxx.gateway.auth;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.exceptions.InvalidTokenException;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.server.resource.BearerTokenAuthenticationToken;
import reactor.core.publisher.Mono;

/**
 * 描述：AuthenticationManager
 *
 * @author 归墟
 * @email huanghe@shzx.com
 * @date 2021/8/18 16:23
 * @company 数海掌讯
 */
@Slf4j
@AllArgsConstructor
public class AuthenticationManager implements ReactiveAuthenticationManager {
    private final TokenStore tokenStore;

    @Override
    public Mono<Authentication> authenticate(Authentication authentication) {
        return Mono.justOrEmpty(authentication)
                .filter(a -> a instanceof BearerTokenAuthenticationToken)
                .cast(BearerTokenAuthenticationToken.class)
                .map(BearerTokenAuthenticationToken::getToken)
                .flatMap((accessTokenValue -> {
                    OAuth2AccessToken accessToken = tokenStore.readAccessToken(accessTokenValue);
                    if (accessToken == null) {
                        return Mono.error(new InvalidTokenException("Invalid access token: " + accessTokenValue));
                    } else if (accessToken.isExpired()) {
                        tokenStore.removeAccessToken(accessToken);
                        return Mono.error(new InvalidTokenException("Access token expired: " + accessTokenValue));
                    }

                    OAuth2Authentication result = tokenStore.readAuthentication(accessToken);
                    if (result == null) {
                        return Mono.error(new InvalidTokenException("Invalid access token: " + accessTokenValue));
                    }
                    return Mono.just(result);
                }))
                .cast(Authentication.class);
    }
}
