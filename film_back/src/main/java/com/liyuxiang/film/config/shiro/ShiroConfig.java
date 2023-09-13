package com.liyuxiang.film.config.shiro;

import org.apache.shiro.authc.credential.CredentialsMatcher;
import org.apache.shiro.authc.credential.HashedCredentialsMatcher;
import org.apache.shiro.authc.pam.AtLeastOneSuccessfulStrategy;
import org.apache.shiro.authc.pam.ModularRealmAuthenticator;
import org.apache.shiro.realm.Realm;
import org.apache.shiro.spring.security.interceptor.AuthorizationAttributeSourceAdvisor;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.apache.shiro.web.session.mgt.DefaultWebSessionManager;
import org.springframework.aop.framework.autoproxy.DefaultAdvisorAutoProxyCreator;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.apache.shiro.mgt.SecurityManager;

import javax.servlet.Filter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

@Configuration
public class ShiroConfig {
    /**
     * 1.anon: 无需认证就可以访问
     * 2.authc: 必须认证了才能访问
     * 3.user: 必须拥有记住我功能才能用
     * 4.perms: 拥有对某个资源权限才能访问
     * 5、role：拥有某个角色权限
     */
    @Bean
    public ShiroFilterFactoryBean shiroFilter(SecurityManager securityManager) {
        ShiroFilterFactoryBean bean=new ShiroFilterFactoryBean();
        // 设置安全管理器
        bean.setSecurityManager(securityManager);
        //配置登录的url和登录成功的url以及验证失败的url
        bean.setLoginUrl("/user/login");
//        bean.setUnauthorizedUrl("/noauth");
        //自定义过滤器
        LinkedHashMap<String, Filter> map = new LinkedHashMap<>();
        map.put("authc", new MyFormAuthenticationFilter());
        bean.setFilters(map);
        // 配置访问权限
        // 添加shiro过滤器
        LinkedHashMap<String, String> filterChainDefinitionMap=new LinkedHashMap<>();
        filterChainDefinitionMap.put("/user/isAuth","authc");
        filterChainDefinitionMap.put("/admin/*","authc");
        filterChainDefinitionMap.put("/admin/user/login","anon");
        filterChainDefinitionMap.put("/ai","anon");
        filterChainDefinitionMap.put("/home/updateWish","authc");
        filterChainDefinitionMap.put("/home/getIsWish","authc");
        filterChainDefinitionMap.put("/Comment/getIsComment","authc");
        filterChainDefinitionMap.put("/Comment/addComment","authc");
        filterChainDefinitionMap.put("/Comment/upApprove","authc");
        filterChainDefinitionMap.put("/order/*","authc");

        bean.setFilterChainDefinitionMap(filterChainDefinitionMap);
        return bean;
    }

    @Bean
    public DefaultWebSecurityManager securityManager(@Qualifier("wxCodeRealm") WxRealm wxRealm,
                                                     @Qualifier("adminRealm") AdminRealm adminRealm,
                                                     ModularRealmAuthenticator modularRealmAuthenticator,
                                                     DefaultWebSessionManager sessionManager) {
        DefaultWebSecurityManager securityManager = new DefaultWebSecurityManager();
        securityManager.setSessionManager(sessionManager);
        securityManager.setAuthenticator(modularRealmAuthenticator);
        List<Realm> list = new ArrayList<Realm>();
        // 关联Realm
        list.add(wxRealm);
        list.add(adminRealm);
        securityManager.setRealms(list);
        return securityManager;
    }

    // 自定义realm对象 注入bean
    @Bean
    public AdminRealm adminRealm(CredentialsMatcher credentialsMatcher){
        AdminRealm adminRealm = new AdminRealm();
        //密码加密 注入在配置中CredentialsMatcher 默认为简单加密方式 MD5盐值加密
        adminRealm.setCredentialsMatcher(credentialsMatcher); // 使用加密
        //账号密码登录使用realm
        return adminRealm;
    }

    // 自定义realm对象 注入bean
    @Bean
    public WxRealm wxCodeRealm(){
        WxRealm wxRealm = new WxRealm();
        //小程序使用openid登录使用的realm
        return wxRealm;
    }
    /**
     * 系统自带的Realm管理，主要针对多realm
     * */
    @Bean
    public ModularRealmAuthenticator modularRealmAuthenticator() {
        MyModularRealmAuthenticator modularRealmAuthenticator = new MyModularRealmAuthenticator();
        //只要有一个成功就视为登录成功
        modularRealmAuthenticator.setAuthenticationStrategy(new AtLeastOneSuccessfulStrategy());
        return modularRealmAuthenticator;
    }

    //开启注解支持
    @Bean
    public DefaultAdvisorAutoProxyCreator advisorAutoProxyCreator(){
        DefaultAdvisorAutoProxyCreator advisorAutoProxyCreator = new DefaultAdvisorAutoProxyCreator();
        advisorAutoProxyCreator.setProxyTargetClass(true);
        return advisorAutoProxyCreator;
    }
    @Bean
    public AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor(SecurityManager securityManager){
        AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor = new AuthorizationAttributeSourceAdvisor();
        authorizationAttributeSourceAdvisor.setSecurityManager(securityManager);
        return authorizationAttributeSourceAdvisor;
    }

    /**
     * session 管理对象
     *
     * @return DefaultWebSessionManager
     */
    @Bean
    public DefaultWebSessionManager sessionManager() {
        //重写DefaultWebSessionManager
        MiniSessionManager sessionManager = new MiniSessionManager();
        // 设置session过期时间
        sessionManager.setGlobalSessionTimeout(3600000000000000000L);
        return sessionManager;
    }

    @Bean
    public HashedCredentialsMatcher hashedCredentialsMatcher() {
        // Shiro自带加密
        HashedCredentialsMatcher credentialsMatcher = new HashedCredentialsMatcher();
        // 散列算法使用md5
        credentialsMatcher.setHashAlgorithmName("MD5");
        // 次数
        credentialsMatcher.setHashIterations(2);
        // 是否存储为16进制
        credentialsMatcher.setStoredCredentialsHexEncoded(true);
        return credentialsMatcher;
    }
}
