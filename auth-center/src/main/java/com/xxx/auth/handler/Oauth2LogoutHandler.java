package com.xxx.auth.handler;

import cn.hutool.core.util.StrUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2RefreshToken;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.util.Assert;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Enumeration;

/**
 * 描述：Oauth2LogoutHandler 退出
 *
 * @author 归墟
 * @email huanghe@shzx.com
 * @date 2021/8/14 16:47
 * @company 数海掌讯
 */
@Slf4j
@AllArgsConstructor
public class Oauth2LogoutHandler implements LogoutHandler {
    private final TokenStore tokenStore;

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        Assert.notNull(tokenStore, "tokenStore must be set");
        String token = getHeaderToken(request);

        if (StrUtil.isEmpty(token)) {
            log.debug("Token not found in request parameters.  Not an OAuth2 request.");
            return;
        }

        OAuth2AccessToken existingAccessToken = tokenStore.readAccessToken(token);
        OAuth2RefreshToken refreshToken;
        if (existingAccessToken != null) {
            refreshToken = existingAccessToken.getRefreshToken();
            if (refreshToken != null) {
                log.info("remove refreshToken! {}", refreshToken);
                tokenStore.removeRefreshToken(refreshToken);
            }
            log.info("remove existingAccessToken! {}", existingAccessToken);
            tokenStore.removeAccessToken(existingAccessToken);
        }
    }

    private String getHeaderToken(HttpServletRequest request) {
        Enumeration<String> headers = request.getHeaders("Authorization");
        String value;
        do {
            if (!headers.hasMoreElements()) {
                return null;
            }

            value = headers.nextElement();
        } while (!value.startsWith("Bearer"));
        String authHeaderValue = value.substring("Bearer".length()).trim();
        int commaIndex = authHeaderValue.indexOf(44);
        if (commaIndex > 0) {
            authHeaderValue = authHeaderValue.substring(0, commaIndex);
        }
        return authHeaderValue;
    }
}
