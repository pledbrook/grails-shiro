package org.apache.shiro.grails;

import java.io.Serializable;
import java.net.InetAddress;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authz.AuthorizationException;
import org.apache.shiro.authz.Permission;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.session.InvalidSessionException;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.subject.Subject;

/**
 * An empty implementation of the Shiro SecurityManager interface. It
 * is used in a hack to get realm reloading working properly. See the
 * ShiroGrailsPlugin code for more details.
 *
 * @author Peter Ledbrook
 */
public class DummySecurityManager implements SecurityManager {
    public Subject createSubject(Map arg0) { return null; }
    public Subject getSubject() { return null; }
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
    public void checkValid(Serializable arg0) throws InvalidSessionException { } 
    public Object getAttribute(Serializable arg0, Object arg1) throws InvalidSessionException { return null; }
    public Collection<Object> getAttributeKeys(Serializable arg0) { return null; }
    public InetAddress getHostAddress(Serializable arg0) { return null; }
    public Date getLastAccessTime(Serializable arg0) { return null; }
    public Date getStartTimestamp(Serializable arg0) { return null; }
    public long getTimeout(Serializable arg0) throws InvalidSessionException { return 0; }
    public boolean isValid(Serializable arg0) { return false; }
    public Object removeAttribute(Serializable arg0, Object arg1) throws InvalidSessionException { return null; }
    public void setAttribute(Serializable arg0, Object arg1, Object arg2) throws InvalidSessionException { }
    public void setTimeout(Serializable arg0, long arg1) throws InvalidSessionException { }
    public Serializable start(InetAddress arg0) throws AuthorizationException { return null; }
    public Serializable start(Map arg0) throws AuthorizationException { return null; }
    public void stop(Serializable arg0) throws InvalidSessionException { }
    public void touch(Serializable arg0) throws InvalidSessionException { }
}
