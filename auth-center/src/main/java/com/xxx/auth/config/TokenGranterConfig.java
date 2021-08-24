package com.xxx.auth.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.core.userdetails.UserDetailsByNameServiceWrapper;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.*;
import org.springframework.security.oauth2.provider.client.ClientCredentialsTokenGranter;
import org.springframework.security.oauth2.provider.code.AuthorizationCodeTokenGranter;
import org.springframework.security.oauth2.provider.code.RandomValueAuthorizationCodeServices;
import org.springframework.security.oauth2.provider.implicit.ImplicitTokenGranter;
import org.springframework.security.oauth2.provider.password.ResourceOwnerPasswordTokenGranter;
import org.springframework.security.oauth2.provider.refresh.RefreshTokenGranter;
import org.springframework.security.oauth2.provider.request.DefaultOAuth2RequestFactory;
import org.springframework.security.oauth2.provider.token.AuthorizationServerTokenServices;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationProvider;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 描述：TokenGranterConfig
 *
 * @author 归墟
 * @email huanghe@shzx.com
 * @date 2021/8/14 14:20
 * @company 数海掌讯
 */
@Configuration
public class TokenGranterConfig {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private TokenStore tokenStore;

    @Autowired
    private ClientDetailsService clientDetailsService;

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private RandomValueAuthorizationCodeServices authorizationCodeServices;

    private TokenGranter tokenGranter;

    /**
     * 授权模式
     */
    @Bean
    public TokenGranter tokenGranter() {
        if (tokenGranter == null) {
            tokenGranter = new TokenGranter() {
                private final CompositeTokenGranter delegate = new CompositeTokenGranter(getAllTokenGranters());

                @Override
                public OAuth2AccessToken grant(String grantType, TokenRequest tokenRequest) {
                    return delegate.grant(grantType, tokenRequest);
                }
            };
        }
        return tokenGranter;
    }


    /**
     * 所有授权模式：默认的5种模式 + 自定义的模式
     */
    private List<TokenGranter> getAllTokenGranters() {
        List<TokenGranter> tokenGranterList = new ArrayList<>();
        AuthorizationServerTokenServices tokenServices = createDefaultTokenServices();
        //认证
        OAuth2RequestFactory requestFactory = new DefaultOAuth2RequestFactory(clientDetailsService);
        // 添加授权码模式 db模式(授权码存储)
        tokenGranterList.add(new AuthorizationCodeTokenGranter(tokenServices, authorizationCodeServices, clientDetailsService, requestFactory));
        // 添加刷新令牌的模式
        tokenGranterList.add(new RefreshTokenGranter(tokenServices, clientDetailsService, requestFactory));
        // 添加隐士授权模式
        tokenGranterList.add(new ImplicitTokenGranter(tokenServices, clientDetailsService, requestFactory));
        // 添加客户端模式
        tokenGranterList.add(new ClientCredentialsTokenGranter(tokenServices, clientDetailsService, requestFactory));
        if (authenticationManager != null) {
            // 添加密码模式
            tokenGranterList.add(new ResourceOwnerPasswordTokenGranter(authenticationManager, tokenServices, clientDetailsService, requestFactory));
        }
        //自定义模式添加
        return tokenGranterList;
    }


    /**
     * 创建默认令牌服务
     * @return 服务
     */
    private DefaultTokenServices createDefaultTokenServices() {
        DefaultTokenServices tokenServices = new DefaultTokenServices();
        // token 存储处理
        tokenServices.setTokenStore(tokenStore);
        //支持刷新token
        tokenServices.setSupportRefreshToken(true);
        // basic 身份获取
        tokenServices.setClientDetailsService(clientDetailsService);

        PreAuthenticatedAuthenticationProvider provider = new PreAuthenticatedAuthenticationProvider();
        // 用户获取
        provider.setPreAuthenticatedUserDetailsService(new UserDetailsByNameServiceWrapper<>(userDetailsService));
        tokenServices.setAuthenticationManager(new ProviderManager(Collections.singletonList(provider)));
        return tokenServices;
    }
}
