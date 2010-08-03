package org.apache.shiro.grails.annotations;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import org.apache.shiro.authz.AuthorizationException;
import org.apache.shiro.authz.Permission;
import org.apache.shiro.authz.UnauthorizedException;
import org.apache.shiro.authz.aop.AuthorizingAnnotationHandler;

/**
 * A custom annotation handler that deals with the legacy @PermissionRequired
 * annotation provided by the plugin. Users should switch to using the native
 * Shiro annotation instead.
 * <p>
 * The implementation has basically been lifted from Shiro's
 * <tt>PermissionAnnotationHandler</tt>.
 */
public class LegacyPermissionAnnotationHandler extends AuthorizingAnnotationHandler {
    public LegacyPermissionAnnotationHandler() {
        super(PermissionRequired.class);
    }
   
    public void assertAuthorized(Annotation a) throws AuthorizationException {
        if (!(a instanceof PermissionRequired)) {
            return;
        }

        PermissionRequired ann = (PermissionRequired)a;

        try {
            Constructor constructor = ann.type().getConstructor(new Class[] { String.class, String.class });
            Object permission = constructor.newInstance(new Object[] { ann.target(), ann.actions() });

            if (!getSubject().isPermitted((Permission) permission)) {
                String msg = "Calling Subject does not have required permission [" + permission + "].  " +
                        "Method invocation denied.";
                throw new UnauthorizedException(msg);
            }
        }
        catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }
}
