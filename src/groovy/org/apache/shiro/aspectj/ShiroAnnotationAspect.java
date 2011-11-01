package org.apache.shiro.aspectj;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;

@Aspect
public class ShiroAnnotationAspect {
	
	private static final String pointCupExpression = "@annotation(org.apache.shiro.authz.annotation.RequiresAuthentication) || "
        + "@annotation(org.apache.shiro.authz.annotation.RequiresGuest) || "
        + "@annotation(org.apache.shiro.authz.annotation.RequiresPermissions) || "
        + "@annotation(org.apache.shiro.authz.annotation.RequiresRoles) || "
        + "@annotation(org.apache.shiro.authz.annotation.RequiresUser)";

	@Pointcut(pointCupExpression)
	void anyShiroAnnotatedMethodCall() {}

	private AspectjAnnotationsAuthorizingMethodInterceptor interceptor = new AspectjAnnotationsAuthorizingMethodInterceptor();

	@Before("anyShiroAnnotatedMethodCall()")
	public void executeAnnotatedMethod(JoinPoint thisJoinPoint) throws Throwable {
    	interceptor.performBeforeInterception(thisJoinPoint);
	}
	
}
