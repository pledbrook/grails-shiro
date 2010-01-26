class MainFunctionalTests extends org.example.functional.TestSupport {
    void testLoginPage() {
        get "/auth/login"
        verifyLoginPage  ""

        login "admin", ""
        verifyLoginPage  "admin", true

        login "rodney", "sdkfjh"
        verifyLoginPage "rodney", true

        login "admin", "admin"
        assertTitle "Welcome to Grails"
        assertContentContains "Welcome to Grails"
        assertContentContains "Welcome back admin!"

        click "BookController"
        assertTitle "Book List"
        assertContentContains "Book List"
        assertContentContains "Logged in as: admin"
        assertEquals 3, byXPath("count(//tbody/tr)").toInteger()

        // This checks that the hasPermission tag is working OK.
        assertContentDoesNotContain "Comments"
        
        click "sign out"
        assertTitle "Welcome to Grails"
        assertContentContains "Welcome to Grails"
        assertContentDoesNotContain "Welcome back"

        logout()
    }

    void testBooks() {
        login "admin", "grrr", "/books"
        verifyLoginPage "admin", true

        login "admin", "admin"
        assertTitle "Book List"
        assertContentContains "Book List"
        assertEquals 3, byXPath("count(//tbody/tr)").toInteger()

        click "sign out"
        assertTitle "Welcome to Grails"

        // Try to log in as a user that does not have the admin
        // role. We need to check that all the book pages are
        // protected.
        login "dilbert", "password", "/books"
        verifyUnauthorised()
        get "/book/create"
        verifyUnauthorised()
        get "/book/edit"
        verifyUnauthorised()
        get "/book/save"
        verifyUnauthorised()
        get "/book/update"
        verifyUnauthorised()
        get "/book/delete"
        verifyUnauthorised()

        // Log out and try to access a URL that has a query string.
        logout()
        login "admin", "admin", "/books?sort=title&order=desc"

        // Check that the list of books is in the correct order
        // based on the query parameters.
        assertTitle "Book List"
        assertContentContains "Book List"
        assertEquals 3, byXPath("count(//tbody/tr)").toInteger()
        assertEquals "Misery", byXPath("//tbody/tr[1]/td[2]").textContent 
        assertEquals "Guns, Germs, and Steel", byXPath("//tbody/tr[2]/td[2]").textContent
        assertEquals "Colossus", byXPath("//tbody/tr[3]/td[2]").textContent

        // Make sure the admin user has access to all pages.
        get "/book/create"
        assertContentContains "Create Book"
        get "/book/edit/1"
        assertContentContains "Edit Book"

        logout()
    }

    void testSessionTimeOutOnPost() {
        post("/book/save") {
            title "The Shining"
            author "Stephen King"
        }
        login "admin", "admin"

        get "/book/list"
        assertTitle "Book List"
        assertEquals 4, byXPath("count(//tbody/tr)").toInteger()
        assertEquals "The Shining", byXPath("//tbody/tr[4]/td[2]").textContent 

    }

    void testPermissions() {
        // Check the JsecBasicPermission access control.
        login "test1", "test1", "/test/create"
        assertContent "Create page"

        get "/test/edit"
        verifyUnauthorised()

        get "/test/delete"
        verifyUnauthorised()

        // Those are the controller actions. Now test the hasPermission
        // tag.
        get "/test/index"
        assertContentContainsStrict "Comments"
        assertContentContainsStrict "Edit a user comment"
        assertContentDoesNotContain "Tags"

        // Now try with a different user.
        logout()
        login "test2", "test2", "/test/create"
        verifyUnauthorised()

        get "/test/edit"
        assertContent "Edit page"

        get "/test/delete"
        assertContent "Delete page"

        // Those are the controller actions. Now test the hasPermission
        // tag.
        get "/test/index"
        assertContentContainsStrict "Comments"
        assertContentDoesNotContain "Edit a user comment"
        assertContentContainsStrict "Tags"
        
        logout()
    }

    /**
     * Tests that tag errors display the correct tag name.
     */
    void testTagErrors() {
        login "test1", "test1", "/test/hasRole"
        assertStatus 500
        assertContentContains "Tag [hasRole]"

        get "/test/lacksRole"
        assertStatus 500
        assertContentContains "Tag [lacksRole]"

        logout()
    }

    /**
     * Tests that the "permission(String)" access control method works
     * as it should.
     */
    void testWildcardPermissions() {
        // Check that user test1 has access to the wildcard "index"
        // and "one" actions, but not the "two" action.
        login "test1", "test1", "/wildcard/index"
        assertContent "Wildcard index page"

        get "/wildcard/one"
        assertContent "Wildcard page one"

        get "/wildcard/two"
        assertContent "You do not have permission to access this page."

        // User test2 should have access to the wildcard "index" and
        // "two" actions, but not the "one" action.
        logout()
        login "test2", "test2", "/wildcard/index"
        assertContent "Wildcard index page"

        get "/wildcard/one"
        assertContent "You do not have permission to access this page."

        get "/wildcard/two"
        assertContent "Wildcard page two"

        logout()
    }
}
