package com.xxx.gateway.auth;

import cn.hutool.core.util.StrUtil;
import org.springframework.http.HttpMethod;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.ReactiveAuthorizationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.web.server.authorization.AuthorizationContext;
import org.springframework.social.security.SocialUserDetails;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Collection;

/**
 * 描述：PermissionAuthManager
 *
 * @author 归墟
 * @email huanghe@shzx.com
 * @date 2021/8/18 18:00
 * @company 数海掌讯
 */
public class PermissionAuthManager implements ReactiveAuthorizationManager<AuthorizationContext> {

    private final AntPathMatcher antPathMatcher = new AntPathMatcher();

    @Override
    public Mono<AuthorizationDecision> check(Mono<Authentication> authentication, AuthorizationContext authorizationContext) {
        return authentication.map(auth -> {
            ServerWebExchange exchange = authorizationContext.getExchange();
            ServerHttpRequest request = exchange.getRequest();

            boolean isPermission = hasPermission(auth, request);
            return new AuthorizationDecision(isPermission);
        }).defaultIfEmpty(new AuthorizationDecision(false));
    }

    private boolean hasPermission(Authentication auth, ServerHttpRequest request) {
        // 前端跨域OPTIONS请求预检放行 也可通过前端配置代理实现
        if (HttpMethod.OPTIONS.name().equalsIgnoreCase(request.getMethodValue())) {
            return true;
        }
        if ((auth instanceof AnonymousAuthenticationToken)) {
            return false;
        }

        Object obj = auth.getPrincipal();
        if (!(obj instanceof SocialUserDetails)) {
            return false;
        }

        OAuth2Authentication auth2Authentication = (OAuth2Authentication) auth;

        String requestPath = request.getURI().getPath();

        Collection<GrantedAuthority> authorities = auth2Authentication.getAuthorities();
        return authorities.parallelStream().anyMatch(e -> StrUtil.isNotEmpty(e.getAuthority()) && antPathMatcher.match(e.getAuthority(), requestPath));
    }

}
