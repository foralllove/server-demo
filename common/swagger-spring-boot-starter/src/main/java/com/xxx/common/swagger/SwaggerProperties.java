package com.xxx.common.swagger;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 描述：SwaggerProperties
 *
 * @author 归墟
 * @email huanghe@shzx.com
 * @date 2021/8/12 15:28
 * @company gsName
 */

@Data
@Component
@ConfigurationProperties("swagger")
@EqualsAndHashCode(callSuper = true)
public class SwaggerProperties extends DocketInfo {
    /**
     * 是否开启swagger
     **/
    private Boolean enabled = Boolean.TRUE;

    /**
     * host信息
     **/
    private String host = "";

    /**
     * 分组文档
     **/
    private Map<String, DocketInfo> docket = new LinkedHashMap<>();


    @Setter
    @Getter
    public static class GlobalOperationParameter {
        /**
         * 参数名
         **/
        private String name;

        /**
         * 描述信息
         **/
        private String description;

        /**
         * 指定参数类型
         **/
        private String modelRef;

        /**
         * 参数放在哪个地方:header,query,path,body.form
         **/
        private String parameterType;

        /**
         * 参数是否必须传
         **/
        private String required;
    }

    @Data
    public static class Contact {
        /**
         * 联系人
         **/
        private String name = "";
        /**
         * 联系人url
         **/
        private String url = "";
        /**
         * 联系人email
         **/
        private String email = "";
    }
}
