/*
 * Copyright 2007 Peter Ledbrook.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use file except in compliance with the License.
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

import grails.test.mixin.TestFor
import org.apache.shiro.SecurityUtils
import org.apache.shiro.subject.PrincipalCollection
import org.apache.shiro.subject.Subject
import org.codehaus.groovy.grails.web.taglib.exceptions.GrailsTagException

/**
 * Test case for {@link ShiroTagLib}.
 */
@TestFor(ShiroTagLib)
class ShiroTagLibTests {
    private Map mockSubject
    private Map savedMetaClasses

    void setUp() {

        // A map that backs the mock Subject instance.
        mockSubject = [
                isAuthenticated: { -> false },
                isPermitted    : { Object -> true }
        ]

        SecurityUtils.metaClass.static.getSubject = { -> mockSubject as Subject }

    }

    void testIsLoggedIn() {

        assert "" == applyTemplate('<shiro:isLoggedIn>You are logged in!</shiro:isLoggedIn>')

        mockSubject["isAuthenticated"] = { -> true }
        assert "You are logged in!" == applyTemplate('<shiro:isLoggedIn>You are logged in!</shiro:isLoggedIn>')
    }

    void testIsNotLoggedIn() {

        assert "You are not logged in!" == applyTemplate('<shiro:isNotLoggedIn>You are not logged in!</shiro:isNotLoggedIn>')

        mockSubject["isAuthenticated"] = { -> true }
        assert "" == applyTemplate('<shiro:isNotLoggedIn>You are not logged in!</shiro:isNotLoggedIn>')
    }

    void testUser() {
        // Local setup.
        mockSubject["getPrincipal"] = { -> null }

        // Now execute the tag and check the output that it generates.
        assert "" == applyTemplate('<shiro:user>blah</shiro:user>')

        // Now try with a 'remembered' user, i.e. the principal is not
        // null, but the user is not authenticated.
        mockSubject["getPrincipal"] = { -> "admin" }
        assert "blah" == applyTemplate('<shiro:user>blah</shiro:user>')

        // Finally try with an authenticated user.
        // TODO this doesn't really test anything more that the previous test. Remove?
        mockSubject["isAuthenticated"] = { -> true }
        assert "blah" == applyTemplate('<shiro:user>blah</shiro:user>')
    }

    void testNotUser() {
        mockSubject["getPrincipal"] = { -> null }

        // Now execute the tag and check the output that it generates.
        assert "blah" == applyTemplate('<shiro:notUser>blah</shiro:notUser>')

        // Now try with a 'remembered' user, i.e. the principal is not
        // null, but the user is not authenticated.
        mockSubject["getPrincipal"] = { -> "admin" }
        assert "" == applyTemplate('<shiro:notUser>blah</shiro:notUser>')

        // Finally try with an authenticated user.
        // TODO this doesn't really test anything more that the previous test. Remove?
        mockSubject["isAuthenticated"] = { -> true }
        assert "" == applyTemplate('<shiro:notUser>blah</shiro:notUser>')
    }

    void testRemembered() {
        mockSubject["getPrincipal"] = { -> null }

        // Now execute the tag and check the output that it generates.
        assert "" == applyTemplate('<shiro:remembered>blah</shiro:remembered>')

        // Now try with a 'remembered' user, i.e. the principal is not
        // null, but the user is not authenticated.
        mockSubject["getPrincipal"] = { -> "admin" }
        assert "blah" == applyTemplate('<shiro:remembered>blah</shiro:remembered>')

        // Finally try with an authenticated user.
        mockSubject["isAuthenticated"] = { -> true }
        assert "" == applyTemplate('<shiro:remembered>blah</shiro:remembered>')
    }

    void testNotRemembered() {
        // Local setup.
        mockSubject["getPrincipal"] = { -> null }

        // Now execute the tag and check the output that it generates.
        assert "blah" == applyTemplate('<shiro:notRemembered>blah</shiro:notRemembered>')

        // Now try with a 'remembered' user, i.e. the principal is not
        // null, but the user is not authenticated.
        mockSubject["getPrincipal"] = { -> "admin" }
        assert "" == applyTemplate('<shiro:notRemembered>blah</shiro:notRemembered>')

        // Finally try with an authenticated user.
        mockSubject["isAuthenticated"] = { -> true }
        assert "blah" == applyTemplate('<shiro:notRemembered>blah</shiro:notRemembered>')
    }

    void testPrincipal() {
        mockSubject["getPrincipal"] = { -> null }

        // Now execute the tag and check the output that it generates.
        assert "" == applyTemplate('<shiro:principal/>')

        // Now try with a 'remembered' user, i.e. the principal is not
        // null, but the user is not authenticated.
        mockSubject["getPrincipal"] = { -> 'admin' }
        assert "admin" == applyTemplate('<shiro:principal/>')
    }

    void testPrincipalEncoding() {
        // Local setup.
        mockSubject["getPrincipal"] = { -> null }

        // Now execute the tag and check the output that it generates.
        assert "" == applyTemplate('<shiro:principal/>')

        // Now try with a 'remembered' user, i.e. the principal is not
        // null, but the user is not authenticated.
        mockSubject["getPrincipal"] = { -> "<admin>" }
        assert "&lt;admin&gt;" == applyTemplate('<shiro:principal/>')
    }

    void testPrincipalWithType() {
        // Local setup.
        def subjectControl = mockFor(Subject, true)
        def principalCollectionControl = mockFor(PrincipalCollection, true)

        subjectControl.demand.getPrincipal(0..4) { -> null }
        principalCollectionControl.demand.oneByType(0..2) { Class clazz ->
            //we only have a String type
            if (clazz == String) return "admin"
            return null
        }
        subjectControl.demand.getPrincipals(0..4) { -> principalCollectionControl.createMock() }
        SecurityUtils.metaClass.static.getSubject = { -> subjectControl.createMock() }

        assert "" == applyTemplate('<shiro:principal type="java.lang.Integer"/>')
        assert "admin" == applyTemplate('<shiro:principal type="java.lang.String"/>')
    }

    void testHasPermissionWithGString() {
        def id = "1"
        def testPermission = "user:manage:${id}"

        mockSubject["isPermitted"] = { String permName -> testPermission == permName }

        assert "yup" == applyTemplate('<shiro:hasPermission permission="user:manage:1">yup</shiro:hasPermission>')
        assert "" == applyTemplate('<shiro:hasPermission permission="user:manage:10">yup</shiro:hasPermission>')
    }

    void testHasRole() {
        //User has role User
        mockSubject["hasRole"] = { String name -> name == "User" }

        //Does user have Administrator role? no.
        assert "" == applyTemplate('<shiro:hasRole name="Administrator">yup</shiro:hasRole>')

        //Does user have User role? yes.
        assert "yup" == applyTemplate('<shiro:hasRole name="User">yup</shiro:hasRole>')

        // Check that the 'name' attribute is required.
        try {
            applyTemplate('<shiro:hasRole>yup</shiro:hasRole>')
            fail("Should have thrown a tag exception.")
        }
        catch (GrailsTagException ex) {
            // The exception message should contain the correct tag name.
            assertTrue ex.message.contains("hasRole")
        }
    }

    void testLacksRole() {
        //User has role Administrator
        mockSubject["hasRole"] = { String name -> name == "Administrator" }

        //Does user lack Administrator role? no.
        assert "" == applyTemplate('<shiro:lacksRole name="Administrator">yup</shiro:lacksRole>')

        //Does user lack User role? yes.
        assert "yup" == applyTemplate('<shiro:lacksRole name="User">yup</shiro:lacksRole>')

        // Check that the 'name' attribute is required.
        try {
            applyTemplate('<shiro:lacksRole>yup</shiro:lacksRole>')
            fail("Should have thrown a tag exception.")
        }
        catch (GrailsTagException ex) {
            // The exception message should contain the correct tag name.
            assertTrue ex.message.contains("lacksRole")
        }
    }

    void testHasAllRoles() {
        mockSubject["hasAllRoles"] = { List roles -> roles.containsAll(["Administrator", "User"]) }

        //Is user a User and Spy? no
        assert "" == applyTemplate('<shiro:hasAllRoles in="${testRoles}">yup</shiro:hasAllRoles>', [testRoles: ["User", "Spy"]])

        //Is user a User and Administrator? yes
        assert "yup" == applyTemplate('<shiro:hasAllRoles in="${testRoles}">yup</shiro:hasAllRoles>', [testRoles: ["User", "Administrator"]])

        //Is user a Administrator? yes
        assert "" == applyTemplate('<shiro:hasAllRoles in="${testRoles}">yup</shiro:hasAllRoles>', [testRoles: ["Administrator"]])

        // Check that the 'in' attribute is required.
        shouldFail(GrailsTagException) {
            applyTemplate('<shiro:hasAllRoles ">yup</shiro:hasAllRoles>')
        }
    }

    void testLacksAnyRole() {
        mockSubject["hasAllRoles"] = { List roles -> roles.containsAll(["Administrator", "User"]) }

        // First test the case where the user has all the roles.
        assert "" == applyTemplate('<shiro:lacksAnyRole in="${testRoles}">yup</shiro:lacksAnyRole>', [testRoles: ["User", "Administrator"]])

        // Now try the case where the user is missing at least one of them
        assert "yup" == applyTemplate('<shiro:lacksAnyRole in="${testRoles}">yup</shiro:lacksAnyRole>', [testRoles: ["User", "spy"]])

        // Check that the 'in' attribute is required.
        shouldFail(GrailsTagException) {
            applyTemplate('<shiro:lacksAnyRole ">yup</shiro:lacksAnyRole>')
        }
    }

    void testHasAnyRole() {
        mockSubject["hasRoles"] = { List roles ->
            roles.collect { it == "User" } as boolean[]//user has the role User only
        }

        // First test the case where the user has none of the roles.
        assert "" == applyTemplate('<shiro:hasAnyRole in="${testRoles}">yup</shiro:hasAnyRole>', [testRoles: ["Administrator", "Spy", "Warlock"]])

        // Now try the case where the user has at least one of them.
        assert "yup" == applyTemplate('<shiro:hasAnyRole in="${testRoles}">yup</shiro:hasAnyRole>', [testRoles: ["Administrator", "User", "Warlock"]])

        // Check that the 'in' attribute is required.
        shouldFail(GrailsTagException) {
            applyTemplate('<shiro:hasAnyRole>yup</shiro:hasAnyRole>')
        }
    }

    void testLacksAllRoles() {
        mockSubject["hasRoles"] = { List roles ->
            roles.collect {
                it == "User" || it == "Administrator"
            } as boolean[]//user has the User and Administrator roles
        }

        // First test the case where the user has at least one of the roles.
        assert "" == applyTemplate('<shiro:lacksAllRoles in="${testRoles}">yup</shiro:lacksAllRoles>', [testRoles: ["Administrator", "User", "Warlock"]])

        // Now try the case where the user has none of them.
        assert "yup" == applyTemplate('<shiro:lacksAllRoles in="${testRoles}">yup</shiro:lacksAllRoles>', [testRoles: ["Loozer", "Spy", "Warlock"]])

        // Check that the 'in' attribute is required.
        shouldFail(GrailsTagException) {
            applyTemplate('<shiro:lacksAllRoles>yup</shiro:lacksAllRoles>')
        }
    }
}
