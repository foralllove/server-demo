package com.xxx.common.model.enums;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

/**
 * 描述：ApiResultCodeEnum
 *
 * @author 归墟
 * @email huanghe@shzx.com
 * @date 2021/8/17 10:14
 * @company 数海掌讯
 */
@Getter
@AllArgsConstructor
public enum ApiResultCodeEnum {
    /**
     * 请求成功
     */
    SUCCESS(0),
    /**
     * 请求失败
     */
    ERROR(1);

    /**
     * 状态码
     */
    private final Integer code;
}
