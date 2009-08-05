package test

/**
 * Super class for all functional tests. Provides automated login and
 * logout, as well as verification for some common pages.
 */
abstract class FunctionalTestSupport extends functionaltestplugin.FunctionalTestCase {
    /**
     * Invokes a secure URL, using the given username and password to
     * get past the login screen.
     */
    def login(String uname, String passwd, String targetUrl = null) {
        if (targetUrl) {
            get targetUrl
        }
        form {
            username = uname
            password = passwd
            click "Sign in"
        }
    }

    /**
     * Invokes the "sign out" action.
     */
    def logout() {
        get "/auth/signOut"
    }

    def verifyLoginPage(String username, boolean inError = false) {
        assertTitle "Login"
        assertEquals username, byName("username").valueAttribute
        assertEquals "", byName("password").valueAttribute

        if (inError) {
            assertContentContains "Invalid username and/or password"
        }
        else {
            assertContentDoesNotContain "Invalid username and/or password"
        }
    }
}
