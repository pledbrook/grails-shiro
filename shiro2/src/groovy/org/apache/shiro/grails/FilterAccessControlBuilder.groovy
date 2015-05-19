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

package org.apache.shiro.grails

import org.apache.shiro.subject.Subject
import org.apache.shiro.authz.Permission

class FilterAccessControlBuilder {
    private Subject subject

    FilterAccessControlBuilder(Subject subject) {
        this.subject = subject
    }

    /**
     * Checks whether the user associated with the builder's security
     * context has the given role or not.
     */
    boolean role(roleName) {
        return this.subject.hasRole(roleName.toString())
    }

    /**
     * Checks whether the user associated with the builder's security
     * context has the given permission or not.
     */
    boolean permission(Permission permission) {
        return this.subject.isPermitted(permission)
    }

    /**
     * <p>Checks whether the user associated with the builder's security
     * context has the given permission or not. The permission is a
     * string that complies with the format supported by Shiro's
     * WildcardPermission, i.e. parts separated by a colon and sub-parts
     * separated by commas. For example, you might have
     * "book:*:view,create,save", where the first part is a type of
     * resource (a "book"), the second part is the ID of the resource
     * ("*" means "all books"), and the last part is a list of actions
     * (sub-parts).</p>
     * <p>The string can contain any number of parts and sub-parts
     * because it is not interpreted by the framework at all. The parts
     * and sub-parts only mean something to the application. The only
     * time the framework effectively "interprets" the strings is when
     * it checks whether one permission implies the other, but this only
     * relies on the logic of parts and sub-parts, not their semantic
     * meaning in the application. See the documentation for Shiro's
     * WildcardPermission for more information.
     */
    boolean permission(String permissionString) {
        return this.subject.isPermitted(permissionString)
    }

    /**
     * <p>Checks whether the user associated with the builder's security
     * context has permission for a given type and set of actions. The
     * map must have 'type' and 'actions' entries. The method should be
     * called like this:</p>
     * <pre>
     *     permission(type: 'profile', actions: 'edit')
     *     permission(type: 'book', actions: [ 'show', 'modify' ])
     *     permission(type: 'book', actions: 'show, modify')
     * </pre>
     */
    boolean permission(Map args) {
        // First check that the argument map has the required entries.
        if (!args['type']) {
            throw new IllegalArgumentException("The 'type' parameter must be provided to 'permission()'")
        }
        else if (!(args['type'] instanceof String)) {
            throw new IllegalArgumentException("The 'type' parameter must be a string.")
        }

        if (!args['actions']) {
            throw new IllegalArgumentException("The 'actions' parameter must be provided to 'permission()'")
        }
        else if (!(args['actions'] instanceof String || args['actions'] instanceof Collection)) {
            throw new IllegalArgumentException("The 'actions' parameter must be a string or a collection of strings.")
        }

        // Create a new permission from the given parameters.
        def p = new ShiroBasicPermission(args['type'], args['actions'])

        // Check whether the currently authenticated user has the
        // permission.
        return this.subject.isPermitted(p)
    }
}
