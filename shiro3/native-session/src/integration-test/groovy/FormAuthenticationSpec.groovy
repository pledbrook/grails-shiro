import geb.spock.GebReportingSpec
import groovyx.net.http.HTTPBuilder
import pages.*
import spock.lang.Specification
import nativesession.Application
import grails.test.mixin.integration.Integration

@Integration(applicationClass=Application)
class FormAuthenticationSpec extends GebReportingSpec {
    def "Test form page requires authentication"() {
        when: "I access the form list page"
        go "form/list"
        page LoginPage

        then: "I'm redirected to the login page"
        at LoginPage
        !loginForm.username
    }

    def "Test authentication with no password"() {
        given:
        go "form/list"
        page LoginPage

        when: "I enter a username but no password"
        loginForm.username = "admin"
        signIn.click()

        then: "I'm redirected back to the login page with the username displayed but no password"
        at LoginPage
        loginForm.username == "admin"
        !loginForm.password
        message.text() == "Invalid username and/or password"
    }

    def "Test authentication with invalid password"() {
        given:
        go "form/list"
        page LoginPage

        when:
        loginForm.username = "admin"
        loginForm.password = "jkehfksdhg"
        signIn.click()

        then:
        at LoginPage
        loginForm.username == "admin"
        !loginForm.password
        message.text() == "Invalid username and/or password"
    }

    def "Test authentication with valid credentials"() {
        given:
        go "form/list"
        page LoginPage

        when:
        loginForm.username = "admin"
        loginForm.password = "admin"
        signIn.click()

        then:
        title == "Form List"
    }

    def "Test authentication with query parameters"() {
        given:
        def http = new HTTPBuilder("http://localhost:8080/")
        http.post path: "/auth/signIn", body: [username: "dilbert", password: "password"]
        http.post path: "/form/save", body: [name: "One"]
        http.post path: "/form/save", body: [name: "Two"]
        http.post path: "/form/save", body: [name: "Three"]
        http.post path: "/form/save", body: [name: "Four"]
        http.post path: "/form/save", body: [name: "Five"]
        go "auth/signOut"

        when: "I go to the form list page with some query parameters and log in"
        go "$FormListPage.url/?max=3&sort=name"

        then:
        at LoginPage

        when:
        loginForm.username = "dilbert"
        loginForm.password = "password"
        signIn.click()

        then: "the form list page is displayed with the items in the correct order"
        at FormListPage
        $("td")*.text() == [ "Five", "Four", "One" ]
    }

    /**
     * Logs into the application either via a target page that requires
     * authentication or by directly requesting the login page.
     */
    private login(username, password, targetPage = null, params = [:]) {
        if (targetPage) {
            go([*:params], targetPage.url)
            page LoginPage
        }
        else {
            to LoginPage
        }

        loginForm.username = username
        loginForm.password = password

        if (targetPage) signIn.click(targetPage)
        else signIn.click(HomePage)
    }

    private boolean verifyUnauthorised() {
        return $().text().contains("You do not have permission to access this page.")
    }
}
