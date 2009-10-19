class FormAuthenticationTests extends org.example.functional.TestSupport {
    void testLoginPage() {
        get "/form/list"
        verifyLoginPage  ""

        login "admin", ""
        verifyLoginPage  "admin", true

        login "rodney", "sdkfjh"
        verifyLoginPage "rodney", true

        login "admin", "admin"
        assertTitle "Form List"

        logout()
    }

    void testRedirectedWithQueryParameters() {
        // Prepare some form items.
        login "dilbert", "password", "/form/list"
        post("/form/save") { name "One" }
        post("/form/save") { name "Two" }
        post("/form/save") { name "Three" }
        post("/form/save") { name "Four" }
        post("/form/save") { name "Five" }
        logout()

        // Now try to access the list page using some query parameters.
        get("/form/list") {
            max 3
            sort "name"
        }
        login "dilbert", "password"

        assertTitle "Form List"
        assertEquals 3, byXPath("count(//tbody/tr)").toInteger()
        assertEquals "Five", byXPath("//tbody/tr[1]/td[2]").textContent 
        assertEquals "Four", byXPath("//tbody/tr[2]/td[2]").textContent
        assertEquals "One", byXPath("//tbody/tr[3]/td[2]").textContent
    }
}
