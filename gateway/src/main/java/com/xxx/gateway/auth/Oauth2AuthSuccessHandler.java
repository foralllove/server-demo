package com.xxx.gateway.auth;

import com.xxx.common.util.JsonUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.server.WebFilterExchange;
import org.springframework.security.web.server.authentication.ServerAuthenticationSuccessHandler;
import org.springframework.social.security.SocialUserDetails;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;



/**
 * 描述：Oauth2AuthSuccessHandler 认证成功处理
 *
 * @author 归墟
 * @email huanghe@shzx.com
 * @date 2021/8/18 17:20
 * @company 数海掌讯
 */
@Slf4j
public class Oauth2AuthSuccessHandler implements ServerAuthenticationSuccessHandler {
    @Override
    public Mono<Void> onAuthenticationSuccess(WebFilterExchange webFilterExchange, Authentication authentication) {
        ServerHttpRequest request = webFilterExchange.getExchange().getRequest();
        String headerJson = JsonUtil.toJsonString(request.getHeaders().entrySet());
        log.info("header 头域值：{},{}", headerJson, request.getMethodValue());

        MultiValueMap<String, String> headerValues = new LinkedMultiValueMap<>(4);
        Object principal = authentication.getPrincipal();
        if (principal instanceof SocialUserDetails) {
            SocialUserDetails userDetails = (SocialUserDetails) authentication.getPrincipal();
            headerValues.add("x-user-id-header", userDetails.getUserId());
            headerValues.add("x-user-name-header", userDetails.getUsername());
        }

        ServerWebExchange exchange = webFilterExchange.getExchange();
        ServerHttpRequest serverHttpRequest = exchange.getRequest().mutate()
                .headers(h -> h.addAll(headerValues))
                .build();

        ServerWebExchange build = exchange.mutate().request(serverHttpRequest).build();
        return webFilterExchange.getChain().filter(build);
    }
}
