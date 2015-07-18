package org.apache.shiro.grails;

import org.apache.shiro.config.Ini;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.web.config.WebIniSecurityManagerFactory;
import org.springframework.context.ApplicationContext;

/**
 * Custom security manager factory that fetches the security manager
 * from a given application context rather than creating a new instance.
 */
public class LegacyIniSecurityManagerFactory extends WebIniSecurityManagerFactory {
    private ApplicationContext applicationContext;
    private String smBeanName;

    /**
     * @param context The Spring application context to fetch the security
     * manager bean from.
     * @param beanName The name of the security manager bean.
     */
    public LegacyIniSecurityManagerFactory(ApplicationContext context, String beanName) {
        this.applicationContext = context;
        this.smBeanName = beanName;
    }

    /**
     * @param context The Spring application context to fetch the security
     * manager bean from.
     * @param beanName The name of the security manager bean.
     * @param ini The INI configuration to apply to the security manager.
     */
    public LegacyIniSecurityManagerFactory(ApplicationContext context, String beanName, Ini ini) {
        super(ini);
        this.applicationContext = context;
        this.smBeanName = beanName;
    }

    /**
     * Returns the security manager that's configured in the Spring
     * application context.
     */
    protected SecurityManager createDefaultInstance() {
        return (SecurityManager) this.applicationContext.getBean(this.smBeanName);
    }
}
