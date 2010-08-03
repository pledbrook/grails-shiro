class MainFunctionalTests extends org.example.functional.TestSupport {
    void testServiceMethodAnnotations() {
        // Users can access unsecured and @RequiresGuest methods without
        // logging in.
        get "/test/unsecured"
        assertContentContains "Unsecured: one"

        get "/test/guest"
        assertContentContains "Guest: two"

        // @RequiresRoles & @RequiresPermissions annotations don't force
        // authentication on their own. They just throw UnauthorizedExceptions.
        get "/test/permission"
        verifyUnauthorised()

        get "/test/role"
        verifyUnauthorised()

        // @RequiresUser forces a login.
        get "/test/user"
        verifyLoginPage ""

        loginRemembered "admin", "admin"
        assertContentContains "User: three"

        // The 'admin' user is now authenticated but does not have the required
        // rights to access the methodFive() and methodSix() methods.
        get "/test/authenticated"
        assertContentContains "Authenticated: four"

        get "/test/role"
        verifyUnauthorised()

        get "/test/permission"
        verifyUnauthorised()

        // Grab the rememberMe cookie so we can pass it in after we've signed out.
        def rmCookie = cookies.find { it.name == "rememberMe" }.value
        logout()

        // A remembered user has access to methods protected by @RequiresUser,
        // but not ones protected by @RequiresAuthentication.
        get "/test/user", {
            headers["Cookie"] = "rememberMe=${rmCookie}"
        }
        assertContentContains "User: three"

        get "/test/authenticated", {
            headers["Cookie"] = "rememberMe=${rmCookie}"
        }
        verifyLoginPage ""

        // 'dilbert' has the 'User' role, but not the 'custom:read' permission.
        login "dilbert", "password"
        get "/test/role"
        assertContentContains "Role: five"

        get "/test/permission"
        verifyUnauthorised()

        logout()

        // authMethodFive() is not only protected by @RequiresRoles, but also
        // @RequiresAuthentication. That means calling the method forces a login.
        login "dilbert", "password", "/test/otherRole"
        assertContentContains "Role: five"

        logout()

        // 'test1' has access to userMethodSix() because he has the 'custom:read'
        // permission, but he doesn't have the 'User' role.
        loginRemembered "test1", "test1", "/test/otherPermission"
        assertContentContains "Permission: six"

        get "/test/role"
        verifyUnauthorised()

        // userMethodSix() is protected by @RequiresUser, so the remembered
        // 'test1' user should be able to access it.
        rmCookie = cookies.find { it.name == "rememberMe" }.value
        logout()
        get "/test/permission", {
            headers["Cookie"] = "rememberMe=${rmCookie}"
        }
        assertContentContains "Permission: six"

        get "/test/otherPermission", {
            headers["Cookie"] = "rememberMe=${rmCookie}"
        }
        assertContentContains "Permission: six"

        // This page still requires authentication, so a remembered user isn't
        // enough. Hence the login page we get.
        get "/test/otherRole"
        verifyLoginPage ""

        logout()
    }

    void testServiceClassAnnotations() {
        // All the target methods require an authenticated or remembered user
        // except for methodFour(), which has a @RequiresGuest annotation.
        get "/test/secureAdd"
        verifyLoginPage ""

        get "/test/secureOne"
        verifyLoginPage ""

        get "/test/secureTwo"
        verifyLoginPage ""

        get "/test/secureThree"
        verifyLoginPage ""

        // methodFour() doesn't require authentication, but it does require
        // a role that the current subject doesn't have.
        get "/test/secureFour"
        verifyUnauthorised()

        // All the target methods also require the 'User' role. So 'test1'
        // can log in, but not access any of the methods.
        login "test1", "test1", "/test/secureAdd"
        verifyUnauthorised()

        get "/test/secureOne"
        verifyUnauthorised()

        get "/test/secureTwo"
        verifyUnauthorised()

        get "/test/secureThree"
        verifyUnauthorised()

        // The 'admin' user doesn't have the 'User' role either, but it does
        // have the 'Aministrator' role.
        logout()
        login "admin", "admin", "/test/secureAdd"
        verifyUnauthorised()

        get "/test/secureTwo"
        assertContentContains "Secured: two"

        // 'dilbert', on the other hand, does have the 'User' role (but not
        // 'Administrator').
        logout()
        loginRemembered "dilbert", "password", "/test/secureAdd"
        assertContentContains "Secured: 5"

        get "/test/secureOne"
        assertContentContains "Secured: one"

        get "/test/secureTwo"
        verifyUnauthorised()

        get "/test/secureThree"
        assertContentContains "Secured: three"

        get "/test/secureFour"
        assertContentContains "Secured: four"

        // Grab the rememberMe cookie so we can pass it in after we've signed out.
        def rmCookie = cookies.find { it.name == "rememberMe" }.value
        logout()

        // A remembered user has access to methods protected by @RequiresUser,
        // but not ones protected by @RequiresAuthentication.
        get "/test/secureAdd", {
            headers["Cookie"] = "rememberMe=${rmCookie}"
        }
        assertContentContains "Secured: 5"

        get "/test/secureOne", {
            headers["Cookie"] = "rememberMe=${rmCookie}"
        }
        assertContentContains "Secured: one"

        get "/test/two", {
            headers["Cookie"] = "rememberMe=${rmCookie}"
        }
        verifyUnauthorised()

        get "/test/three", {
            headers["Cookie"] = "rememberMe=${rmCookie}"
        }
        verifyLoginPage ""

        logout()
    }

    void testControllerAnnotations() {
        // Any remembered or authenticated user has access to the unsecured page.
        get "/book/unsecured"
        verifyLoginPage ""
        
        login "admin", "admin"
        assertContentContains "Unsecured page"

        // 'admin' is authenticated but does not have any book permissions.
        get "/book/list"
        verifyUnauthorised()

        // Now try with 'dilbert', who has the "book:view" permission. This
        // allows him to access the 'list' and 'show' pages.
        logout()
        login "dilbert", "password", "/book/list"
        assertTitle "Book List"
        assertContentContains "Book List"

        get "/book/show/1"
        assertTitle "Show Book"
        assertContentContains "Show Book"

        // He doesn't have access to the edit page though.
        get "/book/edit/1"
        verifyUnauthorised()

        // User 'test1' has permission to access all the book pages. In this
        // case, we log in with the remember me feature active. Note that when
        // authenticated, 'test1' has access to the 'create' page.
        logout()
        loginRemembered "test1", "test1", "/book/create"
        assertTitle "Create Book"
        assertContentContains "Create Book"

        // Grab the rememberMe cookie so we can pass it in after we've
        // signed out.
        def rmCookie = cookies.find { it.name == "rememberMe" }.value
        logout()

        // The remembered user has access to all the pages still, except for
        // the 'create' page. That has an explicit @RequiresAuthentication
        // annotation.
        get "/book/show/1", {
            headers["Cookie"] = "rememberMe=${rmCookie}"
        }
        assertTitle "Show Book"
        assertContentContains "Show Book"

        get "/book/edit/1", {
            headers["Cookie"] = "rememberMe=${rmCookie}"
        }
        assertTitle "Edit Book"
        assertContentContains "Edit Book"

        get "/book/create", {
            headers["Cookie"] = "rememberMe=${rmCookie}"
        }
        verifyLoginPage ""
    }
}
