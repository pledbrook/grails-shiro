import org.apache.commons.codec.binary.Base64

class BasicAuthenticationTests extends org.example.functional.TestSupport {
    void testLoginPage() {
        // First check that we can't access the given URL without
        // first authenticating.
        get "/basic/list"
        assertStatus 401
        assertHeader "WWW-Authenticate", "Basic realm=\"Shiro Plugin Test\""

        // Now pass the a valid username, but empty password with
        // the request.
        get("/basic/list") {
            headers["Authorization"] = createAuthorizationHeader("dilbert", "")
        }
        assertStatus 401
        assertHeader "WWW-Authenticate", "Basic realm=\"Shiro Plugin Test\""

        // Valid username, invalid password.
        get("/basic/list") {
            headers["Authorization"] = createAuthorizationHeader("dilbert", "teatimex")
        }
        assertStatus 401
        assertHeader "WWW-Authenticate", "Basic realm=\"Shiro Plugin Test\""

        // Valid username and valid password.
        get("/basic/list") {
            headers["Authorization"] = createAuthorizationHeader("dilbert", "password")
        }
        assertStatus 200
        assertTitle "Basic List"

        logout()
    }

    private String createAuthorizationHeader(String username, String password) {
        return "Basic " + new String(Base64.encodeBase64("${username}:${password}".toString().bytes))
    }
}
