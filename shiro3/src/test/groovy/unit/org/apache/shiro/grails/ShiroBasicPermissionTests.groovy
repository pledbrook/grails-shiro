package org.apache.shiro.grails

import org.apache.shiro.authz.Permission

/**
 * Test case for {@link ShiroBasicPermission}.
 */
class ShiroBasicPermissionTests extends GroovyTestCase {
    /**
     * Tests that {@link ShiroBasicPermission#getActions()} returns
     * the expected values with permission instances created via
     * different constructor arguments.
     */
    void testActionsSet() {
        def testPermission = new ShiroBasicPermission('book', [ 'view', 'modify', 'create', 'delete' ])
        assert testPermission.actions == [ 'view', 'modify', 'create', 'delete' ] as Set
        assert testPermission.target == 'book'

        testPermission = new ShiroBasicPermission('person', 'view, modify, create, delete')
        assert testPermission.actions == [ 'view', 'modify', 'create', 'delete' ] as Set
        assert testPermission.target == 'person'

        testPermission = new ShiroBasicPermission('person', 'view;create;    modify')
        assert testPermission.actions == [ 'view', 'modify', 'create' ] as Set

        testPermission = new ShiroBasicPermission('person', 'sit')
        assert testPermission.actions == [ 'sit' ] as Set

        testPermission = new ShiroBasicPermission('person', [ 'sit' ])
        assert testPermission.actions == [ 'sit' ] as Set
    }

    /**
     * Tests that {@link ShiroBasicPermission#getActionsString()} returns
     * the expected standard string with permission instances created
     * via different constructor arguments.
     */
    void testActionsString() {
        def testPermission = new ShiroBasicPermission('book', [ 'view', 'modify', 'create', 'delete' ])
        def splitString = Arrays.asList(testPermission.actionsString.split(/,/)) as Set
        assert splitString == [ 'view', 'modify', 'create', 'delete' ] as Set
        assert testPermission.target == 'book'

        testPermission = new ShiroBasicPermission('person', 'view, modify, create, delete')
        splitString = Arrays.asList(testPermission.actionsString.split(/,/)) as Set
        assert splitString == [ 'view', 'modify', 'create', 'delete' ] as Set
        assert testPermission.target == 'person'

        testPermission = new ShiroBasicPermission('person', 'view;create;    modify')
        splitString = Arrays.asList(testPermission.actionsString.split(/,/)) as Set
        assert splitString == [ 'view', 'modify', 'create' ] as Set

        testPermission = new ShiroBasicPermission('person', 'sit')
        assert testPermission.actionsString == 'sit'

        testPermission = new ShiroBasicPermission('person', [ 'sit' ])
        assert testPermission.actionsString == 'sit'
    }

    /**
     * Tests that {@link ShiroBasicPermission#implies(Permission p)
     * returns the correct value when used to compare a variety of
     * different permissions.
     */
    void testImplies() {
        def requiredPermission = new ShiroBasicPermission('book', [ 'delete' ])

        def testPermission = new ShiroBasicPermission('book', 'delete')
        assert testPermission.implies(requiredPermission)

        testPermission = new ShiroBasicPermission('shell', 'delete')
        assert !testPermission.implies(requiredPermission)

        testPermission = new ShiroBasicPermission('*', 'delete')
        assert testPermission.implies(requiredPermission)

        testPermission = new ShiroBasicPermission('book', 'view, modify, create, delete')
        assert testPermission.implies(requiredPermission)

        testPermission = new ShiroBasicPermission('book', '*')
        assert testPermission.implies(requiredPermission)

        testPermission = new ShiroBasicPermission('shell', '*')
        assert !testPermission.implies(requiredPermission)

        testPermission = new ShiroBasicPermission('*', '*')
        assert testPermission.implies(requiredPermission)

        requiredPermission = [ name: 'book', actions: [ 'delete' ] as Set ] as Permission

        testPermission = new ShiroBasicPermission('book', 'delete')
        assert !testPermission.implies(requiredPermission)

        requiredPermission = new ShiroBasicPermission('book', [ 'view', 'create', 'edit' ])

        testPermission = new ShiroBasicPermission('book', '*')
        assert testPermission.implies(requiredPermission)

        testPermission = new ShiroBasicPermission('book', 'view')
        assert !testPermission.implies(requiredPermission)

        testPermission = new ShiroBasicPermission('book', 'view, create,edit,delete')
        assert testPermission.implies(requiredPermission)
    }
}
