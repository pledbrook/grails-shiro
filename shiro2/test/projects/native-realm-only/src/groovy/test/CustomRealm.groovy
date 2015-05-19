package test

import org.apache.shiro.authc.AuthenticationException
import org.apache.shiro.authc.AuthenticationInfo
import org.apache.shiro.authc.AuthenticationToken
import org.apache.shiro.authc.SimpleAccount
import org.apache.shiro.authz.AuthorizationException
import org.apache.shiro.authz.Authorizer
import org.apache.shiro.authz.Permission
import org.apache.shiro.realm.AuthenticatingRealm
import org.apache.shiro.subject.PrincipalCollection

class CustomRealm extends AuthenticatingRealm implements Authorizer {
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
        println ">>> Authenticating against custom realm..."
        return new SimpleAccount(token.username, "password", "CustomRealm")
    }

    boolean isPermitted(PrincipalCollection subjectPrincipal, String permission) {
        println "isPermitted(PrincipalCollection $subjectPrincipal, String $permission)"
        return false
    }

    boolean isPermitted(PrincipalCollection subjectPrincipal, Permission permission) {
        println "isPermitted(PrincipalCollection $subjectPrincipal, Permission $permission)"
        return false
    };

    boolean[] isPermitted(PrincipalCollection subjectPrincipal, String... permissions) {
        println "isPermitted(PrincipalCollection $subjectPrincipal, String... $permissions)"
        return permissions.collect { false } as boolean[]
    };

    boolean[] isPermitted(PrincipalCollection subjectPrincipal, List<Permission> permissions) {
        println "isPermitted(PrincipalCollection $subjectPrincipal, List<Permission> $permissions)"
        return permissions.collect { false } as boolean[]
    };

    boolean isPermittedAll(PrincipalCollection subjectPrincipal, String... permissions) {
        println "isPermittedAll(PrincipalCollection $subjectPrincipal, String... $permissions)"
        return false
    };

    boolean isPermittedAll(PrincipalCollection subjectPrincipal, Collection<Permission> permissions) {
        println "isPermittedAll(PrincipalCollection $subjectPrincipal, Collection<Permission> $permissions)"
        return false
    };

    void checkPermission(PrincipalCollection subjectPrincipal, String permission) throws AuthorizationException {
        println "checkPermission(PrincipalCollection $subjectPrincipal, String $permission)"
    }

    void checkPermission(PrincipalCollection subjectPrincipal, Permission permission) throws AuthorizationException {
        println "checkPermission(PrincipalCollection $subjectPrincipal, Permission $permission)"
    }

    void checkPermissions(PrincipalCollection subjectPrincipal, String... permissions) throws AuthorizationException {
        println "checkPermissions(PrincipalCollection $subjectPrincipal, String... $permissions)"
    }

    void checkPermissions(PrincipalCollection subjectPrincipal, Collection<Permission> permissions) throws AuthorizationException {
        println "checkPermissions(PrincipalCollection $subjectPrincipal, Collection<Permission> $permissions)"
    }

    boolean hasRole(PrincipalCollection subjectPrincipal, String roleIdentifier) {
        println "hasRole --- '$subjectPrincipal' has role '$roleIdentifier'?"
        return true
    }

    boolean[] hasRoles(PrincipalCollection subjectPrincipal, List<String> roleIdentifiers) {
        println "hasRoles(PrincipalCollection $subjectPrincipal, List<String> $roleIdentifiers)"
        return roleIdentifiers.collect { true } as boolean[]
    }

    boolean hasAllRoles(PrincipalCollection subjectPrincipal, Collection<String> roleIdentifiers) {
        println "hasAllRoles(PrincipalCollection $subjectPrincipal, Collection<String> $roleIdentifiers)"
        return true
    }

    void checkRole(PrincipalCollection subjectPrincipal, String roleIdentifier) throws AuthorizationException {
        println "checkRole(PrincipalCollection $subjectPrincipal, String $roleIdentifier)"
    }

    void checkRoles(PrincipalCollection subjectPrincipal, Collection<String> roleIdentifiers) throws AuthorizationException {
        println "checkRoles(PrincipalCollection $subjectPrincipal, Collection<String> $roleIdentifiers)"
    }

    @Override
    void checkRoles(PrincipalCollection subjectPrincipal, String... roleIdentifiers) throws AuthorizationException {
        println "checkRoles(PrincipalCollection $subjectPrincipal, String... $roleIdentifiers)"
    }

    void onLogout(PrincipalCollection accountPrincipal) {
        println "onLogout(PrincipalCollection $accountPrincipal)"
    }


}
