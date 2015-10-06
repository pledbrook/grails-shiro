import static groovyx.net.http.Method.GET

import groovyx.net.http.HTTPBuilder
import groovyx.net.http.HttpResponseException
import org.apache.commons.codec.binary.Base64
import spock.lang.Specification

/**
 * Functional tests for the Basic Authentication handling of the plugin
 * (and Shiro). Uses HttpBuilder to send requests to the server and parse
 * the responses.
 */
class BasicAuthenticationSpec extends Specification {
    def http = new HTTPBuilder("http://localhost:8080")

    def "Test basic authentication response"() {
        when: "I access the basic list page"
        http.get path: "/spring-filter/basic/list"

        then: "I get a 401 response"
        HttpResponseException e = thrown()
        e.statusCode == 401
    }

    def "Test basic authentication with empty password"() {
        when: "I access the basic list page"
        http.request GET, { req ->
            uri.path = "/spring-filter/basic/list" 
            headers.'Authorization' = createAuthorizationHeader("dilbert", "")
        }

        then: "I get a 401 response with the 'Shiro Plugin Test' realm"
        HttpResponseException e = thrown()
        e.statusCode == 401
        e.response.getFirstHeader("WWW-Authenticate").value ==~ /(?i)Basic realm="Shiro Plugin Test"/
    }

    def "Test basic authentication with invalid password"() {
        when: "I access the basic list page"
        http.request GET, { req ->
            uri.path = "/spring-filter/basic/list" 
            headers.'Authorization' = createAuthorizationHeader("dilbert", "teatimex")
        }

        then: "I get a 401 response with the 'Shiro Plugin Test' realm"
        HttpResponseException e = thrown()
        e.statusCode == 401
        e.response.getFirstHeader("WWW-Authenticate").value ==~ /(?i)Basic realm="Shiro Plugin Test"/
    }

    def "Test basic authentication with valid password"() {
        when: "I access the basic list page with a valid username and password"
        def page = http.get(path: "/spring-filter/basic/list", headers: [Authorization: createAuthorizationHeader("dilbert", "password")])

        then: "it's displayed"
        page.HEAD.TITLE.text() == "Basic List"
    }

    private String createAuthorizationHeader(String username, String password) {
        return "Basic " + new String(Base64.encodeBase64("${username}:${password}".toString().bytes))
    }
}
