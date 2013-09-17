package org.apache.shiro.grails;

import org.apache.shiro.authz.annotation.*;

import java.lang.reflect.Method;

/**
 * User: pmcneil
 * Date: 16/09/13
 */
public class AuthorizationAttributeSourceAdvisor extends org.apache.shiro.spring.security.interceptor.AuthorizationAttributeSourceAdvisor {

    @Override
    public boolean matches(Method method, Class targetClass) {
        return ((method.getAnnotation(RequiresPermissions.class) != null) ||
                (method.getAnnotation(RequiresRoles.class) != null) ||
                (method.getAnnotation(RequiresUser.class) != null) ||
                (method.getAnnotation(RequiresGuest.class) != null) ||
                (method.getAnnotation(RequiresAuthentication.class) != null) ||
                (targetClass.getAnnotation(RequiresPermissions.class) != null) ||
                (targetClass.getAnnotation(RequiresRoles.class) != null) ||
                (targetClass.getAnnotation(RequiresUser.class) != null) ||
                (targetClass.getAnnotation(RequiresGuest.class) != null) ||
                (targetClass.getAnnotation(RequiresAuthentication.class) != null));
    }
}
