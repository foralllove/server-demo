// package com.xxx.gateway.swagger;
//
// import lombok.AllArgsConstructor;
// import org.springframework.cloud.gateway.config.GatewayProperties;
// import org.springframework.cloud.gateway.route.RouteLocator;
// import org.springframework.cloud.gateway.support.NameUtils;
// import org.springframework.context.annotation.Primary;
// import org.springframework.stereotype.Component;
// import springfox.documentation.swagger.web.SwaggerResource;
// import springfox.documentation.swagger.web.SwaggerResourcesProvider;
//
// import java.util.ArrayList;
// import java.util.List;
//
// /**
//  * 描述：SwaggerProvider
//  *
//  * @author 归墟
//  * @email huanghe@shzx.com
//  * @date 2021/8/20 10:17
//  * @company 数海掌讯
//  */
// @Component
// @Primary
// @AllArgsConstructor
// public class SwaggerProvider implements SwaggerResourcesProvider {
//     /**
//      * 聚合文档中心结尾
//      */
//     private static final String SUFFIX = "-center";
//     private final RouteLocator routeLocator;
//     private final GatewayProperties gatewayProperties;
//
//     @Override
//     public List<SwaggerResource> get() {
//         List<SwaggerResource> resources = new ArrayList<>();
//         gatewayProperties.getRoutes()
//                 .forEach(routeDefinition -> {
//                             //非指定路由忽略
//                             if (routeDefinition.getId().indexOf(SUFFIX) <= 0) {
//                                 return;
//                             }
//                             routeDefinition.getPredicates().stream()
//                                     .filter(predicateDefinition -> ("Path").equalsIgnoreCase(predicateDefinition.getName()))
//                                     .forEach(predicateDefinition -> resources.add(
//                                             createSwaggerResource(routeDefinition.getId(),
//                                                     predicateDefinition.getArgs().get(NameUtils.GENERATED_NAME_PREFIX + "0").replace("/**", "/v2/api-docs"))
//                                             )
//                                     );
//                         }
//
//                 );
//         return resources;
//     }
//
//     private SwaggerResource createSwaggerResource(String name, String location) {
//         SwaggerResource swaggerResource = new SwaggerResource();
//         swaggerResource.setName(name);
//         swaggerResource.setLocation(location);
//         swaggerResource.setSwaggerVersion("2.0");
//         return swaggerResource;
//     }
// }
