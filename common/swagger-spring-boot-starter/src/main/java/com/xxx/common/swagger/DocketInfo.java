package com.xxx.common.swagger;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * 描述：DocketInfo
 *
 * @author 归墟
 * @email huanghe@shzx.com
 * @date 2021/8/13 9:50
 * @company 数海掌讯
 */
@Data
public class DocketInfo {
    /**
     * 标题
     **/
    private String title = "";
    /**
     * 描述
     **/
    private String description = "";
    /**
     * 版本
     **/
    private String version = "";
    /**
     * 许可证
     **/
    private String license = "";
    /**
     * 许可证URL
     **/
    private String licenseUrl = "";
    /**
     * 服务条款URL
     **/
    private String termsOfServiceUrl = "";

    private SwaggerProperties.Contact contact = new SwaggerProperties.Contact();

    /**
     * swagger会解析的包路径
     **/
    private String basePackage = "";

    /**
     * swagger会解析的url规则
     **/
    private List<String> basePath = new ArrayList<>();
    /**
     * 在basePath基础上需要排除的url规则
     **/
    private List<String> excludePath = new ArrayList<>();

    private List<SwaggerProperties.GlobalOperationParameter> globalOperationParameters;

    public void handle(DocketInfo docketInfo) {
        this.title = title == null ? docketInfo.getTitle() : null;
        this.description = description == null ? docketInfo.getDescription() : null;
        this.version = version == null ? docketInfo.getVersion() : null;
        this.license = license == null ? docketInfo.getLicense() : null;
        this.licenseUrl = licenseUrl == null ? docketInfo.getLicenseUrl() : null;
        this.termsOfServiceUrl = termsOfServiceUrl == null ? docketInfo.getTermsOfServiceUrl() : null;
        this.contact = contact == null ? docketInfo.getContact() : null;
        this.basePackage = basePackage == null ? docketInfo.getBasePackage() : null;
        this.basePath = basePath == null ? docketInfo.getBasePath() : null;
        this.excludePath = excludePath == null ? docketInfo.getExcludePath() : null;
    }
}
