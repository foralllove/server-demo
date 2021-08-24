package com.xxx.gateway.config;

import com.alibaba.cloud.nacos.NacosConfigProperties;
import com.xxx.gateway.route.NacosRouteDefinitionRepository;
import lombok.AllArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

/**
 * 描述：DynamicRouteConfig
 *
 * @author 归墟
 * @email huanghe@shzx.com
 * @date 2021/8/18 13:48
 * @company 数海掌讯
 */
@Component
@AllArgsConstructor
public class DynamicRouteConfig {
    private final ApplicationEventPublisher publisher;

    @Bean
    public NacosRouteDefinitionRepository nacosRouteDefinitionRepository(NacosConfigProperties nacosConfigProperties) {
        return new NacosRouteDefinitionRepository(publisher, nacosConfigProperties);
    }
}
