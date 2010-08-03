import org.apache.shiro.spring.security.interceptor.AuthorizationAttributeSourceAdvisor
import org.springframework.aop.framework.autoproxy.DefaultAdvisorAutoProxyCreator

// Place your Spring DSL code here
beans = {
//    aasa(AuthorizationAttributeSourceAdvisor) {
//        securityManager = ref("shiroSecurityManager")
//    }

//    daapc(DefaultAdvisorAutoProxyCreator)//{ proxyTargetClass = false }
    xmlns aop:"http://www.springframework.org/schema/aop"

    tracer2(org.springframework.aop.interceptor.SimpleTraceInterceptor) {
        loggerName = "shiroTest"
    }

    aop {
        config('proxy-target-class': true) {
            advisor(pointcut: "execution(* testMethod(..))", 'advice-ref': "tracer2")
        }
    }
}
