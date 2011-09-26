package org.apache.shiro.aspectj;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;

@Aspect
public class ShiroAnnotationAspect {
	
	private static final String pointCupExpression = "execution(@org.apache.shiro.authz.annotation.RequiresAuthentication * *(..)) || "
        + "execution(@org.apache.shiro.authz.annotation.RequiresGuest * *(..)) || "
        + "execution(@org.apache.shiro.authz.annotation.RequiresPermissions * *(..)) || "
        + "execution(@org.apache.shiro.authz.annotation.RequiresRoles * *(..)) || "
        + "execution(@org.apache.shiro.authz.annotation.RequiresUser * *(..))";

	@Pointcut(pointCupExpression)
	void anyShiroAnnotatedMethodCall() {}

	private AspectjAnnotationsAuthorizingMethodInterceptor interceptor = new AspectjAnnotationsAuthorizingMethodInterceptor();

	@Before("anyShiroAnnotatedMethodCall()")
	public void executeAnnotatedMethod(JoinPoint thisJoinPoint) throws Throwable {
    	interceptor.performBeforeInterception(thisJoinPoint);
	}
	
}
