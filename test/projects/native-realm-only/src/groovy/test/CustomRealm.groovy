package test

import org.apache.shiro.authc.Account
import org.apache.shiro.authc.AuthenticationException
import org.apache.shiro.authc.AuthenticationInfo
import org.apache.shiro.authc.AuthenticationToken
import org.apache.shiro.authc.SimpleAccount
import org.apache.shiro.authz.*
import org.apache.shiro.realm.AuthenticatingRealm
import org.apache.shiro.subject.PrincipalCollection

class CustomRealm extends AuthenticatingRealm {
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
        println ">>> Authenticating against custom realm..."
        return new SimpleAccount(token.username, "password", "CustomRealm")
    }

    boolean isPermitted( PrincipalCollection subjectPrincipal, String permission ) { return false }
    boolean isPermitted( PrincipalCollection subjectPrincipal, Permission permission ) { return false };
    boolean[] isPermitted( PrincipalCollection subjectPrincipal, String... permissions ) { return permissions.collect{ false } as boolean[] };
    boolean[] isPermitted( PrincipalCollection subjectPrincipal, List<Permission> permissions ) { return permissions.collect{ false } as boolean[] };
    boolean isPermittedAll( PrincipalCollection subjectPrincipal, String... permissions ) { return false };
    boolean isPermittedAll( PrincipalCollection subjectPrincipal, Collection<Permission> permissions ) { return false };
    void checkPermission( PrincipalCollection subjectPrincipal, String permission ) throws AuthorizationException {}
    void checkPermission( PrincipalCollection subjectPrincipal, Permission permission ) throws AuthorizationException {}
    void checkPermissions( PrincipalCollection subjectPrincipal, String... permissions ) throws AuthorizationException {}
    void checkPermissions( PrincipalCollection subjectPrincipal, Collection<Permission> permissions ) throws AuthorizationException {}
    boolean hasRole( PrincipalCollection subjectPrincipal, String roleIdentifier ) { println "--- '$subjectPrincipal' has role '$roleIdentifier'?"; return true }
    boolean[] hasRoles( PrincipalCollection subjectPrincipal, List<String> roleIdentifiers ) { return roleIdentifiers.collect{ true } as boolean[] }
    boolean hasAllRoles( PrincipalCollection subjectPrincipal, Collection<String> roleIdentifiers ) { return true }
    void checkRole( PrincipalCollection subjectPrincipal, String roleIdentifier ) throws AuthorizationException { }
    void checkRoles( PrincipalCollection subjectPrincipal, Collection<String> roleIdentifiers ) throws AuthorizationException { }
    void onLogout( PrincipalCollection accountPrincipal ) {}
}
