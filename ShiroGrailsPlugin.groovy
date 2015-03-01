/*
 * Copyright 2007 Peter Ledbrook.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 *
 *
 * Modified 2009 Bradley Beddoes, Intient Pty Ltd, Ported to Apache Ki
 * Modified 2009 Kapil Sachdeva, Gemalto Inc, Ported to Apache Shiro
 */
import org.apache.shiro.SecurityUtils
import org.apache.shiro.authc.credential.HashedCredentialsMatcher
import org.apache.shiro.authc.credential.PasswordHashAdapter
import org.apache.shiro.authc.pam.AtLeastOneSuccessfulStrategy
import org.apache.shiro.authc.pam.ModularRealmAuthenticator
import org.apache.shiro.authz.permission.WildcardPermissionResolver
import org.apache.shiro.grails.*
import org.apache.shiro.grails.annotations.PermissionRequired
import org.apache.shiro.grails.annotations.RoleRequired
import org.apache.shiro.realm.Realm
import org.apache.shiro.session.mgt.SessionManager
import org.apache.shiro.spring.LifecycleBeanPostProcessor
import org.apache.shiro.spring.web.ShiroFilterFactoryBean
import org.apache.shiro.web.filter.authc.BasicHttpAuthenticationFilter
import org.apache.shiro.web.mgt.CookieRememberMeManager
import org.apache.shiro.web.mgt.DefaultWebSecurityManager
import org.apache.shiro.web.session.mgt.DefaultWebSessionManager
import org.apache.shiro.web.session.mgt.ServletContainerSessionManager
import org.codehaus.groovy.grails.commons.ControllerArtefactHandler
import org.codehaus.groovy.grails.commons.GrailsClassUtils
import org.codehaus.groovy.grails.plugins.web.filters.FilterConfig
import org.springframework.aop.framework.autoproxy.DefaultAdvisorAutoProxyCreator

class ShiroGrailsPlugin {

    def version = "1.2.2-SNAPSHOT"
    def grailsVersion = "1.2 > *"
    def author = "Peter Ledbrook"
    def authorEmail = "peter@cacoethes.co.uk"
    def title = "Apache Shiro Integration for Grails"
    def description = """\
Enables Grails applications to take advantage of the Apache Shiro security layer, adding easy authentication and access control via roles and permissions.
"""
    def organization = [name: "nerdErg", url: "http://nerdErg.com/"]
    def developers = [[name: "Peter Ledbrook", email: "peter@cacoethes.co.uk"], [name: "Peter McNeil", email: "pmcneil@nerderg.com"]]
    def documentation = "http://grails.org/plugin/shiro"
    def license = "APACHE"
    def issueManagement = [system: "JIRA", url: "http://jira.grails.org/browse/GPSHIRO"]
    def scm = [url: "https://github.com/pledbrook/grails-shiro"]

    def loadAfter = ["controllers", "services"]
    def observe = ["controllers"]
    def watchedResources = "file:./grails-app/realms/**/*Realm.groovy"
    def artefacts = [RealmArtefactHandler]

    def roleMaps = [:]
    def permMaps = [:]

    def doWithSpring = {
        def securityConfig = application.config.security.shiro

        println '\nConfiguring Shiro ...'

        // Configure realms defined in the project.
        def realmBeans = []
        def realmClasses = application.realmClasses
        realmClasses.each { realmClass ->
            log.info "Registering realm: ${realmClass.fullName}"
            configureRealm.delegate = delegate

            realmBeans << configureRealm(realmClass)
        }

        shiroLifecycleBeanPostProcessor(LifecycleBeanPostProcessor)

        // Shiro annotation support for services...
        shiroAdvisorAutoProxyCreator(DefaultAdvisorAutoProxyCreator) { bean ->
            bean.dependsOn = "shiroLifecycleBeanPostProcessor"
            proxyTargetClass = true
        }

        shiroAuthorizationAttributeSourceAdvisor(AuthorizationAttributeSourceAdvisor) { bean ->
            securityManager = ref("shiroSecurityManager")
        }

        // The default credential matcher.
        credentialMatcher(HashedCredentialsMatcher) { bean ->
            hashAlgorithmName = 'SHA-256'
            storedCredentialsHexEncoded = true
        }

        // This bean allows you to use the credential matcher to encode
        // passwords.
        passwordHashAdapter(PasswordHashAdapter) {
            credentialMatcher = ref("credentialMatcher")
        }

        // Default permission resolver: WildcardPermissionResolver.
        // This converts permission strings into WildcardPermission
        // instances.
        shiroPermissionResolver(WildcardPermissionResolver)

        // Default authentication strategy
        shiroAuthenticationStrategy(AtLeastOneSuccessfulStrategy)

        // Default authenticator
        shiroAuthenticator(ModularRealmAuthenticator) {
            authenticationStrategy = ref("shiroAuthenticationStrategy")
        }

        // Default remember-me manager.
        shiroRememberMeManager(CookieRememberMeManager)

        // The real security manager instance.
        shiroSecurityManager(DefaultWebSecurityManager) { bean ->
            // Shiro doesn't like an empty collection of realms, so we
            // only configure the "realms" property if there are some.
            if (!realmBeans.isEmpty()) {
                realms = realmBeans.collect { ref(it) }
            }

            // Allow the user to customise the session type: 'http' or
            // 'native'.
            if (securityConfig.session.mode) {
                sessionManager = createSessionManager(securityConfig.session.mode)
            }

            // Allow the user to provide his own versions of these
            // components in resources.xml or resources.groovy.
            authenticator = ref("shiroAuthenticator")
            rememberMeManager = ref("shiroRememberMeManager")
        }

        // If the legacy 'shiro.filter.config' setting has a value, then
        // configuration is done via IniShiroFilter and we don't use the
        // shiroFilter Spring bean. Otherwise, we create the bean so that
        // it can be used by the configured DelegatingFilterProxy.
        if (!securityConfig.filter.config) {
            // Create a basic authentication filter bean if the relevant
            // configuration setting is used.
            if (securityConfig.filter.basicAppName) {
                authcBasicFilter(BasicHttpAuthenticationFilter) {
                    applicationName = securityConfig.filter.basicAppName
                }
            }

            // Create the main security filter.
            shiroFilter(ShiroFilterFactoryBean) { bean ->
                securityManager = ref("shiroSecurityManager")

                loginUrl = securityConfig.filter.loginUrl ?: "/auth/login"
                unauthorizedUrl = securityConfig.filter.unauthorizedUrl ?: "/auth/unauthorized"

                if (securityConfig.filter.filterChainDefinitions) {
                    filterChainDefinitions = securityConfig.filter.filterChainDefinitions
                }

                if (securityConfig.filter.basicAppName) {
                    filters = [authcBasic: ref("authcBasicFilter")]
                }
            }
        }
        println '\nShiro Configured'
    }

    def doWithApplicationContext = { applicationContext ->
        def mgr = applicationContext.getBean("shiroSecurityManager")

        // Add any extra realms that might have been defined in the project
        def beans = applicationContext.getBeanNamesForType(Realm) as List

        // Filter out beans created by the plugin for the realm artefacts.
        beans = beans.findAll { !(it.endsWith("Wrapper") || it.endsWith("Proxy")) }

        // Add the remaining beans to the security manager.
        log.info "Registering native realms: $beans"
        def realms = beans.collect { applicationContext.getBean(it) }

        if (mgr.realms == null) {
            // If there are no native realms and no normal realms,
            // then there is probably something wrong.
            if (!realms) {
                log.warn "No Shiro realms configured - access control won't work!"
            } else {
                mgr.realms = realms
            }
        } else {
            mgr.realms.addAll(realms)
        }
    }

    /**
     * Adds 'roleMap' and 'permissionMap' properties to controllers
     * so that the before-interceptor can query them to find out
     * whether a user has the required role/permission for an action.
     */
    def doWithDynamicMethods = { ctx ->
        // Get the access control information from the controllers, if
        // there are any.
        if (manager?.hasGrailsPlugin("controllers")) {
            // Process each controller.
            application.controllerClasses.each { controllerClass ->
                processController(controllerClass, log)
            }
        }

        application.filtersClasses.each { filterClass ->
            filterClass.clazz.metaClass.getRoleMap = { String controller -> return roleMaps[controller] }
            filterClass.clazz.metaClass.getPermissionMap = { String controller -> return permMaps[controller] }
        }

        // Get the config option that determines whether authentication
        // is required for access control or not. By default, it is
        // required.
        boolean authcRequired = true
        if (application.config.security.shiro.authc.required instanceof Boolean) {
            authcRequired = application.config.security.shiro.authc.required
        }

        // Add an 'accessControl' method to FilterConfig (so that it's
        // available from Grails filters).
        def mc = FilterConfig.metaClass

        mc.accessControl << { -> return accessControlMethod(application, delegate, authcRequired) }
        mc.accessControl << { Map args -> return accessControlMethod(application, delegate, authcRequired, args) }
        mc.accessControl << { Closure c -> return accessControlMethod(application, delegate, authcRequired, [:], c) }
        mc.accessControl << { Map args, Closure c -> return accessControlMethod(application, delegate, authcRequired, args, c) }
    }

    def doWithWebDescriptor = { xml ->
        def contextParam = xml.'context-param'
        contextParam[contextParam.size() - 1] + {
            'filter' {
                'filter-name'('shiroFilter')

                // If the legacy 'security.shiro.filter.config' option is set,
                // use our custom INI-based filter...
                if (application.config.security.shiro.filter.config) {
                    log.warn "security.shiro.filter.config option is deprecated. Use Grails' bean property override mechanism instead."

                    'filter-class'('org.apache.shiro.grails.LegacyShiroFilter')
                    'init-param' {
                        'param-name'('securityManagerBeanName')
                        'param-value'('shiroSecurityManager')
                    }

                    // If a Shiro configuration is available, add it
                    // as an 'init-param' of the filter. This config should
                    // be in .ini format.
                    'init-param' {
                        'param-name'('config')
                        'param-value'(application.config.security.shiro.filter.config)
                    }
                } else {
                    // ...otherwise use a DelagatingFilterProxy with the 'shiroFilter'
                    // bean, which is now the recommended approach. Configuration is
                    // done via Grails' bean property override configuration.
                    'filter-class'('org.springframework.web.filter.DelegatingFilterProxy')
                    'init-param' {
                        'param-name'('targetFilterLifecycle')
                        'param-value'('true')
                    }
                }
            }
        }

        // Place the Shiro filters after the Spring character encoding filter, otherwise the latter filter won't work.
        def filter = xml.'filter-mapping'.find { it.'filter-name'.text() == "charEncodingFilter" }

        // NOTE: The following shenanigans are designed to ensure that
        // the filter mapping is inserted in the right location under
        // a variety of circumstances. However, at this point in time
        // it's a bit of wasted effort because Grails itself can't handle
        // certain situations, such as no filter mappings at all, or
        // a SiteMesh one but no character encoding filter mapping.
        // Bleh.
        if (!filter) {
            /* Of course, if there is no char encoding filter, the next
             requirement is that we come before the SiteMesh filter.
             This is trickier to accomplish. First we find out at what
             index the SiteMesh filter mapping is. */
            int i = 0
            int siteMeshIndex = -1
            xml.'filter-mapping'.each {
                if (it.'filter-name'.text().equalsIgnoreCase("sitemesh")) {
                    siteMeshIndex = i
                }
                i++
            }

            if (siteMeshIndex > 0) {
                /* There is at least one other filter mapping that comes
                 before the SiteMesh one, so we can simply use the filter
                 mapping that comes immediately before SiteMesh as the
                 insertion point. */
                filter = xml.'filter-mapping'[siteMeshIndex - 1]
            } else if (siteMeshIndex == 0 || xml.'filter-mapping'.size() == 0) {
                /* If the index of the SiteMesh filter mapping is 0, i.e.
                 it's the first one, we need to use the last filter
                 definition as the insertion point. We also need to do
                 this if there are no filter mappings. */
                def filters = xml.'filter'
                filter = filters[filters.size() - 1]
            } else {
                // Simply add this filter mapping to the end.
                def filterMappings = xml.'filter-mapping'
                filter = filterMappings[filterMappings.size() - 1]
            }
        }

        // Finally add the Shiro filter mapping after the selected insertion point.
        filter + {
            'filter-mapping' {
                'filter-name'('shiroFilter')
                'url-pattern'("/*")
                dispatcher('REQUEST')
                dispatcher('ERROR')
            }
        }
    }

    def onChange = { event ->
        if (application.isControllerClass(event.source)) {
            // Get the GrailsClass instance for the controller.
            def controllerClass = application.getControllerClass(event.source?.name)

            // If no GrailsClass can be found, i.e. 'controllerClass' is null, then this is a new controller.
            if (controllerClass == null) {
                controllerClass = application.addArtefact(ControllerArtefactHandler.TYPE, event.source)
            }

            // Now update the role and permission information for this controller.
            log.info "Reconfiguring access control for ${controllerClass.shortName}"
            processController(controllerClass, log)
            return
        } else if (application.isRealmClass(event.source)) {
            log.info "Realm modified!"

            def context = event.ctx
            if (!context) {
                log.debug("Application context not found - can't reload.")
                return
            }

            // Make sure the new realm class is registered.
            def realmClass = application.addArtefact(RealmArtefactHandler.TYPE, event.source)

            // We clone the closure because we're going to change
            // the delegate.
            def beans = beans(configureRealm.curry(realmClass))
            beans.registerBeans(context)
        }
    }

    def configureRealm = { grailsClass ->
        def realmName = grailsClass.shortName

        // Create the realm bean.
        "${realmName}Instance"(grailsClass.clazz) { bean ->
            bean.autowire = "byName"
        }

        // Wrap each realm with an adapter that implements the Shiro Realm interface.
        def wrapperName = "${realmName}Wrapper".toString()
        "${wrapperName}"(RealmWrapper) {
            realm = ref("${realmName}Instance")
            tokenClass = GrailsClassUtils.getStaticPropertyValue(grailsClass.clazz, "authTokenClass")
            permissionResolver = ref("shiroPermissionResolver")
        }

        // Return the bean name for this realm.
        return wrapperName
    }

    /**
     * Implementation of the "accessControl()" dynamic method available
     * to filters. It requires a reference to the filter so that it can
     * access the dynamic properties and methods, such as "request" and
     * "redirect()".
     * @param filter The filter instance that the "accessControl()"
     * method is called from.
     * @param authcRequired Specifies whether the default behaviour is
     * to only allow access if the user is authenticated. If
     * <code>false</code>, remembered users are also allowed unless this
     * setting is overridden in the arguments of the method.
     * @param args An argument map as passed to the "accessControl()"
     * method. Only the "auth" argument is supported at the moment.
     * @param c The closure to execute if the user has not been blocked
     * by the authentication requirement. The closure should return
     * <code>true</code> to allow access, or <code>false</code> otherwise.
     */
    boolean accessControlMethod(application, filter, boolean authcRequired, Map args = [:], Closure c = null) {
        // If we're accessing the auth controller itself, we don't
        // want to check whether the user is authenticated, otherwise
        // we end up in an infinite loop of redirects.
        if (filter.controllerName == "auth") return true

        // Get hold of the filters class instance.
        def filtersClass = filter.filtersDefinition

        // ...and the HTTP request.
        def request = filter.request

        // Is an authenticated user required for this URL? If not,
        // then we can do a permission check for remembered users
        // as well as authenticated ones. Otherwise, remembered
        // users will have to log in.
        def authenticatedUserRequired = args["auth"] || (args["auth"] == null && authcRequired)

        // If required, check that the user is authenticated.
        def subject = SecurityUtils.subject
        if (subject.principal == null || (authenticatedUserRequired && !subject.authenticated)) {
            // User is not authenticated, so deal with it. First, let
            // the filters class deal with it.
            boolean doDefault = true
            if (filtersClass.metaClass.respondsTo(filtersClass, "onNotAuthenticated")) {
                doDefault = filtersClass.onNotAuthenticated(subject, filter)
            }

            // Continue with the default behaviour of redirecting to
            // the login page, unless the onNotAuthenticated() method
            // requests otherwise.
            if (doDefault) {
                // Default behaviour is to redirect to the login page.
                // We start by building the target URI from the request's
                // 'forwardURI', which is the URL specified by the
                // browser.
                def targetUri = new StringBuilder(request.forwardURI[request.contextPath.size()..-1])
                def query = request.queryString
                if (query) {
                    if (!query.startsWith('?')) {
                        targetUri << '?'
                    }
                    targetUri << query
                }

                def redirectUri = application.config.security.shiro.redirect.uri
                if (redirectUri) {
                    filter.redirect(uri: redirectUri + "?targetUri=${targetUri.encodeAsURL()}")
                } else {
                    filter.redirect(
                            controller: "auth",
                            action: "login",
                            params: [targetUri: targetUri.toString()])
                }
            }

            return false
        }

        def isPermitted
        if (c == null) {
            // Check that the user has the required permission for the target controller/action.
            def permString = new StringBuilder()
            permString << filter.controllerName << ':' << (filter.actionName ?: "index")

            // Add the ID if it's in the web parameters.
            if (filter.params.id) {
                permString << ':' << filter.params.list('id').join(',')
            }
            
            isPermitted = subject.isPermitted(permString.toString())
        } else {
            // Call the closure with the access control builder and
            // check the result. The closure will return 'true' if the
            // user is permitted access, otherwise 'false'.
            c.delegate = new FilterAccessControlBuilder(subject)
            isPermitted = c.call()
        }

        if (!isPermitted) {
            // User does not have the required permission(s)
            if (filtersClass.metaClass.respondsTo(filtersClass, "onUnauthorized")) {
                filtersClass.onUnauthorized(subject, filter)
            } else {
                // Default behaviour is to redirect to the 'unauthorized' page.
                filter.redirect(controller: "auth", action: "unauthorized")
            }

            return false
        } else {
            return true
        }
    }

    def processController(controllerClass, log) {
        // This is the wrapped class.
        def clazz = controllerClass.clazz

        // These maps are made available to controllers via the dynamically injected 'roleMap' and 'permissionMap' properties.
        def roleMap = [:]
        def permissionMap = [:]
        this.roleMaps[controllerClass.logicalPropertyName] = roleMap
        this.permMaps[controllerClass.logicalPropertyName] = permissionMap

        // Process any annotations that this controller declares.
        log.debug "Processing annotations on ${controllerClass.shortName}"
        processAnnotations(controllerClass, roleMap, permissionMap, log)

        // Check whether this controller class has a static
        // 'accessControl' property. If so, use that as a definition
        // of the controller's role and permission requirements.
        // Note that these settings override any annotations that
        // are declared in the class.
        if (GrailsClassUtils.isStaticProperty(clazz, "accessControl")) {
            // The property should be a Closure. If it's not, we can't do anything with it.
            def c = GrailsClassUtils.getStaticPropertyValue(clazz, "accessControl")
            if (!(c instanceof Closure)) {
                log.error("Static property [accessControl] on controller [${controllerClass.fullName}] is not a closure.")
                return
            }

            // Process the closure, building a map of actions to permissions and a map of actions to roles.
            def b = new AccessControlBuilder(clazz)
            c.delegate = b
            c.call()

            roleMap.putAll(b.roleMap)
            permissionMap.putAll(b.permissionMap)

            if (log.isDebugEnabled()) {
                log.debug("Access control role map for controller '${controllerClass.logicalPropertyName}': ${roleMap}")
                log.debug("Access control permission map for controller '${controllerClass.logicalPropertyName}': ${permissionMap}")
            }
        }

        // Inject the role and permission maps into the controller.
        controllerClass.metaClass.getRoleMap = { ->
            return roleMap
        }

        controllerClass.metaClass.getPermissionMap = { ->
            return permissionMap
        }
    }

    /**
     * Process any plugin annotations (RoleRequired or PermissionRequired)
     * on the given controller. Any annotations are evaluated and used
     * to update the role and permission maps.
     */
    def processAnnotations(controllerClass, roleMap, permissionMap, log) {
        def clazz = controllerClass.clazz
        clazz.declaredFields.each { field ->
            // First see whether this field/action requires any roles.
            def ann = field.getAnnotation(RoleRequired)
            if (ann != null) {
                if (log.isDebugEnabled()) {
                    log.debug("Annotation role required by controller '${controllerClass.logicalPropertyName}', action '${field.name}': ${ann.value()}")
                }

                // Found RoleRequired annotation. Configure the interceptor
                def roles = roleMap[field.name]
                if (!roles) {
                    roles = []
                    roleMap[field.name] = roles
                }
                roles << ann.value()
            }

            // Now check for permission requirements.
            ann = field.getAnnotation(PermissionRequired)
            if (ann != null) {
                if (log.isDebugEnabled()) {
                    log.debug("Annotation permission required by controller '${controllerClass.logicalPropertyName}', action '${field.name}': ${ann.value()}")
                }

                // Found PermissionRequired annotation. Configure the interceptor for this.
                def permissions = permissionMap[field.name]
                if (!permissions) {
                    permissions = []
                    permissionMap[field.name] = permissions
                }

                def constructor = ann.type().getConstructor([String, String] as Class[])
                permissions << constructor.newInstance([ann.target(), ann.actions()] as Object[])
            }
        }
    }

    private static SessionManager createSessionManager(String sessionMode) {
        if (sessionMode == null || !sessionMode.equalsIgnoreCase('native')) {
            return new ServletContainerSessionManager();
        } else {
            return new DefaultWebSessionManager();
        }
    }
}
