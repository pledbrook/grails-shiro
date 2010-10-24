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
 */
package org.apache.shiro.grails

import grails.test.GrailsUnitTestCase
import org.apache.shiro.SecurityUtils
import org.apache.shiro.subject.Subject
import org.codehaus.groovy.grails.plugins.codecs.HTMLCodec
import org.codehaus.groovy.grails.web.taglib.exceptions.GrailsTagException

/**
 * Test case for {@link ShiroTagLib}.
 */
class ShiroTagLibTests extends GrailsUnitTestCase {
    private ShiroTagLib tagLib
    private Map mockSubject
    private TagDelegate tagDelegate = new TagDelegate()

    private Map savedMetaClasses

    void setUp() {
        super.setUp()

        // Save the old meta classes for classes we will modify.
        def registry = GroovySystem.metaClassRegistry
        this.savedMetaClasses = [
                (SecurityUtils): registry.getMetaClass(SecurityUtils),
                (ShiroTagLib): registry.getMetaClass(ShiroTagLib)]

        // Now remove the meta classes from the registry so that our
        // additions do not affect the old meta classes.
        this.savedMetaClasses.each { Class cls, MetaClass mc ->
            registry.removeMetaClass(cls)
        }

        // Add the "throwTagError" method to the tag lib.
        ShiroTagLib.metaClass.throwTagError = { String message ->
            throw new GrailsTagException(message)
        }

        // A map that backs the mock Subject instance.
        this.mockSubject = [
            isAuthenticated: {-> false },
            isPermitted: {Object -> true }
        ]

        // Mock the 'getSubject()' method on SecurityUtils by overriding
        // it via ExpandoMetaClass. Note that this will create a new
        // ExpandoMetaClass because we have removed the old one from the
        // registry cache.
        SecurityUtils.metaClass.static.getSubject = {-> mockSubject as Subject }

        loadCodec(HTMLCodec)

        // Create the test tag library.
        this.tagLib = new ShiroTagLib()
    }
    
    void tearDown() {
        // Restore the old meta classes.
        this.savedMetaClasses.each { Class cls, MetaClass mc ->
            GroovySystem.metaClassRegistry.setMetaClass(cls, mc)
        }

        super.tearDown()
    }

    void testIsLoggedIn() {
        // Local setup.
        def testBody = "You are logged in!"
        this.tagLib.isLoggedIn.delegate = this.tagDelegate

        // Now execute the tag and check the output that it generates.
        this.tagLib.isLoggedIn([:], {-> testBody})
        assertEquals "", this.tagDelegate.output

        // Clear the output buffer and execute the tag again, but with
        // and 'authenticated' user.
        this.tagDelegate.reset()
        this.mockSubject["isAuthenticated"] = {-> true }
        this.tagLib.isLoggedIn([:], {-> testBody})
        assertEquals testBody, this.tagDelegate.output
    }

    void testIsNotLoggedIn() {
        // Local setup.
        def testBody = "You are not logged in!"
        this.tagLib.isNotLoggedIn.delegate = this.tagDelegate

        // Now execute the tag and check the output that it generates.
        this.tagLib.isNotLoggedIn([:], {-> testBody})
        assertEquals testBody, this.tagDelegate.output

        // Clear the output buffer and execute the tag again, but with
        // and 'authenticated' user.
        this.tagDelegate.reset()
        this.mockSubject["isAuthenticated"] = {-> true }
        this.tagLib.isNotLoggedIn([:], {-> testBody})
        assertEquals "", this.tagDelegate.output
    }

    void testUser() {
        // Local setup.
        def testBody = "Some output or other"
        this.mockSubject["getPrincipal"] = {-> null}
        this.tagLib.user.delegate = this.tagDelegate

        // Now execute the tag and check the output that it generates.
        this.tagLib.user([:], {-> testBody})
        assertEquals "", this.tagDelegate.output

        // Now try with a 'remembered' user, i.e. the principal is not
        // null, but the user is not authenticated.
        this.mockSubject["getPrincipal"] = {-> "admin"}
        this.tagDelegate.reset()
        this.tagLib.user([:], {-> testBody})
        assertEquals testBody, this.tagDelegate.output

        // Finally try with an authenticated user.
        this.mockSubject["isAuthenticated"] = {-> true}
        this.tagDelegate.reset()
        this.tagLib.user([:], {-> testBody})
        assertEquals testBody, this.tagDelegate.output
    }

    void testNotUser() {
        // Local setup.
        def testBody = "Some output or other"
        this.mockSubject["getPrincipal"] = {-> null}
        this.tagLib.notUser.delegate = this.tagDelegate

        // Now execute the tag and check the output that it generates.
        this.tagLib.notUser([:], {-> testBody})
        assertEquals testBody, this.tagDelegate.output

        // Now try with a 'remembered' user, i.e. the principal is not
        // null, but the user is not authenticated.
        this.mockSubject["getPrincipal"] = {-> "admin"}
        this.tagDelegate.reset()
        this.tagLib.notUser([:], {-> testBody})
        assertEquals "", this.tagDelegate.output

        // Finally try with an authenticated user.
        this.mockSubject["isAuthenticated"] = {-> true}
        this.tagDelegate.reset()
        this.tagLib.notUser([:], {-> testBody})
        assertEquals "", this.tagDelegate.output
    }

    void testRemembered() {
        // Local setup.
        def testBody = "Some output or other"
        this.mockSubject["getPrincipal"] = {-> null}
        this.tagLib.remembered.delegate = this.tagDelegate

        // Now execute the tag and check the output that it generates.
        this.tagLib.remembered([:], {-> testBody})
        assertEquals "", this.tagDelegate.output

        // Now try with a 'remembered' user, i.e. the principal is not
        // null, but the user is not authenticated.
        this.mockSubject["getPrincipal"] = {-> "admin"}
        this.tagDelegate.reset()
        this.tagLib.remembered([:], {-> testBody})
        assertEquals testBody, this.tagDelegate.output

        // Finally try with an authenticated user.
        this.mockSubject["isAuthenticated"] = {-> true}
        this.tagDelegate.reset()
        this.tagLib.remembered([:], {-> testBody})
        assertEquals "", this.tagDelegate.output
    }

    void testNotRemembered() {
        // Local setup.
        def testBody = "Some output or other"
        this.mockSubject["getPrincipal"] = {-> null}
        this.tagLib.notRemembered.delegate = this.tagDelegate

        // Now execute the tag and check the output that it generates.
        this.tagLib.notRemembered([:], {-> testBody})
        assertEquals testBody, this.tagDelegate.output

        // Now try with a 'remembered' user, i.e. the principal is not
        // null, but the user is not authenticated.
        this.mockSubject["getPrincipal"] = {-> "admin"}
        this.tagDelegate.reset()
        this.tagLib.notRemembered([:], {-> testBody})
        assertEquals "", this.tagDelegate.output

        // Finally try with an authenticated user.
        this.mockSubject["isAuthenticated"] = {-> true}
        this.tagDelegate.reset()
        this.tagLib.notRemembered([:], {-> testBody})
        assertEquals testBody, this.tagDelegate.output
    }

    void testPrincipal() {
        // Local setup.
        def principal = "admin"
        this.mockSubject["getPrincipal"] = {-> null}
        this.tagLib.principal.delegate = this.tagDelegate

        // Now execute the tag and check the output that it generates.
        this.tagLib.principal([:])
        assertEquals "", this.tagDelegate.output

        // Now try with a 'remembered' user, i.e. the principal is not
        // null, but the user is not authenticated.
        this.mockSubject["getPrincipal"] = {-> principal}
        this.tagDelegate.reset()
        this.tagLib.principal([:])
        assertEquals principal, this.tagDelegate.output
    }

    void testPrincipalEncoding() {
        // Local setup.
        def principal = "<admin>"
        this.mockSubject["getPrincipal"] = {-> null}
        this.tagLib.principal.delegate = this.tagDelegate

        // Now execute the tag and check the output that it generates.
        this.tagLib.principal([:])
        assertEquals "", this.tagDelegate.output

        // Now try with a 'remembered' user, i.e. the principal is not
        // null, but the user is not authenticated.
        this.mockSubject["getPrincipal"] = {-> principal}
        this.tagDelegate.reset()
        this.tagLib.principal([:])
        assertEquals "&lt;admin&gt;", this.tagDelegate.output
    }

    void testPrincipalWithType() {
        // Local setup.
        def principal = "admin"
        this.mockSubject["getPrincipalByType"] = {Class clazz -> null}
        this.tagLib.principal.delegate = this.tagDelegate

        // Now execute the tag and check the output that it generates.
//        this.tagLib.principal([:])
//        assertEquals "", this.tagDelegate.output

        // Now try with a 'remembered' user, i.e. the principal is not
        // null, but the user is not authenticated.
//        this.mockSubject["getPrincipal"] = {-> principal}
//        this.tagDelegate.reset()
//        this.tagLib.principal([:])
//        assertEquals principal, this.tagDelegate.output
    }

    void testHasPermissionWithGString() {
        def id = "1"
        def testPermission = "user:manage:${id}"
        def testBody = "I can manage this user!"

        this.tagLib.hasPermission.delegate = this.tagDelegate
        this.mockSubject["hasPermission"] = { String name -> assertEquals testPermission, name; true }

        this.tagLib.hasPermission(permission: testPermission) {-> testBody }
        assertEquals testBody, this.tagDelegate.output
    }
    
    void testHasRole() {
        String testBody = "I am an administrator!"
        String testRole = "Administrator"

        // First test the case where the user does not have the role.
        this.tagLib.hasRole.delegate = this.tagDelegate
        this.mockSubject["hasRole"] = { String name -> assertEquals testRole, name; false }

        this.tagLib.hasRole(name: testRole) {-> testBody }
        assertEquals "", this.tagDelegate.output

        // Now try the case where the user *does* have it.
        testRole = "User"
        this.mockSubject["hasRole"] = { String name -> assertEquals testRole, name; true }

        this.tagLib.hasRole(name: testRole) {-> testBody }
        assertEquals testBody, this.tagDelegate.output

        // Check that the 'name' attribute is required.
        try {
            this.tagLib.hasRole(dummy: "Administrator") {-> testBody }
            fail("Should have thrown a tag exception.")
        }
        catch (GrailsTagException ex) {
            // The exception message should contain the correct tag name.
            assertTrue ex.message.contains("hasRole")
        }
    }

    void testLacksRole() {
        String testBody = "I am an administrator!"
        String testRole = "Administrator"

        // First test the case where the user does not have the role.
        this.tagLib.lacksRole.delegate = this.tagDelegate
        this.mockSubject["hasRole"] = { String name -> assertEquals testRole, name; true }

        this.tagLib.lacksRole(name: testRole) {-> testBody }
        assertEquals "", this.tagDelegate.output

        // Now try the case where the user *does* have it.
        testRole = "User"
        this.mockSubject["hasRole"] = { String name -> assertEquals testRole, name; false }

        this.tagLib.lacksRole(name: testRole) {-> testBody }
        assertEquals testBody, this.tagDelegate.output

        // Check that the 'name' attribute is required.
        try {
            this.tagLib.lacksRole(dummy: "Administrator") {-> testBody }
            fail("Should have thrown a tag exception.")
        }
        catch (GrailsTagException ex) {
            // The exception message should contain the correct tag name.
            assertTrue ex.message.contains("lacksRole")
        }
    }

    void testHasAllRoles() {
        String testBody = "I am all things!"
        List testRoles = [ "Administrator", "Engineer", "User" ]

        // First test the case where the user does not have the roles.
        this.tagLib.hasAllRoles.delegate = this.tagDelegate
        this.mockSubject["hasAllRoles"] = { List roles -> assertEquals testRoles, roles; false }

        this.tagLib.hasAllRoles("in": testRoles) {-> testBody }
        assertEquals "", this.tagDelegate.output

        // Now try the case where the user *does* have them.
        this.mockSubject["hasAllRoles"] = { List roles -> assertEquals testRoles, roles; true }

        this.tagLib.hasAllRoles("in": testRoles) {-> testBody }
        assertEquals testBody, this.tagDelegate.output

        // Check that the 'in' attribute is required.
        shouldFail(GrailsTagException) {
            this.tagLib.hasAllRoles(name: "Administrator") {-> testBody }
        }
    }

    void testLacksAnyRole() {
        String testBody = "I am NOT all things!"
        List testRoles = [ "Administrator", "Engineer", "User" ]

        // First test the case where the user has all the roles.
        this.tagLib.lacksAnyRole.delegate = this.tagDelegate
        this.mockSubject["hasAllRoles"] = { List roles -> assertEquals testRoles, roles; true }

        this.tagLib.lacksAnyRole("in": testRoles) {-> testBody }
        assertEquals "", this.tagDelegate.output

        // Now try the case where the user is missing at least one of
        // them.
        this.mockSubject["hasAllRoles"] = { List roles -> assertEquals testRoles, roles; false }

        this.tagLib.lacksAnyRole("in": testRoles) {-> testBody }
        assertEquals testBody, this.tagDelegate.output

        // Check that the 'in' attribute is required.
        shouldFail(GrailsTagException) {
            this.tagLib.lacksAnyRole(name: "Administrator") {-> testBody }
        }
    }

    void testHasAnyRole() {
        String testBody = "I am some things!"
        List testRoles = [ "Administrator", "Engineer", "User" ]

        // First test the case where the user has none of the roles.
        this.tagLib.hasAnyRole.delegate = this.tagDelegate
        this.mockSubject["hasRoles"] = { List roles ->
            assertEquals testRoles, roles
            [ false, false, false ] as boolean[]
        }

        this.tagLib.hasAnyRole("in": testRoles) {-> testBody }
        assertEquals "", this.tagDelegate.output

        // Now try the case where the user has at least one of them.
        this.mockSubject["hasRoles"] = { List roles ->
            assertEquals testRoles, roles
            [ false, true, false ] as boolean[]
        }

        this.tagLib.hasAnyRole("in": testRoles) {-> testBody }
        assertEquals testBody, this.tagDelegate.output

        // Check that the 'in' attribute is required.
        shouldFail(GrailsTagException) {
            this.tagLib.hasAnyRole(name: "Administrator") {-> testBody }
        }
    }

    void testLacksAllRoles() {
        String testBody = "I am none of the things!"
        List testRoles = [ "Administrator", "Engineer", "User" ]

        // First test the case where the user has at least one of the
        // roles.
        this.tagLib.lacksAllRoles.delegate = this.tagDelegate
        this.mockSubject["hasRoles"] = { List roles ->
            assertEquals testRoles, roles
            [ false, true, true ] as boolean[]
        }

        this.tagLib.lacksAllRoles("in": testRoles) {-> testBody }
        assertEquals "", this.tagDelegate.output

        // Now try the case where the user has none of them.
        this.mockSubject["hasRoles"] = { List roles ->
            assertEquals testRoles, roles
            [ false, false, false ] as boolean[]
        }

        this.tagLib.lacksAllRoles("in": testRoles) {-> testBody }
        assertEquals testBody, this.tagDelegate.output

        // Check that the 'in' attribute is required.
        shouldFail(GrailsTagException) {
            this.tagLib.lacksAllRoles(name: "Administrator") {-> testBody }
        }
    }
}

/**
 * Use this as the delegate for tag closures when unit testing them.
 * It provides an 'out' property that you can then check.
 */
class TagDelegate {
    def out = new StringBuffer()

    String getOutput() {
        this.out.toString()
    }

    void reset() {
        this.out = new StringBuffer()
    }
}
