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
	@Override
	public Subject createSubject(Map arg0) { return null; }
	@Override
	public Subject getSubject() { return null; }
	@Override
	public Subject login(Subject arg0, AuthenticationToken arg1) throws AuthenticationException { return null; }
	@Override
	public void logout(Subject arg0) { }
	@Override
	public AuthenticationInfo authenticate(AuthenticationToken arg0) throws AuthenticationException { return null; }
	@Override
	public void checkPermission(PrincipalCollection arg0, String arg1) throws AuthorizationException { }
	@Override
	public void checkPermission(PrincipalCollection arg0, Permission arg1) throws AuthorizationException { }
	@Override
	public void checkPermissions(PrincipalCollection arg0, String... arg1) throws AuthorizationException { }
	@Override
	public void checkPermissions(PrincipalCollection arg0, Collection<Permission> arg1) throws AuthorizationException { }
	@Override
	public void checkRole(PrincipalCollection arg0, String arg1) throws AuthorizationException { }
	@Override
	public void checkRoles(PrincipalCollection arg0, Collection<String> arg1) throws AuthorizationException { }
	@Override
	public boolean hasAllRoles(PrincipalCollection arg0, Collection<String> arg1) { return false; }
	@Override
	public boolean hasRole(PrincipalCollection arg0, String arg1) { return false; }
	@Override
	public boolean[] hasRoles(PrincipalCollection arg0, List<String> arg1) { return null; }
	@Override
	public boolean isPermitted(PrincipalCollection arg0, String arg1) { return false; }
	@Override
	public boolean isPermitted(PrincipalCollection arg0, Permission arg1) { return false; }
	@Override
	public boolean[] isPermitted(PrincipalCollection arg0, String... arg1) { return null; }
	@Override
	public boolean[] isPermitted(PrincipalCollection arg0, List<Permission> arg1) { return null; }
	@Override
	public boolean isPermittedAll(PrincipalCollection arg0, String... arg1) { return false; }
	@Override
	public boolean isPermittedAll(PrincipalCollection arg0, Collection<Permission> arg1) { return false; }
	@Override
	public void checkValid(Serializable arg0) throws InvalidSessionException { } 
	@Override
	public Object getAttribute(Serializable arg0, Object arg1) throws InvalidSessionException { return null; }
	@Override
	public Collection<Object> getAttributeKeys(Serializable arg0) { return null; }
	@Override
	public InetAddress getHostAddress(Serializable arg0) { return null; }
	@Override
	public Date getLastAccessTime(Serializable arg0) { return null; }
	@Override
	public Date getStartTimestamp(Serializable arg0) { return null; }
	@Override
	public long getTimeout(Serializable arg0) throws InvalidSessionException { return 0; }
	@Override
	public boolean isValid(Serializable arg0) { return false; }
	@Override
	public Object removeAttribute(Serializable arg0, Object arg1) throws InvalidSessionException { return null; }
	@Override
	public void setAttribute(Serializable arg0, Object arg1, Object arg2) throws InvalidSessionException { }
	@Override
	public void setTimeout(Serializable arg0, long arg1) throws InvalidSessionException { }
	@Override
	public Serializable start(InetAddress arg0) throws AuthorizationException { return null; }
	@Override
	public Serializable start(Map arg0) throws AuthorizationException { return null; }
	@Override
	public void stop(Serializable arg0) throws InvalidSessionException { }
	@Override
	public void touch(Serializable arg0) throws InvalidSessionException { }
}
