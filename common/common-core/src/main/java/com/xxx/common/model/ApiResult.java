package com.xxx.common.model;

import com.xxx.common.model.enums.ApiResultCodeEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 描述：ApiResult
 *
 * @author 归墟
 * @email huanghe@shzx.com
 * @date 2021/8/17 10:11
 * @company 数海掌讯
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApiResult<T> implements Serializable {
    private static final long serialVersionUID = -1L;

    /**
     * 返回码
     */
    private Integer code;

    /**
     * 返回描述
     */
    private String msg;

    /**
     * 返回数据
     */
    private T data;

    public static <T> ApiResult<T> succeed() {
        return of(ApiResultCodeEnum.SUCCESS.getCode(), "ok", null);
    }

    public static <T> ApiResult<T> succeed(T model) {
        return of(ApiResultCodeEnum.SUCCESS.getCode(), "ok", model);
    }

    public static <T> ApiResult<T> succeed(String msg, T model) {
        return of(ApiResultCodeEnum.SUCCESS.getCode(), msg, model);
    }

    public static <T> ApiResult<T> of(Integer code, String msg, T datas) {
        return new ApiResult<>(code, msg, datas);
    }

    public static <T> ApiResult<T> failed() {
        return of(ApiResultCodeEnum.ERROR.getCode(), "failed", null);
    }

    public static <T> ApiResult<T> failed(String msg) {
        return of(ApiResultCodeEnum.ERROR.getCode(), msg, null);
    }

    public static <T> ApiResult<T> failed(String msg, T model) {
        return of(ApiResultCodeEnum.ERROR.getCode(), msg, model);
    }

    public boolean suc() {
        return ApiResultCodeEnum.SUCCESS.getCode().equals(code);
    }
}
