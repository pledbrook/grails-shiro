package org.apache.shiro.grails.annotations;

import java.lang.annotation.Annotation;
import org.apache.shiro.authz.AuthorizationException;
import org.apache.shiro.authz.aop.AuthorizingAnnotationHandler;

/**
 * Ties a Shiro @Requires* annotation instance to a Shiro annotation handler.
 * This is because Shiro's annotation handlers require an annotation instance
 * when invoked, but it's not worth getting this information at runtime when
 * we can find it out on startup.
 */
public class AnnotationHandler {
    private Annotation annotation;
    private AuthorizingAnnotationHandler handler;

    public AnnotationHandler(Annotation annotation, AuthorizingAnnotationHandler handler) {
        this.annotation = annotation;
        this.handler = handler;
    }

    /**
     * Executes the stored authorizing annotation handler against the stored
     * annotation. The end result is that an exception is thrown if the current
     * user does not satisfy the semantics of the stored annotation.
     */
    public void invoke() throws AuthorizationException {
        this.handler.assertAuthorized(this.annotation);
    }
}
