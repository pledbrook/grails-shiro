package org.apache.shiro.grails.annotations;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;
import org.apache.shiro.authz.AuthorizationException;
import org.apache.shiro.authz.UnauthorizedException;
import org.apache.shiro.authz.aop.AuthorizingAnnotationHandler;

/**
 * A custom annotation handler that deals with the legacy @RoleRequired
 * annotation provided by the plugin. Users should switch to using the
 * native Shiro annotations instead.
 * <p>
 * The implementation has basically been lifted from Shiro's
 * <tt>RoleAnnotationHandler</tt>.
 */
public class LegacyRoleAnnotationHandler extends AuthorizingAnnotationHandler {
    public LegacyRoleAnnotationHandler() {
        super(RoleRequired.class);
    }
   
    public void assertAuthorized(Annotation a) throws AuthorizationException {
        if (!(a instanceof RoleRequired)) {
            return;
        }

        RoleRequired ann = (RoleRequired)a;
        String roleId = ann.value();
        String[] roles = roleId.split(",");

        if (roles.length == 1) {
            if (!getSubject().hasRole(roles[0])) {
                String msg = "Calling Subject does not have required role [" + roleId + "].  " +
                        "MethodInvocation denied.";
                throw new UnauthorizedException(msg);
            }
        }
        else {
            Set<String> rolesSet = new LinkedHashSet<String>(Arrays.asList(roles));
            if (!getSubject().hasAllRoles(rolesSet)) {
                String msg = "Calling Subject does not have required roles [" + roleId + "].  " +
                        "MethodInvocation denied.";
                throw new UnauthorizedException(msg);
            }
        }
    }
}
