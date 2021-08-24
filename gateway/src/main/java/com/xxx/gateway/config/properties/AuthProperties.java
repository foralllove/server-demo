package com.xxx.gateway.config.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;

import java.util.Arrays;
import java.util.List;

/**
 * 描述：AuthProperties
 *
 * @author 归墟
 * @email huanghe@shzx.com
 * @date 2021/8/18 16:43
 * @company 数海掌讯
 */
@Data
@RefreshScope
@ConfigurationProperties(prefix = "auth")
public class AuthProperties {
    private String[] authUrls = {};

    private String[] ignoreUrls = {};

    private static final String[] SYS_IGNORE_URL = {
            "/oauth/**",
            "/actuator/**",
            "/*/v2/api-docs",
            "/*/v2/api-docs-ext",
            "/swagger/api-docs",
            "/doc.html",
            "/swagger-resources/**",
            "/webjars/**",
            "/druid/**"
    };

    public String[] geAuthUrls() {
        if (authUrls == null || authUrls.length <= 0) {
            return new String[0];
        }
        return authUrls;
    }

    public String[] getIgnoreUrls() {
        if (ignoreUrls == null || ignoreUrls.length <= 0) {
            return SYS_IGNORE_URL;
        }
        List<String> list = Arrays.asList(ignoreUrls);
        list.addAll(Arrays.asList(SYS_IGNORE_URL));
        return list.toArray(new String[0]);
    }

}
