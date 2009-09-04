class MainFunctionalTests extends org.example.functional.TestSupport {
    void testAccessControl() {
        get "/book/list"
        verifyLoginPage  ""

        login "dilbert", "password", "/book/list"
        assertContentContains "Book List"
        get "/book/create"
        assertContentContains "Create Book"
        get "/book/show/1"
        assertContentContains "Show Book"
        get "/book/edit/1"
        verifyUnauthorised()
        post "/book/delete/1"
        verifyUnauthorised()
        logout()

        login "unknown", ""
        verifyLoginPage  "unknown", true

        login "admin", "", "/book/list"
        assertContentContains "Book List"
        get "/book/create"
        assertContentContains "Create Book"
        get "/book/show/1"
        assertContentContains "Show Book"
        get "/book/edit/1"
        assertContentContains "Edit Book"
        post "/book/delete/1"
        assertContentContains "Book List"
        assertContentContains "deleted"
        logout()
    }
}
