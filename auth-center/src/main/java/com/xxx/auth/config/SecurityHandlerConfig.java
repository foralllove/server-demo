package com.xxx.auth.config;

import com.xxx.auth.handler.Oauth2LogoutHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.oauth2.common.exceptions.InvalidGrantException;
import org.springframework.security.oauth2.common.exceptions.OAuth2Exception;
import org.springframework.security.oauth2.provider.error.DefaultWebResponseExceptionTranslator;
import org.springframework.security.oauth2.provider.error.WebResponseExceptionTranslator;
import org.springframework.security.oauth2.provider.token.TokenStore;

/**
 * 描述：SecurityHandlerConfig 认证结果处理
 *
 * @author 归墟
 * @email huanghe@shzx.com
 * @date 2021/8/14 16:45
 * @company 数海掌讯
 */
@Slf4j
@Configuration
public class SecurityHandlerConfig {

    @Bean
    public Oauth2LogoutHandler oauthLogoutHandler(TokenStore tokenStore) {
        return new Oauth2LogoutHandler(tokenStore);
    }

    /**
     * 异常
     * @return 异常处理
     */
    @Bean
    public WebResponseExceptionTranslator<OAuth2Exception> webResponseExceptionTranslator() {
        return new DefaultWebResponseExceptionTranslator() {

            @Override
            public ResponseEntity<OAuth2Exception> translate(Exception e) throws Exception {
                OAuth2Exception oAuth2Exception;
                if (e instanceof BadCredentialsException) {
                    oAuth2Exception = new InvalidGrantException("用户名或密码错误", e);
                } else if (e instanceof InternalAuthenticationServiceException) {
                    oAuth2Exception = new InvalidGrantException(e.getMessage(), e);
                } else if (e instanceof OAuth2Exception) {
                    oAuth2Exception = (OAuth2Exception) e;
                } else {
                    oAuth2Exception = new OAuth2Exception("服务内部错误", e);
                }
                ResponseEntity<OAuth2Exception> response = super.translate(oAuth2Exception);
                ResponseEntity.status(oAuth2Exception.getHttpErrorCode());
                OAuth2Exception body = response != null ? response.getBody() : null;
                if (body != null) {
                    body.addAdditionalInformation("code", oAuth2Exception.getHttpErrorCode() + "");
                    body.addAdditionalInformation("msg", oAuth2Exception.getMessage());
                }
                return response;
            }
        };
    }

    // /**
    //  * 登陆成功
    //  */
    // @Bean
    // public AuthenticationSuccessHandler loginSuccessHandler() {
    //     return new SavedRequestAwareAuthenticationSuccessHandler() {
    //         @Override
    //         public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
    //                                             Authentication authentication) throws IOException, ServletException {
    //             super.onAuthenticationSuccess(request, response, authentication);
    //         }
    //     };
    // }
}
