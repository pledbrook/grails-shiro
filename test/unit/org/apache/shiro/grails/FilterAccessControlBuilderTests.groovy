package org.apache.shiro.grails

import junit.framework.Assert
import org.apache.shiro.authz.Permission
import org.apache.shiro.subject.Subject
import org.apache.shiro.authz.permission.WildcardPermission

/**
 * Test case for {@link FilterAccessControlBuilder}.
 */
class FilterAccessControlBuilderTests extends GroovyTestCase {
    /**
     * Tests the simple case where 'role()' is called with a single
     * string argument.
     */
    void testRole() {
        def hasRoleCalled = false

        def mockSubject = [
            hasRole: { String role ->
                hasRoleCalled = true
                Assert.assertEquals('Incorrect role checked.', 'Administrator', role)
                return true
            }
        ] as Subject

        def testBuilder = new FilterAccessControlBuilder(mockSubject)

        assertTrue('Access control returned wrong result.', testBuilder.role('Administrator'))
        assertTrue('hasRole() not called.', hasRoleCalled)
    }

    /**
     * Tests the case where the user does not have the required role.
     */
    void testRoleNotPossessed() {
        def hasRoleCalled = false

        def mockSubject = [
            hasRole: { role ->
                hasRoleCalled = true
                Assert.assertEquals('Incorrect role checked.', 'Observer', role)
                return false
            }
        ] as Subject

        def testBuilder = new FilterAccessControlBuilder(mockSubject)

        assertFalse('Access control returned wrong result.', testBuilder.role('Observer'))
        assertTrue('hasRole() not called.', hasRoleCalled)
    }

    /**
     * Tests the case where the given argument(s) is/are invalid.
     */
    void testRoleInvalidArg() {
        def hasRoleCalled = false

        def mockSubject = [
            hasRole: { String role ->
                hasRoleCalled = true
                Assert.assertEquals('Incorrect role checked.', 'Administrator', role)
                return true
            }
        ] as Subject

        def testBuilder = new FilterAccessControlBuilder(mockSubject)

        // Invalid argument type.
        shouldFail {
            testBuilder.role(target: 'Administrator')
        }

        // Invalid number of arguments.
        shouldFail {
            testBuilder.role('myRole', 'observer')
        }
    }

    /**
     * Tests the simple cases where 'permission()' is called with a
     * valid Permission instance.
     */
    void testPermission() {
        def isPermittedCalled = false
        def testPermission = new ShiroBasicPermission('profile', 'show')

        def mockSubject = [
            isPermitted: { Permission p ->
                isPermittedCalled = true
                Assert.assertEquals("Unexpected permission passed to 'isPermitted()'.", testPermission, p)
                return true
            }
        ] as Subject

        def testBuilder = new FilterAccessControlBuilder(mockSubject)

        assertTrue('Access control returned wrong result.', testBuilder.permission(testPermission))
        assertTrue('isPermitted() not called.', isPermittedCalled)

        // Now test the case where the user is not permitted.
        isPermittedCalled = false
        mockSubject = [
            isPermitted: { Permission p ->
                isPermittedCalled = true
                Assert.assertEquals("Unexpected permission passed to 'isPermitted()'.", testPermission, p)
                return false
            }
        ] as Subject

        testBuilder = new FilterAccessControlBuilder(mockSubject)

        assertFalse('Access control returned wrong result.', testBuilder.permission(testPermission))
        assertTrue('isPermitted() not called.', isPermittedCalled)
    }

    /**
     * Tests the simple cases where 'permission()' is called with a
     * string. In this case, a WildcardPermission should implicitly
     * be created.
     */
    void testPermissionWithString() {
        def isPermittedCalled = false
        def testString = "printer:hp4401:print"
        def testPermission = new WildcardPermission(testString)

        def mockSubject = [
            isPermitted: { Permission p ->
                isPermittedCalled = true
                Assert.assertEquals(
                        "Unexpected permission passed to 'isPermitted()'.",
                        [ ["printer"] as Set, ["hp4401"] as Set, ["print"] as Set ],
                        p.parts)
                return true
            }
        ] as Subject

        def testBuilder = new FilterAccessControlBuilder(mockSubject)

        assertTrue('Access control returned wrong result.', testBuilder.permission(testString))
        assertTrue('isPermitted() not called.', isPermittedCalled)

        // Now test the case where the user is not permitted.
        isPermittedCalled = false
        mockSubject = [
            isPermitted: { Permission p ->
                isPermittedCalled = true
                Assert.assertEquals(
                        "Unexpected permission passed to 'isPermitted()'.",
                        [ ["printer"] as Set, ["hp4401"] as Set, ["print"] as Set ],
                        p.parts)
                return false
            }
        ] as Subject

        testBuilder = new FilterAccessControlBuilder(mockSubject)

        assertFalse('Access control returned wrong result.', testBuilder.permission(testString))
        assertTrue('isPermitted() not called.', isPermittedCalled)
    }

    /**
     * Tests that 'permission()' can be called with an argument map
     * (named arguments).
     */
    void testPermissionWithMap() {
        def isPermittedCalled = false
        def expectedPermission = new ShiroBasicPermission('profile', 'show')

        def mockSubject = [
            isPermitted: { Permission p ->
                isPermittedCalled = true
                Assert.assertEquals("Unexpected permission passed to 'isPermitted()'.", expectedPermission, p)
                return true
            }
        ] as Subject

        def testBuilder = new FilterAccessControlBuilder(mockSubject)

        assertTrue('Access control returned wrong result.', testBuilder.permission(type: 'profile', actions: 'show'))
        assertTrue('isPermitted() not called.', isPermittedCalled)

        // Now test the case where the 'actions' arg is a list.
        isPermittedCalled = false
        expectedPermission = new ShiroBasicPermission('book', 'show, edit')

        mockSubject = [
            isPermitted: { Permission p ->
                isPermittedCalled = true
                Assert.assertEquals("Unexpected permission passed to 'isPermitted()'.", expectedPermission, p)
                return true
            }
        ] as Subject

        testBuilder = new FilterAccessControlBuilder(mockSubject)

        assertTrue(
                'Access control returned wrong result.',
                testBuilder.permission(type: 'book', actions: [ 'show', 'edit' ]))
        assertTrue('isPermitted() not called.', isPermittedCalled)
    }

    /**
     * Tests that invalid arguments to permission(Map) are handled
     * correctly - usually by the method throwing an IllegalArgumentException.
     */
    void testPermissionWithInvalidArgs() {
        def isPermittedCalled = false
        def expectedPermission = new ShiroBasicPermission('profile', 'show')

        def mockSubject = [
            isPermitted: { Permission p ->
                isPermittedCalled = true
                Assert.assertEquals("Unexpected permission passed to 'isPermitted()'.", expectedPermission, p)
                return true
            }
        ] as Subject

        def testBuilder = new FilterAccessControlBuilder(mockSubject)

        // Test an empty map.
        shouldFail(IllegalArgumentException) {
            testBuilder.permission([:])
        }

        // Test a missing 'type' argument
        shouldFail(IllegalArgumentException) {
            testBuilder.permission(target: 'test', actions: 'show')
        }

        // Test a missing 'actions' argument.
        shouldFail(IllegalArgumentException) {
            testBuilder.permission(type: 'book')
        }

        // Test a non-string 'type'.
        shouldFail(IllegalArgumentException) {
            testBuilder.permission(type: ['profile'], actions: 'test')
        }

        // Test a non-string or non-collection 'actions' arg.
        shouldFail(IllegalArgumentException) {
            testBuilder.permission(type: 'profile', actions: 10)
        }
    }

    void testMultipleControls1() {
        def hasRoleCalled = false
        def isPermittedCalled = false
        def expectedPermission = new ShiroBasicPermission('project', 'edit')

        def mockSubject = [
            hasRole: { String role ->
                hasRoleCalled = true
                Assert.assertEquals("Incorrect role checked.", 'Administrator', role)
                return false
            },

            isPermitted: { Permission p ->
                isPermittedCalled = true
                Assert.assertEquals("Unexpected permission passed to 'isPermitted()'.", expectedPermission, p)
                return true
            }
        ] as Subject

        def b = new FilterAccessControlBuilder(mockSubject)
        def accessControl = {
            role('Administrator') || permission(type: 'project', actions: 'edit')
        }
        accessControl.delegate = b
        assertTrue(accessControl())
    }

    void testMultipleControls2() {
        def hasRoleCalled = false
        def isPermittedCalled = false
        def expectedPermission = new ShiroBasicPermission('project', 'edit')

        def mockSubject = [
            hasRole: { String role ->
                hasRoleCalled = true
                Assert.assertEquals("Incorrect role checked.", 'Administrator', role)
                return true
            },

            isPermitted: { Permission p ->
                isPermittedCalled = true
                Assert.assertEquals("Unexpected permission passed to 'isPermitted()'.", expectedPermission, p)
                return true
            }
        ] as Subject

        def b = new FilterAccessControlBuilder(mockSubject)
        def accessControl = {
            role('Administrator') && permission(type: 'project', actions: 'edit')
        }
        accessControl.delegate = b
        assertTrue(accessControl())
    }

    void testMultipleControls3() {
        def hasRoleCalled = false
        def isPermittedCalled = false
        def expectedPermission = new ShiroBasicPermission('project', 'edit')

        def mockSubject = [
            hasRole: { String role ->
                hasRoleCalled = true
                Assert.assertEquals("Incorrect role checked.", 'Administrator', role)
                return true
            },

            isPermitted: { Permission p ->
                isPermittedCalled = true
                Assert.assertEquals("Unexpected permission passed to 'isPermitted()'.", expectedPermission, p)
                return false
            }
        ] as Subject

        def b = new FilterAccessControlBuilder(mockSubject)
        def accessControl = {
            role('Administrator') && permission(type: 'project', actions: 'edit')
        }
        accessControl.delegate = b
        assertFalse(accessControl())
    }
}
