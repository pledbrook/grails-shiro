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
package org.apache.shiro.grails

import org.apache.shiro.authz.AuthorizationException
import org.apache.shiro.authz.UnauthenticatedException
import org.springframework.web.util.WebUtils

/**
 * <p>A Grails interceptor that implements the same functionality as the old
 * JsecAuthBase abstract controller. It uses the role and permission
 * maps injected into the interceptor to determine whether the current user
 * has the required rights for a particular request.</p>
 *
 * <p>The role and permission maps are populated via the 'accessControl'
 * property on controllers or via annotations.</p>
 */
class ShiroInterceptor implements grails.artefact.Interceptor {
    def shiroAnnotationHandlerService
    ShiroInterceptor(){
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
            catch (UnauthenticatedException ex) {
                redirect(
                        controller: 'auth',
                        action: 'login',
                        params: [ targetUri: request.forwardURI - request.contextPath ])
                return false
            }
            catch (AuthorizationException ex) {
                redirect(controller: 'auth', action: 'unauthorized')
                return false
            }
        }
    }
    
    // Methods with Shiro @Requires* annotations throw AuthorizationExceptions,
    // so rather than force the user to catch them himself, we deal with them
    // in an 'afterView' interceptor.
    void afterView() {
        def e  = throwable
        if (grailsApplication.config.security.shiro.annotationdriven.enabled) {
            while (e && !(e instanceof AuthorizationException)) {
                e = e.cause
            }

            // Redirect to the 'unauthorized' page if the cause was an
            // AuthorizationException.
            if (e instanceof AuthorizationException) {
                if (e instanceof UnauthenticatedException) {
                    // User is not authenticated, so redirect to the login page.
                    redirect(
                            controller: 'auth',
                            action: 'login',
                            params: [ targetUri: request.forwardURI - request.contextPath ])
                }
                else {
                    redirect(controller: 'auth', action: 'unauthorized')
                }

                // HACK! Even with the redirect, Tomcat will execute
                // the error dispatcher unless we remove all the
                // javax.servlet.error.* attributes from the request.
                //todo is this still the case, or just for old versions??
                WebUtils.clearErrorRequestAttributes(request)
            }
        }
    }
}
