package com.xxx.common.model.entity;

import lombok.Data;

import java.io.Serializable;

/**
 * 描述：UserEntity
 *
 * @author 归墟
 * @email huanghe@shzx.com
 * @date 2021/8/17 14:24
 * @company 数海掌讯
 */
@Data
public class UserEntity implements Serializable {
    private static final long serialVersionUID = -5886012896705137070L;

    private Long id;

    private String userName;

    private String password;

}
