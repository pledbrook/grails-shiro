package org.apache.shiro.grails;

import java.util.Map;

import org.apache.shiro.config.Ini;
import org.apache.shiro.util.CollectionUtils;
import org.apache.shiro.web.config.WebIniSecurityManagerFactory;
import org.apache.shiro.web.mgt.WebSecurityManager;
import org.apache.shiro.web.servlet.IniShiroFilter;

import grails.core.ApplicationAttributes;
import org.springframework.context.ApplicationContext;

/**
 * Custom servlet filter that loads the Shiro configuration from an
 * init parameter in INI format. Based on IniShiroFilter, this class
 * ensures that the "shiroSecurityManager" Spring bean is used by the
 * filter rather than the one created by IniShiroFilter. Its only
 * reason for existence is to support the legacy
 * <tt>security.shiro.filter.config</tt> setting.
 */
public class LegacyShiroFilter extends IniShiroFilter {
    public static final String SM_BEAN_NAME_INIT_PARAM_NAME = "securityManagerBeanName";

    private String securityManagerBeanName;

    /**
     * Reads the 'securityManagerBeanName' servlet init parameter and
     * stores it for use by the {@link applySecurityManager} method.
     * Delegates to the super class for other init parameters.
     */
    protected void applyInitParams() throws Exception {
        String smBeanName = getInitParam(SM_BEAN_NAME_INIT_PARAM_NAME);
        if (smBeanName != null) {
            this.securityManagerBeanName = smBeanName;
        }

        super.applyInitParams();
    }

    /**
     * Grabs the bean with the configured name "securityManagerBeanName"
     * and sets that as the filter's security manager.
     */
    protected Map<String, ?> applySecurityManager(Ini ini) {
        // Get the Spring application context from the servlet context.
        ApplicationContext context = (ApplicationContext)
                getServletContext().getAttribute(ApplicationAttributes.APPLICATION_CONTEXT);

        // Create a specialised factory that grabs the security manager
        // from the application's Spring application context and initialises
        // it using the INI configuration.
        WebIniSecurityManagerFactory factory;
        if (CollectionUtils.isEmpty(ini)) {
            factory = new LegacyIniSecurityManagerFactory(context, this.securityManagerBeanName);
        } else {
            factory = new LegacyIniSecurityManagerFactory(context, this.securityManagerBeanName, ini);
        }

        setSecurityManager((WebSecurityManager) factory.getInstance());

        return factory.getBeans();
    }
}
