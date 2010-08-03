package org.example.functional

abstract class TestSupport extends functionaltestplugin.FunctionalTestCase {
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
     * Invokes a secure URL, using the given username and password to
     * get past the login screen.
     */
    def loginRemembered(String uname, String passwd, String targetUrl = null) {
        if (targetUrl) {
            get targetUrl
        }
        form {
            username = uname
            password = passwd
            rememberMe = true
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

    def verifyUnauthorised() {
        assertContent "You do not have permission to access this page."
    }

    def verifyListSize(int size) {
        verifyText  'Book List'
        verifyXPath xpath:      ROW_COUNT_XPATH,
                    text:       size,
                    description:"$size row(s) of data expected"
    }

    def showFirstElementDetails() {
        ant.clickLink   '1', description:'go to detail view'
    }
}
