class MainFunctionalTests extends test.FunctionalTestSupport {
    /**
     * Tests the access control based on the custom realm.
     */
    void testAccessControl() {
        // Any user can log in with any password.
        login "test1", "test1", "/"
        assertTitle "Welcome to Grails"

        logout()
        login "dummy", ""
        assertTitle "Welcome to Grails"

        // The custom realm always allows access to role-protected
        // resources, so test that now.
        get "/book/create"
        assertTitle "Create Book"
        assertContentContains "Create Book"

        get "/book/edit/1"
        assertTitle "Edit Book"
        assertContentContains "Edit Book"

        // Conversely, the realm does not allow access to any resource
        // protected by one or more permissions.
        get "/book/list"
        assertContent "You do not have permission to access this page."
    }
}
