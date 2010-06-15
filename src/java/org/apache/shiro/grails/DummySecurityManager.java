package org.apache.shiro.grails;

import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authz.AuthorizationException;
import org.apache.shiro.authz.Permission;
import org.apache.shiro.session.Session;
import org.apache.shiro.session.SessionException;
import org.apache.shiro.session.mgt.SessionContext;
import org.apache.shiro.session.mgt.SessionKey;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.subject.SubjectContext;
import org.apache.shiro.web.mgt.WebSecurityManager;

import java.util.Collection;
import java.util.List;

/**
 * An empty implementation of the Shiro SecurityManager interface. It
 * is used in a hack to get realm reloading working properly. See the
 * ShiroGrailsPlugin code for more details.
 *
 * @author Peter Ledbrook
 */
public class DummySecurityManager implements WebSecurityManager {
    public Subject createSubject(SubjectContext arg0) { return null; }
    public Subject login(Subject arg0, AuthenticationToken arg1) throws AuthenticationException { return null; }
    public void logout(Subject arg0) { }
    public AuthenticationInfo authenticate(AuthenticationToken arg0) throws AuthenticationException { return null; }
    public void checkPermission(PrincipalCollection arg0, String arg1) throws AuthorizationException { }
    public void checkPermission(PrincipalCollection arg0, Permission arg1) throws AuthorizationException { }
    public void checkPermissions(PrincipalCollection arg0, String... arg1) throws AuthorizationException { }
    public void checkPermissions(PrincipalCollection arg0, Collection<Permission> arg1) throws AuthorizationException { }
    public void checkRole(PrincipalCollection arg0, String arg1) throws AuthorizationException { }
    public void checkRoles(PrincipalCollection arg0, Collection<String> arg1) throws AuthorizationException { }
    public boolean hasAllRoles(PrincipalCollection arg0, Collection<String> arg1) { return false; }
    public boolean hasRole(PrincipalCollection arg0, String arg1) { return false; }
    public boolean[] hasRoles(PrincipalCollection arg0, List<String> arg1) { return null; }
    public boolean isPermitted(PrincipalCollection arg0, String arg1) { return false; }
    public boolean isPermitted(PrincipalCollection arg0, Permission arg1) { return false; }
    public boolean[] isPermitted(PrincipalCollection arg0, String... arg1) { return null; }
    public boolean[] isPermitted(PrincipalCollection arg0, List<Permission> arg1) { return null; }
    public boolean isPermittedAll(PrincipalCollection arg0, String... arg1) { return false; }
    public boolean isPermittedAll(PrincipalCollection arg0, Collection<Permission> arg1) { return false; }
    public Session start(SessionContext sessionContext) { return null; }
    public Session getSession(SessionKey sessionKey) throws SessionException { return null; }
    public boolean isHttpSessionMode() { return false; }
}
