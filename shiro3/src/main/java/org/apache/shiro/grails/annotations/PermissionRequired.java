package org.apache.shiro.grails.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.apache.shiro.authz.Permission;

@Target({ElementType.FIELD, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface PermissionRequired {
    Class<? extends Permission> type();

    /**
     * The name of the role required to be granted this authorization.
     */
    String target() default "*";

    String actions() default "";
}
