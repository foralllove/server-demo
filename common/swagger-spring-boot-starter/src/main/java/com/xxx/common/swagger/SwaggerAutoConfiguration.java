package com.xxx.common.swagger;

import cn.hutool.core.collection.CollUtil;
import com.github.xiaoymin.knife4j.spring.annotations.EnableKnife4j;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.Lists;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import springfox.bean.validators.configuration.BeanValidatorPluginsConfiguration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.ParameterBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.schema.ModelRef;
import springfox.documentation.service.*;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 描述：SwaggerAutoConfiguration
 *
 * @author 归墟
 * @email huanghe@shzx.com
 * @date 2021/8/12 15:28
 * @company gsName
 */
@Configuration
@EnableSwagger2
@EnableKnife4j
@Import(BeanValidatorPluginsConfiguration.class)
public class SwaggerAutoConfiguration implements BeanFactoryAware {
    private static final String AUTH_KEY = "Authorization";

    private BeanFactory beanFactory;

    @Bean
    @ConditionalOnMissingBean
    public SwaggerProperties swaggerProperties() {
        return new SwaggerProperties();
    }

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(prefix = "swagger", name = "enabled", matchIfMissing = true)

    public List<Docket> createRestApi(SwaggerProperties swaggerProperties) {
        List<Docket> docketList = Lists.newArrayList();
        if (Boolean.FALSE.equals(swaggerProperties.getEnabled())) {
            return docketList;
        }

        Map<String, DocketInfo> docketInfoMap = swaggerProperties.getDocket();
        if (CollUtil.isEmpty(docketInfoMap)) {
            Docket docket = createDocket(swaggerProperties, swaggerProperties.getHost(), buildOperationParameters(swaggerProperties.getGlobalOperationParameters()));
            addBean("defaultDocket", docket);
            docketList.add(docket);
            return docketList;
        }

        // 分组创建
        for (Map.Entry<String, DocketInfo> entry : docketInfoMap.entrySet()) {
            DocketInfo docketInfo = entry.getValue();
            docketInfo.handle(swaggerProperties);
            List<Parameter> parameters = assemblyGlobalOperationParameters(swaggerProperties.getGlobalOperationParameters()
                    , docketInfo.getGlobalOperationParameters());
            Docket docket = createDocket(docketInfo, swaggerProperties.getHost(), parameters);
            docket.groupName(entry.getKey());
            addBean(entry.getKey(), docket);
            docketList.add(docket);
        }
        return docketList;
    }

    private void addBean(String name, Object bean) {
        ConfigurableBeanFactory configurableBeanFactory = (ConfigurableBeanFactory) beanFactory;
        configurableBeanFactory.registerSingleton(name, bean);
    }

    /**
     * 创建 Docket对象
     *
     * @param docketInfo swagger配置
     * @param parameters 参数
     * @return Docket
     */
    private Docket createDocket(DocketInfo docketInfo, String host, List<Parameter> parameters) {
        ApiInfo apiInfo = new ApiInfoBuilder()
                .title(docketInfo.getTitle())
                .description(docketInfo.getDescription())
                .version(docketInfo.getVersion())
                .license(docketInfo.getLicense())
                .licenseUrl(docketInfo.getLicenseUrl())
                .contact(new Contact(docketInfo.getContact().getName(),
                        docketInfo.getContact().getUrl(),
                        docketInfo.getContact().getEmail()))
                .termsOfServiceUrl(docketInfo.getTermsOfServiceUrl())
                .build();

        // base-path处理
        // 当没有配置任何path的时候，解析/**
        if (docketInfo.getBasePath().isEmpty()) {
            docketInfo.getBasePath().add("/**");
        }
        List<Predicate<String>> basePath = new ArrayList<>();
        for (String path : docketInfo.getBasePath()) {
            basePath.add(PathSelectors.ant(path));
        }

        // exclude-path处理
        List<Predicate<String>> excludePath = new ArrayList<>();
        for (String path : docketInfo.getExcludePath()) {
            excludePath.add(PathSelectors.ant(path));
        }
        return new Docket(DocumentationType.SWAGGER_2)
                .host(host)
                .apiInfo(apiInfo)
                .globalOperationParameters(parameters)
                .select()
                .apis(RequestHandlerSelectors.basePackage(docketInfo.getBasePackage()))
                .paths(Predicates.and(Predicates.not(Predicates.or(excludePath)), Predicates.or(basePath)))
                .build()
                .securitySchemes(securitySchemes())
                .securityContexts(securityContexts());

    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) {
        this.beanFactory = beanFactory;
    }

    private List<SecurityContext> securityContexts() {
        List<SecurityContext> contexts = new ArrayList<>(1);
        SecurityContext securityContext = SecurityContext.builder()
                .securityReferences(defaultAuth())
                .forPaths(PathSelectors.regex("^(?!auth).*$"))
                .build();
        contexts.add(securityContext);
        return contexts;
    }

    private List<SecurityReference> defaultAuth() {
        AuthorizationScope authorizationScope = new AuthorizationScope("global", "accessEverything");
        AuthorizationScope[] authorizationScopes = new AuthorizationScope[1];
        authorizationScopes[0] = authorizationScope;
        List<SecurityReference> references = new ArrayList<>(1);
        references.add(new SecurityReference(AUTH_KEY, authorizationScopes));
        return references;
    }

    /**
     * 默认一个全局配置参数
     * @return 参数列表
     */
    private List<ApiKey> securitySchemes() {
        List<ApiKey> apiKeys = new ArrayList<>(1);
        ApiKey apiKey = new ApiKey(AUTH_KEY, AUTH_KEY, "header");
        apiKeys.add(apiKey);
        return apiKeys;
    }

    /**
     * 全局参数设定
     * @param operationParameterList 参数设定
     * @return 参数
     */
    private List<Parameter> buildOperationParameters(List<SwaggerProperties.GlobalOperationParameter> operationParameterList) {
        List<Parameter> parameters = Lists.newArrayList();

        if (Objects.isNull(operationParameterList)) {
            return parameters;
        }
        for (SwaggerProperties.GlobalOperationParameter operationParameter : operationParameterList) {
            parameters.add(new ParameterBuilder()
                    .name(operationParameter.getName())
                    .description(operationParameter.getDescription())
                    .modelRef(new ModelRef(operationParameter.getModelRef()))
                    .parameterType(operationParameter.getParameterType())
                    .required(Boolean.parseBoolean(operationParameter.getRequired()))
                    .build());
        }
        return parameters;
    }

    /**
     * 局部参数按照name覆盖局部参数
     *
     * @param globalOperationParameters 全局
     * @param docketOperationParameters 接口
     * @return 参数列表
     */
    private List<Parameter> assemblyGlobalOperationParameters(
            List<SwaggerProperties.GlobalOperationParameter> globalOperationParameters,
            List<SwaggerProperties.GlobalOperationParameter> docketOperationParameters
    ) {

        if (CollUtil.isEmpty(docketOperationParameters)) {
            return buildOperationParameters(globalOperationParameters);
        }

        if (CollUtil.isEmpty(globalOperationParameters)) {
            return buildOperationParameters(docketOperationParameters);
        }

        Set<String> docketNames = docketOperationParameters.stream()
                .map(SwaggerProperties.GlobalOperationParameter::getName)
                .collect(Collectors.toSet());

        List<SwaggerProperties.GlobalOperationParameter> resultOperationParameters = Lists.newArrayList();

        for (SwaggerProperties.GlobalOperationParameter parameter : globalOperationParameters) {
            if (!docketNames.contains(parameter.getName())) {
                docketOperationParameters.add(parameter);
            }
        }

        return buildOperationParameters(resultOperationParameters);
    }

}
