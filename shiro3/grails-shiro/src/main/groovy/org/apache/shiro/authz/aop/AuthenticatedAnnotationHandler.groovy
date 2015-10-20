package org.apache.shiro.authz.aop

import org.apache.shiro.authz.UnauthenticatedException
import org.apache.shiro.authz.annotation.RequiresAuthentication
import grails.artefact.controller.support.ResponseRedirector
import grails.artefact.controller.support.RequestForwarder
import grails.web.api.ServletAttributes
import java.lang.annotation.Annotation
import org.springframework.web.util.WebUtils
import org.grails.web.util.GrailsApplicationAttributes
import org.slf4j.LoggerFactory

/**
 * Handles {@link RequiresAuthentication RequiresAuthentication} annotations and ensures the calling subject is
 * authenticated before allowing access.
 *
 * @since 0.9.0
 */
public class AuthenticatedAnnotationHandler extends AuthorizingAnnotationHandler
        implements ResponseRedirector, RequestForwarder, ServletAttributes {

    final static log = LoggerFactory.getLogger(AuthenticatedAnnotationHandler)
    /**
     * Default no-argument constructor that ensures this handler to process
     * {@link org.apache.shiro.authz.annotation.RequiresAuthentication RequiresAuthentication} annotations.
     */
    public AuthenticatedAnnotationHandler() {
        super(RequiresAuthentication.class);
    }

    /**
     * Ensures that the calling <code>Subject</code> is authenticated, and if not, throws an
     * {@link org.apache.shiro.authz.UnauthenticatedException UnauthenticatedException} indicating the method is not allowed to be executed.
     *
     * @param a the annotation to inspect
     * @throws org.apache.shiro.authz.UnauthenticatedException if the calling <code>Subject</code> has not yet
     * authenticated.
     */
    public void assertAuthorized(Annotation a) {
        if (a instanceof RequiresAuthentication && !getSubject().isAuthenticated()) {
            if (request && grailsApplication.config.security.shiro.annotationdriven.enabled) {
                //We are in an HTTP request, so let's redirect if not authorized
                if (!request.getAttribute(GrailsApplicationAttributes.REDIRECT_ISSUED)) {
                    // User is not authenticated, so redirect to the login page.
                    redirect(
                            controller: 'auth',
                            action: 'login',
                            params: [targetUri: request.forwardURI - request.contextPath])
                } else {
                    log.warn("Request already redirected!!!")
                }
            } else {
                //The standard handling from the shiro library is throwing an exception
                throw new UnauthenticatedException("The current Subject is not authenticated.  Access denied.")
            }
        }
    }
}
