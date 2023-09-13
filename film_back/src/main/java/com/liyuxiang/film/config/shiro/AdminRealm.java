package com.liyuxiang.film.config.shiro;

import com.liyuxiang.film.config.exception.AuthException;
import com.liyuxiang.film.entity.AdminUser;
import com.liyuxiang.film.service.AdminMenuService;
import com.liyuxiang.film.service.AdminUserService;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.util.ByteSource;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

// 继承AuthorizingRealm
public class AdminRealm extends AuthorizingRealm {
    /**
     * principal
     * authentication 认证
     *
     */
    @Autowired
    private AdminUserService adminUserService;
    @Autowired
    private AdminMenuService adminMenuService;


    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {
        //授权 得到管理员用户
        AdminUser adminUser = (AdminUser)principalCollection.getPrimaryPrincipal();
        // 数据库操作
        List<String> userPermissions = adminMenuService.selectPermissionByUserId(adminUser.getId());
        // 授予权限
        SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();
        info.addStringPermissions(userPermissions); // [user:add] 注解实现
        return info;
    }

    //认证
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken) throws AuthenticationException {
        UsernamePasswordToken token = (UsernamePasswordToken) authenticationToken;
        String username = token.getUsername(); // 获得用户名
        AdminUser adminUser = adminUserService.getByUserName(username);
        if(adminUser==null)
            throw new AuthException("该用户不存在");
        Object credentials = adminUser.getPassword();
        //第一个参数传入上面
        // 实现加盐加密认证
        AuthenticationInfo authcInfo = new SimpleAuthenticationInfo(adminUser, credentials,ByteSource.Util.bytes("liyuxiang799"),this.getClass().getSimpleName());
        return authcInfo;
    }

    @Override
    public boolean supports(AuthenticationToken authenticationToken) {
        //设置此Realm支持的Token
        return authenticationToken != null && (authenticationToken instanceof AdminToken );
    }
}
