package com.xxx.gateway.config;

import com.xxx.gateway.auth.AuthenticationManager;
import com.xxx.gateway.auth.Oauth2AuthSuccessHandler;
import com.xxx.gateway.auth.PermissionAuthManager;
import com.xxx.gateway.config.properties.AuthProperties;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.server.resource.web.server.BearerTokenServerAuthenticationEntryPoint;
import org.springframework.security.oauth2.server.resource.web.server.ServerBearerTokenAuthenticationConverter;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authentication.AuthenticationWebFilter;
import org.springframework.security.web.server.authorization.HttpStatusServerAccessDeniedHandler;

/**
 * 描述：ResourceServerConfiguration
 *
 * @author 归墟
 * @email huanghe@shzx.com
 * @date 2021/8/18 16:09
 * @company 数海掌讯
 */
@Configuration
@AllArgsConstructor
@Import({AuthProperties.class, PermissionAuthManager.class})
public class ResourceServerConfiguration {
    private final TokenStore tokenStore;

    private final AuthProperties authProperties;

    private final PermissionAuthManager permissionAuthManager;

    @Bean
    SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
        //认证处理器
        ReactiveAuthenticationManager authenticationManager = new AuthenticationManager(tokenStore);
        //token转换器
        ServerBearerTokenAuthenticationConverter tokenAuthenticationConverter = new ServerBearerTokenAuthenticationConverter();
        tokenAuthenticationConverter.setAllowUriQueryParameter(true);
        //oauth2认证过滤器
        AuthenticationWebFilter authenticationWebFilter = new AuthenticationWebFilter(authenticationManager);
        authenticationWebFilter.setServerAuthenticationConverter(tokenAuthenticationConverter);
        authenticationWebFilter.setAuthenticationSuccessHandler(new Oauth2AuthSuccessHandler());
        http.addFilterAt(authenticationWebFilter, SecurityWebFiltersOrder.AUTHENTICATION);

        ServerHttpSecurity.AuthorizeExchangeSpec authorizeExchange = http.authorizeExchange();
        if (authProperties.geAuthUrls().length > 0) {
            authorizeExchange.pathMatchers(authProperties.geAuthUrls()).authenticated();
        }
        if (authProperties.getIgnoreUrls().length > 0) {
            authorizeExchange.pathMatchers(authProperties.getIgnoreUrls()).permitAll();
        }

        authorizeExchange
                .pathMatchers(HttpMethod.OPTIONS).permitAll()
                .anyExchange()
                    .access(permissionAuthManager)
                .and()
                    .exceptionHandling()
                        .accessDeniedHandler(new HttpStatusServerAccessDeniedHandler(HttpStatus.FORBIDDEN))
                        .authenticationEntryPoint(new BearerTokenServerAuthenticationEntryPoint())
                .and()
                    .headers()
                        .frameOptions()
                         .disable()
                .and()
                    .httpBasic().disable()
                    .csrf().disable();
        return http.build();
    }
}
