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
package org.apache.shiro.grails;

import org.apache.shiro.realm.Realm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.authz.AuthorizationException;
import org.apache.shiro.authz.Permission;
import org.apache.shiro.authz.permission.InvalidPermissionStringException;
import org.apache.shiro.authz.permission.PermissionResolver;
import org.apache.shiro.authz.permission.PermissionResolverAware;
import org.apache.shiro.authz.permission.WildcardPermission;

import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;


/**
 * Adapter for the RealmWrapper which has problems implementing the
 * "String..." methods under JDK 1.5. So, these methods automatically
 * convert the strings into WildcardPermission instances.
 */
public abstract class RealmAdapter implements Realm, PermissionResolverAware {
    PermissionResolver permResolver = null;

    public void setPermissionResolver(PermissionResolver pr) {
        this.permResolver = pr;
    }

    public void checkPermission(PrincipalCollection principal, String s) throws AuthorizationException {
        checkPermission(principal, toPermission(s));
    }

    public void checkPermissions(PrincipalCollection principal, String... strings) throws AuthorizationException {
        checkPermissions(principal, toPermissionList(strings));
    }

    public boolean isPermitted(PrincipalCollection principal, String s) {
        return isPermitted(principal, toPermission(s));
    }

    public boolean[] isPermitted(PrincipalCollection principal, String... strings) {
        return isPermitted(principal, toPermissionList(strings));
    }

    public boolean isPermittedAll(PrincipalCollection principal, String... strings) {
        return isPermittedAll(principal, toPermissionList(strings));
    }

    public void checkRoles(PrincipalCollection principal, String... roles) throws AuthorizationException {
        checkRoles(principal, Arrays.asList(roles));
    }

    /**
     * Converts a single permission string into a Permission instances.
     */
    private Permission toPermission(String s) {
        if (permResolver == null) return null;
        try {
            return permResolver.resolvePermission(s);
        }
        catch (InvalidPermissionStringException ex) {
            // Nothing we can do about it
            return null; //@todo Is returning null the right thing to do?
        }
    }

    /**
     * Converts an array of string permissions into a list of
     * {@link WildcardPermission} instances.
     */
    private List toPermissionList(String[] strings) {
        List permissions = new ArrayList(strings.length);
        for (int i = 0; i < strings.length; i++) {
            permissions.add(toPermission(strings[i]));
        }

        return permissions;
    }
}
