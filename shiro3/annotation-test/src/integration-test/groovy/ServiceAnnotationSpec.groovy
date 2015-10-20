import annotation.test.Application
import geb.spock.GebReportingSpec
import org.openqa.selenium.Cookie
import pages.*
import spock.lang.Unroll
import grails.test.mixin.integration.Integration

@Integration(applicationClass=Application)
class ServiceAnnotationSpec extends GebReportingSpec {

    @Unroll
    def "When not logged in service method annotations return [#value] for #theUrl @#select"() {
        when:
        go theUrl

        then:
        if (select) {
            $(select).text().contains(value)
        } else {
            $().text().contains(value)
        }

        where:
        theUrl            | select  | value
        'test/unsecured'  | ''      | 'Unsecured: one'
        'test/guest'      | ''      | 'Guest: two'
        'test/role'       | 'title' | 'Login'
        'test/permission' | 'title' | 'Login'
        'test/user'       | 'title' | 'Login'
    }

    @Unroll
    def "When logged in as #user/#password service method annotations returns [#value] for #theUrl @#select"() {
        given:
        go 'auth/signOut'

        when:
        to LoginPage

        then:
        at LoginPage

        when:
        loginForm.username = user
        loginForm.password = password
        signIn.click()

        and:
        go theUrl

        then:
        if (select) {
            $(select).text().contains(value)
        } else {
            $().text().contains(value)
        }

        where:
        user      | password   | theUrl            | select | value
        'admin'   | 'admin'    | 'test/unsecured'  | ''     | 'Unsecured: one'
        'admin'   | 'admin'    | 'test/guest'      | ''     | 'Guest: two'
        'admin'   | 'admin'    | 'test/user'       | ''     | 'User: three'
        'admin'   | 'admin'    | 'test/role'       | ''     | 'You do not have permission to access this page.'
        'admin'   | 'admin'    | 'test/permission' | ''     | 'You do not have permission to access this page.'
        'dilbert' | 'password' | 'test/role'       | ''     | 'Role: five'
        'dilbert' | 'password' | 'test/permission' | ''     | 'Permission: six'
    }

    def "When rememberMe is used and not authenticated this session"() {
        given:
        go 'auth/signOut'

        when:
        to LoginPage

        then:
        at LoginPage

        when:
        loginForm.username = 'admin'
        loginForm.password = 'admin'
        loginForm.rememberMe = true
        signIn.click()

        and:
        browser.driver.manage().deleteCookieNamed("JSESSIONID")
        go 'test/user'

        then:
        //has the rememberMe cookie set
        browser.driver.manage().cookies.find { Cookie cookie -> cookie.name == 'rememberMe' } != null
        $().text().contains('User: three')

        when:
        go 'test/authenticated'

        then:
        at LoginPage

        when:
        loginForm.username = 'admin'
        loginForm.password = 'admin'
        signIn.click()

        then:
        $().text().contains('Authenticated: four')
    }

    def "Wildcard permissions should work"() {
        given:
        go 'auth/signOut'

        when:
        go 'test/permission'

        then:
        at LoginPage

        when:
        //test1 user has permission book:*
        loginForm.username = 'test1'
        loginForm.password = 'test1'
        signIn.click()

        then:
        //should redirect
        $().text().contains('Permission: six')
    }

    def "Check secured Class requires authentication asks for login"() {
        given:
        go 'auth/signOut'

        when:
        go 'test/unrestricted'

        then:
        at LoginPage

        when:
        //test1 user has permission book:*
        loginForm.username = 'dilbert'
        loginForm.password = 'password'
        signIn.click()

        then:
        //should redirect
        $().text().contains('secure class: unrestricted')

        when:
        go 'test/administrator'

        then:
        $().text().contains('You do not have permission to access this page.')

    }

    def "Check secured Class requires authentication asks for login before refusing on role"() {
        given:
        go 'auth/signOut'

        when:
        go 'test/administrator'

        then:
        at LoginPage

        when:
        loginForm.username = 'dilbert'
        loginForm.password = 'password'
        signIn.click()

        then:
        $().text().contains('You do not have permission to access this page.')
    }

}