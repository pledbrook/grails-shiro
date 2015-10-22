package shiro

import grails.plugins.*

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
 * Modified 2015 Yellowsnow, Arkilog, Migrated to Grails 3
 */

import grails.artefact.Interceptor
import grails.util.GrailsClassUtils
import javax.servlet.Filter
import org.apache.shiro.SecurityUtils
import org.apache.shiro.authc.credential.HashedCredentialsMatcher
import org.apache.shiro.authc.credential.PasswordHashAdapter
import org.apache.shiro.authc.pam.AtLeastOneSuccessfulStrategy
import org.apache.shiro.authc.pam.ModularRealmAuthenticator
import org.apache.shiro.authz.AuthorizationException
import org.apache.shiro.authz.UnauthenticatedException
import org.apache.shiro.authz.permission.WildcardPermissionResolver
import org.apache.shiro.config.Ini
import org.apache.shiro.grails.*
import org.apache.shiro.grails.annotations.PermissionRequired
import org.apache.shiro.grails.annotations.RoleRequired
import org.apache.shiro.realm.Realm
import org.apache.shiro.session.mgt.SessionManager
import org.apache.shiro.spring.LifecycleBeanPostProcessor
import org.apache.shiro.spring.web.ShiroFilterFactoryBean
import org.apache.shiro.web.config.WebIniSecurityManagerFactory
import org.apache.shiro.web.filter.authc.BasicHttpAuthenticationFilter
import org.apache.shiro.web.mgt.CookieRememberMeManager
import org.apache.shiro.web.mgt.DefaultWebSecurityManager
import org.apache.shiro.web.session.mgt.DefaultWebSessionManager
import org.apache.shiro.web.session.mgt.ServletContainerSessionManager
import org.grails.core.artefact.ControllerArtefactHandler
import org.grails.spring.DefaultBeanConfiguration
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.aop.framework.autoproxy.DefaultAdvisorAutoProxyCreator
import org.springframework.beans.factory.config.MethodInvokingFactoryBean
import org.springframework.beans.factory.support.BeanDefinitionBuilder
import org.springframework.boot.context.embedded.FilterRegistrationBean
import org.springframework.core.Ordered
import org.springframework.web.filter.DelegatingFilterProxy
import org.springframework.web.util.WebUtils
import static javax.servlet.DispatcherType.*

class ShiroGrailsPlugin extends Plugin {

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

    def loadAfter = ["controllers", "services", "logging"]
    def observe = ["controllers"]
    def watchedResources = "file:./grails-app/realms/**/*Realm.groovy"

    def artefacts = [RealmArtefactHandler]

    def roleMaps = [:]
    def permMaps = [:]

    final static log = LoggerFactory.getLogger(ShiroGrailsPlugin)

    Closure doWithSpring() {
        { ->
            def securityConfig = grailsApplication.config.security.shiro

            println '\nConfiguring Shiro ...'

            // Configure realms defined in the project.
            def realmBeans = []
            def realmClasses = grailsApplication.realmClasses
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

            def sessionMode = securityConfig.session.mode ?: null
            if (sessionMode == null || !sessionMode.equalsIgnoreCase('native')) {
                sessionManager(ServletContainerSessionManager)
            } else {
                sessionManager(DefaultWebSessionManager)
            }
            // The real security manager instance.
            shiroSecurityManager(DefaultWebSecurityManager) { bean ->
                // Shiro doesn't like an empty collection of realms, so we
                // only configure the "realms" property if there are some.
                if (!realmBeans.isEmpty()) {
                    realms = realmBeans.collect {
                        log.debug("Adding realm bean $it")
                        ref(it)
                    }
                }

                // Allow the user to customise the session type: 'http' or
                // 'native'.
                sessionManager = ref('sessionManager')

                // Allow the user to provide his own versions of these
                // components in resources.xml or resources.groovy.
                authenticator = ref("shiroAuthenticator")
                rememberMeManager = ref("shiroRememberMeManager")
            }
            //Alias
            //      shiroAnnotationSecurityInterceptor(ShiroAnnotationSecurityInterceptor)

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
            } else {
                println "Using legacy configuration..."
                log.warn "security.shiro.filter.config option is deprecated. Use Grails' bean property override mechanism instead."
                def iniConfig = new Ini()
                iniConfig.load(securityConfig.filter.config)
                shiroSecurityManagerFactory(LegacyIniSecurityManagerFactory, applicationContext, "shiroSecurityManager", iniConfig)
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
/*            shiroFilter(ShiroFilterFactoryBean){
                securityManager = {bean->
                    bean.factoryMethod = "getInstance"
                    bean.factoryBean = "shiroSecurityManagerFactory"
                }
            }
*/
            }
            //New in Grails 3.0.x
            //instead of web.xml configuration
            log.debug('Filter definition via FilterRegistrationBean')
            servletShiroFilter(FilterRegistrationBean) {
                filter = ref('shiroFilter')
                urlPatterns = ['/*']
                dispatcherTypes = EnumSet.of(REQUEST, ERROR)
                //order = Ordered.HIGHEST_PRECEDENCE
            }
            println '\nShiro Configured'
        }
    }

    void doWithApplicationContext() {
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
    void doWithDynamicMethods() {
        // Get the access control information from the controllers, if
        // there are any.
        if (manager?.hasGrailsPlugin("controllers")) {
            // Process each controller.
            grailsApplication.controllerClasses.each { controllerClass ->
                processController(controllerClass, log)
            }
        }

        grailsApplication.filtersClasses.each { filterClass ->
            filterClass.clazz.metaClass.getRoleMap = { String controller -> return roleMaps[controller] }
            filterClass.clazz.metaClass.getPermissionMap = { String controller -> return permMaps[controller] }
        }

        // Get the config option that determines whether authentication
        // is required for access control or not. By default, it is
        // required.
        boolean authcRequired = true
        if (grailsApplication.config.security.shiro.authc.required instanceof Boolean) {
            authcRequired = grailsApplication.config.security.shiro.authc.required
        }

        // Add an 'accessControl' method to Interceptor (so that it's
        // available from Grails filters).
        def mc = Interceptor.metaClass

        mc.accessControl << { -> return accessControlMethod(grailsApplication, delegate, authcRequired) }
        mc.accessControl << { Map args -> return accessControlMethod(grailsApplication, delegate, authcRequired, args) }
        mc.accessControl << { Closure c -> return accessControlMethod(grailsApplication, delegate, authcRequired, [:], c) }
        mc.accessControl << { Map args, Closure c -> return accessControlMethod(grailsApplication, delegate, authcRequired, args, c) }
    }

    void onChange(Map<String, Object> event) {
        log.debug "onChange -> $event"
        if (grailsApplication.isControllerClass(event.source)) {
            // Get the GrailsClass instance for the controller.
            def controllerClass = grailsApplication.getControllerClass(event.source?.name)

            // If no GrailsClass can be found, i.e. 'controllerClass' is null, then this is a new controller.
            if (controllerClass == null) {
                controllerClass = grailsApplication.addArtefact(ControllerArtefactHandler.TYPE, event.source)
            }

            // Now update the role and permission information for this controller.
            log.info "Reconfiguring access control for ${controllerClass.shortName}"
            processController(controllerClass, log)
            return
        } else if (grailsApplication.isRealmClass(event.source)) {
            log.info "Realm modified!"

            def context = event.ctx
            if (!context) {
                log.debug("grailsApplication context not found - can't reload.")
                return
            }

            // Make sure the new realm class is registered.
            def realmClass = grailsApplication.addArtefact(RealmArtefactHandler.TYPE, event.source)

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
            bean.dependsOn = "shiroLifecycleBeanPostProcessor"
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
     * to filters. It requires a reference to the interceptor so that it can
     * access the dynamic properties and methods, such as "request" and
     * "redirect()".
     * @param interceptor The interceptor instance that the "accessControl()"
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
    boolean accessControlMethod(grailsApplication, interceptor, boolean authcRequired, Map args = [:], Closure c = null) {
        // If we're accessing the auth controller itself, we don't
        // want to check whether the user is authenticated, otherwise
        // we end up in an infinite loop of redirects.
        if (interceptor.controllerName == "auth") return true

        // Get hold of the interceptors class instance.
        def interceptorClass = interceptor.class

        // ...and the HTTP request.
        def request = interceptor.request

        // Is an authenticated user required for this URL? If not,
        // then we can do a permission check for remembered users
        // as well as authenticated ones. Otherwise, remembered
        // users will have to log in.
        def authenticatedUserRequired = args["auth"] || (args["auth"] == null && authcRequired)

        // If required, check that the user is authenticated.
        def subject = SecurityUtils.subject
        if (subject.principal == null || (authenticatedUserRequired && !subject.authenticated)) {
            // User is not authenticated, so deal with it. First, let
            // the interceptors class deal with it.
            boolean doDefault = true
            if (interceptorClass.metaClass.respondsTo(interceptorClass, "onNotAuthenticated")) {
                doDefault = interceptorClass.onNotAuthenticated(subject, interceptor)
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

                def redirectUri = grailsApplication.config.security.shiro.redirect.uri
                if (redirectUri) {
                    interceptor.redirect(uri: redirectUri + "?targetUri=${targetUri.encodeAsURL()}")
                } else {
                    interceptor.redirect(
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
            permString << interceptor.controllerName << ':' << (interceptor.actionName ?: "index")

            // Add the ID if it's in the web parameters.
            if (interceptor.params.id) {
                permString << ':' << interceptor.params.list('id').join(',')
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
            if (interceptorClass.metaClass.respondsTo(interceptorClass, "onUnauthorized")) {
                interceptorClass.onUnauthorized(subject, interceptor)
            } else {
                // Default behaviour is to redirect to the 'unauthorized' page.
                interceptor.redirect(controller: "auth", action: "unauthorized")
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

}
