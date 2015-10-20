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
package org.apache.shiro.grails

import org.apache.shiro.authz.AuthorizationException
import org.apache.shiro.authz.UnauthenticatedException
import org.springframework.web.util.WebUtils
import org.grails.web.util.GrailsApplicationAttributes
import org.apache.shiro.SecurityUtils
/**
 * <p>A Grails interceptor that implements the same functionality as the old
 * JsecAuthBase abstract controller. It uses the role and permission
 * maps injected into the interceptor to determine whether the current user
 * has the required rights for a particular request.</p>
 *
 * <p>The role and permission maps are populated via the 'accessControl'
 * property on controllers or via annotations.</p>
 */
class ShiroAnnotationSecurityInterceptor {
    def shiroAnnotationHandlerService
    ShiroAnnotationSecurityInterceptor(){
        matchAll()
        .excludes(controller:"auth")
    }
    boolean before() {
        if (grailsApplication.config.security.shiro.annotationdriven.enabled) {
            try {
                def handlers = shiroAnnotationHandlerService.getHandlersFor(controllerName, actionName)
                handlers.each { h -> h.invoke() }
                return true
            }
            catch (AuthorizationException ex) {
                if (!request.getAttribute(GrailsApplicationAttributes.REDIRECT_ISSUED)) {
                    if (SecurityUtils.subject?.isAuthenticated()) {
                        redirect(controller: 'auth', action: 'unauthorized')
                    } else {
                        redirect(controller: 'auth', action: 'login',
                            params: [targetUri: request.forwardURI - request.contextPath])
                    }
                } else {
                    log.warn("Request already redirected!!!")
                }
                return false
            }
        } else {
            return true
        }
    }
    
}
