package com.xxx.common.model;

import cn.hutool.core.collection.CollUtil;
import com.xxx.common.model.entity.UserEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.social.security.SocialUserDetails;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 描述：SysUser
 *
 * @author 归墟
 * @email huanghe@shzx.com
 * @date 2021/8/17 13:43
 * @company 数海掌讯
 */

@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
public class SysUser extends UserEntity implements Serializable, SocialUserDetails {
    private static final long serialVersionUID = -5886012896705137070L;

    private List<String> urlList;

    @Override
    public String getUserId() {
        return String.valueOf(this.getId());
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        if (CollUtil.isEmpty(urlList)) {
            return new HashSet<>();
        }
        return urlList.stream().map(SimpleGrantedAuthority::new).collect(Collectors.toSet());
    }

    @Override
    public String getUsername() {
        return this.getUserName();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return this.getId() != null;
    }
}
